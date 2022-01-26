package com.weberbox.pifire.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.FragmentInfoBinding;
import com.weberbox.pifire.model.local.LicensesModel;
import com.weberbox.pifire.model.remote.InfoResponseModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.recycler.adapter.LicensesListAdapter;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding mBinding;
    private MainViewModel mMainViewModel;
    private Socket mSocket;
    private RelativeLayout mRootContainer;
    private TextView mServerVersion;
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
    private ArrayList<LicensesModel> mLicenses;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

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

        mCPUInfo = mBinding.systemCardView.cpuInfoText;
        mTempInfo = mBinding.systemCardView.tempInfoText;
        mNetworkInfo = mBinding.systemCardView.networkInfoText;

        mUptimeInfo = mBinding.uptimeCardView.uptimeInfoText;

        mGPIOOutAuger = mBinding.gpioCardView.gpioOutputAuger;
        mGPIOOutFan = mBinding.gpioCardView.gpioOutputFan;
        mGPIOOutIgniter = mBinding.gpioCardView.gpioOutputIgniter;
        mGPIOOutPower = mBinding.gpioCardView.gpioOutputPower;
        mGPIOInSelector = mBinding.gpioCardView.gpioInputSelector;

        mLicenseInfo = mBinding.licensesCardView.infoLicensesRecycler;

        mServerVersion = mBinding.serverVersionCardView.serverVersionText;

        LinearLayout appGitContainer = mBinding.appVersionCardView.appBuildGitContainer;
        TextView appVersion = mBinding.appVersionCardView.appVersionText;
        TextView appVersionCode = mBinding.appVersionCardView.appVersionCodeText;
        TextView appBuildType = mBinding.appVersionCardView.appBuildTypeText;
        TextView appBuildFlavor = mBinding.appVersionCardView.appBuildFlavorText;
        TextView appBuildDate = mBinding.appVersionCardView.appBuildDate;
        TextView appGitBranch = mBinding.appVersionCardView.appBuildGitBranch;
        TextView appGitRev = mBinding.appVersionCardView.appBuildGitRev;

        appVersion.setText(BuildConfig.VERSION_NAME);
        appVersionCode.setText(String.valueOf(BuildConfig.VERSION_CODE));
        appBuildType.setText(BuildConfig.BUILD_TYPE);
        appBuildFlavor.setText(BuildConfig.FLAVOR);

        appBuildDate.setText(TimeUtils.getFormattedDate(
                BuildConfig.BUILD_TIME, "MM-dd-yy HH:mm"));

        if (AppConfig.IS_DEV_BUILD) {
            appGitContainer.setVisibility(View.VISIBLE);
            appGitBranch.setText(BuildConfig.GIT_BRANCH);
            appGitRev.setText(BuildConfig.GIT_REV);
        }

        mSwipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                mSwipeRefresh.setRefreshing(false);
            }
        });

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getInfoData().observe(getViewLifecycleOwner(), infoData -> {
                mSwipeRefresh.setRefreshing(false);
                if (infoData != null && infoData.getLiveData() != null) {
                    if (infoData.getIsNewData()) {
                        FileUtils.executorSaveJSON(getActivity(), Constants.JSON_INFO,
                                infoData.getLiveData());
                    }
                    updateUIWithData(infoData.getLiveData());
                }
            });
        }

        mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
            toggleLoading(true);
            requestDataUpdate();
        });

        loadLicenses();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void showOfflineAlert() {
        if (getActivity() != null) {
            AlertUtils.createOfflineAlert(getActivity());
        }
    }

    private boolean socketConnected() {
        if (mSocket != null && mSocket.connected()) {
            return true;
        } else {
            showOfflineAlert();
            return false;
        }
    }

    private void requestDataUpdate() {
        if (mSocket != null && mSocket.connected()) {
            forceRefreshData();
        } else {
            loadStoredData();
        }
    }

    private void loadStoredData() {
        toggleLoading(true);
        FileUtils.executorLoadJSON(getActivity(), Constants.JSON_INFO, jsonString -> {
            if (jsonString != null && mMainViewModel != null) {
                mMainViewModel.setInfoData(jsonString, false);
            } else {
                toggleLoading(false);
            }
        });
    }

    private void forceRefreshData() {
        mSocket.emit(ServerConstants.REQUEST_INFO_DATA, (Ack) args -> {
            if (mMainViewModel != null && args.length > 0 && args[0] != null) {
                mMainViewModel.setInfoData(args[0].toString(), true);
            }
        });
    }

    private void toggleLoading(boolean show) {
        if (show && mSocket != null && mSocket.connected()) {
            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    private void updateUIWithData(String responseData) {

        try {

            InfoResponseModel infoResponseModel = InfoResponseModel.parseJSON(responseData);

            List<String> cpuInfo = infoResponseModel.getCpuInfo();
            String cpuTemp = infoResponseModel.getTemp();
            List<String> networkInfo = infoResponseModel.getIfConfig();
            String upTime = infoResponseModel.getUpTime();
            String auger = infoResponseModel.getOutPins().getAuger();
            String fan = infoResponseModel.getOutPins().getFan();
            String igniter = infoResponseModel.getOutPins().getIgniter();
            String power = infoResponseModel.getOutPins().getPower();
            String selector = infoResponseModel.getInPins().getSelector();
            String version = infoResponseModel.getServerVersion();

            StringBuilder cpuString = new StringBuilder();
            for (String cpu : cpuInfo) {
                cpuString.append(cpu).append("\n");
            }

            StringBuilder networkString = new StringBuilder();
            for (String network : networkInfo) {
                networkString.append(network.trim()).append("\n");
            }

            TransitionManager.beginDelayedTransition(mRootContainer, new Fade(Fade.IN));

            mCPUInfo.setText(cpuString);
            mNetworkInfo.setText(networkString);
            mUptimeInfo.setText(upTime);
            mTempInfo.setText(cpuTemp);
            mGPIOOutAuger.setText(auger);
            mGPIOOutFan.setText(fan);
            mGPIOOutIgniter.setText(igniter);
            mGPIOOutPower.setText(power);
            mGPIOInSelector.setText(selector);
            mServerVersion.setText(version);

        } catch (NullPointerException e) {
            Timber.w(e, "Info Response Error");
            AlertUtils.createErrorAlert(getActivity(), R.string.json_error_info, false);
        }

        toggleLoading(false);
    }

    private void loadLicenses() {
        mLicenses.clear();

        if (getActivity() != null) {
            FileUtils.executorLoadRawJson(getActivity(), R.raw.licences, jsonString -> {
                try {

                    if (jsonString != null) {
                        JSONObject rootObject = new JSONObject(jsonString);
                        JSONArray array = rootObject.getJSONArray(Constants.LICENSES_LIST);

                        for (int i = 0; i < array.length(); i++) {
                            JSONArray innerArray = array.getJSONArray(i);

                            LicensesModel project = new LicensesModel(
                                    innerArray.getString(0),
                                    innerArray.getString(1)
                            );
                            mLicenses.add(project);
                        }

                        LicensesListAdapter licensesListAdapter =
                                new LicensesListAdapter(mLicenses);

                        mLicenseInfo.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mLicenseInfo.setItemAnimator(new DefaultItemAnimator());
                        mLicenseInfo.setAdapter(licensesListAdapter);

                    }

                } catch (Exception e) {
                    Timber.w(e, "Licences JSON Error");
                    AlertUtils.createErrorAlert(getActivity(), R.string.json_error_info, false);
                }
            });
        }
    }
}
