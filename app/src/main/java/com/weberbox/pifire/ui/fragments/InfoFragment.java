package com.weberbox.pifire.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.FragmentInfoBinding;
import com.weberbox.pifire.interfaces.LicensesCallbackInterface;
import com.weberbox.pifire.model.InfoResponseModel;
import com.weberbox.pifire.recycler.adapter.LicensesListAdapter;
import com.weberbox.pifire.recycler.viewmodel.LicensesViewModel;
import com.weberbox.pifire.ui.model.InfoViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.FadeTransition;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class InfoFragment extends Fragment implements LicensesCallbackInterface {
    private static final String TAG = InfoFragment.class.getSimpleName();

    private FragmentInfoBinding mBinding;
    private InfoViewModel mInfoViewModel;
    private Socket mSocket;
    private RelativeLayout mRootContainer;
    private TextView mCPUInfo;
    private TextView mTempInfo;
    private TextView mNetworkInfo;
    private TextView mUptimeInfo;
    private TextView mGPIOOutAuger;
    private TextView mGPIOOutFan;
    private TextView mGPIOOutIgniter;
    private TextView mGPIOOutPower;
    private TextView mGPIOInSelector;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mLoadingBar;
    private RecyclerView mLicenseInfo;
    private LicensesListAdapter mLicensesListAdapter;
    private ArrayList<LicensesViewModel> mLicenses;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentInfoBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLicenses = new ArrayList<>();

        mRootContainer = mBinding.infoRootContainer;
        mSwipeRefresh = mBinding.infoPullRefresh;
        mLoadingBar = mBinding.loadingProgressbar;

        mCPUInfo = mBinding.cpuInfoText;
        mTempInfo = mBinding.tempInfoText;
        mNetworkInfo = mBinding.networkInfoText;
        mUptimeInfo = mBinding.uptimeInfoText;

        mGPIOOutAuger = mBinding.gpioOutputAuger;
        mGPIOOutFan = mBinding.gpioOutputFan;
        mGPIOOutIgniter = mBinding.gpioOutputIgniter;
        mGPIOOutPower = mBinding.gpioOutputPower;
        mGPIOInSelector = mBinding.gpioInputSelector;

        mLicenseInfo = mBinding.infoLicensesRecycler;

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSocket != null && mSocket.connected()) {
                    requestListData();
                } else {
                    mSwipeRefresh.setRefreshing(false);
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        if (getActivity() != null) {
            mInfoViewModel = new ViewModelProvider(getActivity()).get(InfoViewModel.class);
            mInfoViewModel.getInfoData().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String response_data) {
                    mSwipeRefresh.setRefreshing(false);
                    if (response_data != null) {
                        if (getActivity() != null) {
                            FileUtils.saveJSONFile(getActivity(), Constants.JSON_INFO, response_data);
                        }
                        updateUIWithData(response_data);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void checkStoredData() {
        if (mInfoViewModel.getInfoData().getValue() == null) {
            requestListData();
            toggleLoading(true);
        } else {
            toggleLoading(false);
        }
    }

    private void requestListData() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(ServerConstants.REQUEST_INFO_DATA, (Ack) args -> {
                if (mInfoViewModel != null) {
                    mInfoViewModel.setInfoData(args[0].toString());
                }
            });
        } else {
            if (getActivity() != null) {
                String jsonData = FileUtils.loadJSONFile(getActivity(), Constants.JSON_INFO);
                if (jsonData != null && mInfoViewModel != null) {
                    mInfoViewModel.setInfoData(jsonData);
                }
            }
        }
    }

    private void toggleLoading(boolean show) {
        if (show && mSocket != null && mSocket.connected()) {
            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
            checkStoredData();
        }
    }

    private void updateUIWithData(String response_data) {
        mLicenses.clear();

        try {

            InfoResponseModel infoResponseModel = InfoResponseModel.parseJSON(response_data);

            List<String> cpuInfo = infoResponseModel.getCpuInfo();
            String cpuTemp = infoResponseModel.getTemp();
            List<String> networkInfo = infoResponseModel.getIfConfig();
            String upTime = infoResponseModel.getUpTime();
            String auger = infoResponseModel.getOutPins().getAuger();
            String fan = infoResponseModel.getOutPins().getFan();
            String igniter = infoResponseModel.getOutPins().getIgniter();
            String power = infoResponseModel.getOutPins().getPower();
            String selector = infoResponseModel.getInPins().getSelector();

            StringBuilder cpuString = new StringBuilder();
            for(String cpu: cpuInfo) {
                cpuString.append(cpu).append("\n");
            }

            StringBuilder networkString = new StringBuilder();
            for(String network: networkInfo) {
                networkString.append(network.trim()).append("\n");
            }

            TransitionManager.beginDelayedTransition(mRootContainer, new FadeTransition());

            mCPUInfo.setText(cpuString);
            mNetworkInfo.setText(networkString);
            mUptimeInfo.setText(upTime);
            mTempInfo.setText(cpuTemp);
            mGPIOOutAuger.setText(auger);
            mGPIOOutFan.setText(fan);
            mGPIOOutIgniter.setText(igniter);
            mGPIOOutPower.setText(power);
            mGPIOInSelector.setText(selector);

            if (getActivity() != null) {
                String json = FileUtils.readRawJSONFile(getActivity(), R.raw.licences);

                if (json != null) {
                    JSONObject rootObject = new JSONObject(json);
                    JSONArray array = rootObject.getJSONArray(Constants.LICENSES_LIST);

                    for (int i = 0; i < array.length(); i++) {
                        JSONArray innerArray = array.getJSONArray(i);

                        LicensesViewModel project = new LicensesViewModel(
                                innerArray.getString(0),
                                innerArray.getString(1)
                        );
                        mLicenses.add(project);
                    }

                    mLicensesListAdapter = new LicensesListAdapter(mLicenses, this);

                    mLicenseInfo.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mLicenseInfo.setItemAnimator(new DefaultItemAnimator());
                    mLicenseInfo.setAdapter(mLicensesListAdapter);

                }
            }

            toggleLoading(false);

        } catch (JSONException | IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
            if (getActivity() != null) {
                showSnackBarMessage(getActivity());
            }
        }
    }

    private void showSnackBarMessage(Activity activity) {
        Snackbar snack = Snackbar.make(mBinding.getRoot(), R.string.json_error_events, Snackbar.LENGTH_LONG);
        snack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
        snack.setTextColor(activity.getColor(R.color.colorWhite));
        snack.show();
    }

    @Override
    public void licenseClick(String url) {
        if (getActivity() != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            getActivity().startActivity(i);
        }
    }
}
