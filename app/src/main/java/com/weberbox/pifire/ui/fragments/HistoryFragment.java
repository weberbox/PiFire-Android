package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.databinding.FragmentHistoryBinding;
import com.weberbox.pifire.model.remote.HistoryModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.ui.dialogs.DeleteActionDialog;
import com.weberbox.pifire.ui.utils.LineChartXAxisValueFormatter;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding mBinding;
    private MainViewModel mMainViewModel;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mLoadingBar;
    private LineChart mLineChart;
    private TextView mRefreshButton;
    private Handler mHandler;
    private Socket mSocket;

    private boolean mStarted = false;
    private boolean mIsLoading = false;

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
        mBinding = FragmentHistoryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler();

        mLineChart = mBinding.historyLayout.historyLinechart;
        mLoadingBar = mBinding.loadingProgressbar;
        mSwipeRefresh = mBinding.historyPullRefresh;

        mSwipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                mSwipeRefresh.setRefreshing(false);
            }
        });

        mRefreshButton = mBinding.historyRefreshButton;
        if (getActivity() != null) {
            if (Prefs.getBoolean(getString(R.string.prefs_history_refresh_app), true)) {
                mRefreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                        R.drawable.bg_ripple_refresh_on));
                mRefreshButton.setText(R.string.on);
            } else {
                mRefreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                        R.drawable.bg_ripple_refresh_off));
                mRefreshButton.setText(R.string.off);
            }
        }

        mRefreshButton.setOnClickListener(view1 -> {
            if (getActivity() != null) {
                if (Prefs.getBoolean(getString(R.string.prefs_history_refresh_app), true)) {
                    mRefreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                            R.drawable.bg_ripple_refresh_off));
                    mRefreshButton.setText(R.string.off);
                    Prefs.putBoolean(getString(R.string.prefs_history_refresh_app), false);
                    stopRefresh();
                } else {
                    mRefreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                            R.drawable.bg_ripple_refresh_on));
                    mRefreshButton.setText(R.string.on);
                    Prefs.putBoolean(getString(R.string.prefs_history_refresh_app), true);
                    if (!mStarted) {
                        startRefresh();
                    }
                }
            }
        });

        ImageView deleteButton = mBinding.historyDeleteButton;
        deleteButton.setOnClickListener(view12 -> {
            if (socketConnected()) {
                if (getActivity() != null) {
                    DeleteActionDialog historyActionDialog = new DeleteActionDialog(getActivity(),
                            getString(R.string.history_delete_content), () -> {
                                if (mSocket != null) {
                                    GrillControl.setHistoryDelete(mSocket);
                                }
                            });
                    historyActionDialog.showDialog();
                }
            }
        });

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getHistoryData().observe(getViewLifecycleOwner(), historyData -> {
                mSwipeRefresh.setRefreshing(false);
                if (historyData != null && historyData.getLiveData() != null) {
                    if (historyData.getIsNewData()) {
                        FileUtils.executorSaveJSON(getActivity(), Constants.JSON_HISTORY,
                                historyData.getLiveData());
                    }
                    updateUIWithData(historyData.getLiveData());
                }
            });

            mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                toggleLoading(true);
                requestDataUpdate();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
        stopRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestDataUpdate();
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
            checkAutoRefreshGraph();
        } else {
            loadStoredData();
        }
    }

    private void loadStoredData() {
        FileUtils.executorLoadJSON(getActivity(), Constants.JSON_HISTORY, jsonString -> {
            if (jsonString != null && mMainViewModel != null) {
                mMainViewModel.setHistoryData(jsonString, false);
            } else {
                toggleLoading(false);
            }
        });
    }

    private void forceRefreshData() {
        if (mSocket != null && mSocket.connected()) {
            mIsLoading = true;
            mSocket.emit(ServerConstants.REQUEST_HISTORY_DATA, (Ack) args -> {
                if (mMainViewModel != null && args.length > 0 && args[0] != null) {
                    mMainViewModel.setHistoryData(args[0].toString(), true);
                }
            });
        }
    }

    private void checkAutoRefreshGraph() {
        if (Prefs.getBoolean(getString(R.string.prefs_history_refresh_app),
                getResources().getBoolean(R.bool.def_history_refresh_enabled_app))) {
            if (!mStarted) {
                if (!mIsLoading) {
                    forceRefreshData();
                }
                startRefresh();
            }
        } else {
            forceRefreshData();
        }
    }

    private void toggleLoading(boolean show) {
        if (show && mSocket != null && mSocket.connected()) {
            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    private void stopRefresh() {
        mStarted = false;
        mHandler.removeCallbacks(runnable);
    }

    private void startRefresh() {
        mStarted = true;
        int customAmount = Integer.parseInt(Prefs.getString(
                getString(R.string.prefs_history_refresh_interval_app),
                getString(R.string.def_history_refresh_time_app)));
        mHandler.postDelayed(runnable, customAmount);
    }

    private final Runnable runnable = () -> {
        forceRefreshData();
        if (mStarted) {
            startRefresh();
        }
    };

    private void updateUIWithData(String responseData) {
        mIsLoading = false;

        try {

            HistoryModel historyModel = HistoryModel.parseJSON(responseData);

            List<String> timeList = historyModel.getLabelTimeList();
            List<Double> grillSetTemp = historyModel.getGrillSetTempList();
            List<Double> grillTemp = historyModel.getGrillTempList();
            List<Double> probe1SetTemp = historyModel.getProbe1SetTempList();
            List<Double> probe2SetTemp = historyModel.getProbe2SetTempList();
            List<Double> probe1Temp = historyModel.getProbe1TempList();
            List<Double> probe2Temp = historyModel.getProbe2TempList();

            ArrayList<Float> time = new ArrayList<>();

            for (int i = 0; i < timeList.size(); i++) {
                @SuppressLint("SimpleDateFormat")
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date date = dateFormat.parse(timeList.get(i));
                    float seconds = (float) date.getTime() / 1000L;
                    time.add(seconds);
                } catch (ParseException e) {
                    Timber.w(e, "Failed to parse time");
                    return;
                }
            }

            ArrayList<Entry> grillSTArray = new ArrayList<>();
            ArrayList<Entry> grillTArray = new ArrayList<>();
            ArrayList<Entry> probe1STArray = new ArrayList<>();
            ArrayList<Entry> probe1TArray = new ArrayList<>();
            ArrayList<Entry> probe2STArray = new ArrayList<>();
            ArrayList<Entry> probe2TArray = new ArrayList<>();

            for (int i = 0; i < time.size(); i++) {
                grillSTArray.add(new Entry(time.get(i), grillSetTemp.get(i).floatValue()));
                grillTArray.add(new Entry(time.get(i), grillTemp.get(i).floatValue()));
                probe1STArray.add(new Entry(time.get(i), probe1SetTemp.get(i).floatValue()));
                probe1TArray.add(new Entry(time.get(i), probe1Temp.get(i).floatValue()));
                probe2STArray.add(new Entry(time.get(i), probe2SetTemp.get(i).floatValue()));
                probe2TArray.add(new Entry(time.get(i), probe2Temp.get(i).floatValue()));
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

            LineDataSet grillSetD = new LineDataSet(grillSTArray, getString(R.string.history_grill_set));
            grillSetD.setLineWidth(3.0f);
            grillSetD.setDrawCircles(false);

            LineDataSet grillTempD = new LineDataSet(grillTArray, getString(R.string.history_grill_temp));
            grillTempD.setLineWidth(3.0f);
            grillTempD.setDrawCircles(false);

            LineDataSet Probe1SetD = new LineDataSet(probe1STArray, getString(R.string.history_probe1_set));
            Probe1SetD.setLineWidth(3.0f);
            Probe1SetD.setDrawCircles(false);

            LineDataSet Probe1TempD = new LineDataSet(probe1TArray, getString(R.string.history_probe1_temp));
            Probe1TempD.setLineWidth(3.0f);
            Probe1TempD.setDrawCircles(false);

            LineDataSet Probe2SetD = new LineDataSet(probe2STArray, getString(R.string.history_probe2_set));
            Probe2SetD.setLineWidth(3.0f);
            Probe2SetD.setDrawCircles(false);

            LineDataSet Probe2TempD = new LineDataSet(probe2TArray, getString(R.string.history_probe2_temp));
            Probe2TempD.setLineWidth(3.0f);
            Probe2TempD.setDrawCircles(false);

            grillSetD.setColor(colors[0]);
            grillSetD.setCircleColor(colors[0]);
            grillTempD.setColor(colors[1]);
            grillTempD.setCircleColor(colors[1]);
            Probe1SetD.setColor(colors[2]);
            Probe1SetD.setCircleColor(colors[2]);
            Probe1TempD.setColor(colors[3]);
            Probe1TempD.setCircleColor(colors[3]);
            Probe2SetD.setColor(colors[4]);
            Probe2SetD.setCircleColor(colors[4]);
            Probe2TempD.setColor(colors[5]);
            Probe2TempD.setCircleColor(colors[5]);
            dataSets.add(grillTempD);
            dataSets.add(grillSetD);
            dataSets.add(Probe1TempD);
            dataSets.add(Probe1SetD);
            dataSets.add(Probe2TempD);
            dataSets.add(Probe2SetD);

            mLineChart.setGridBackgroundColor(Color.WHITE);
            mLineChart.getXAxis().setTextColor(Color.WHITE);
            mLineChart.getXAxis().setLabelRotationAngle(-45);
            //mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            mLineChart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
            mLineChart.getAxisLeft().setTextColor(Color.WHITE);
            mLineChart.getLegend().setTextColor(Color.WHITE);
            mLineChart.getLegend().setWordWrapEnabled(true);
            mLineChart.getDescription().setEnabled(false);
            mLineChart.getAxisRight().setEnabled(false);

            LineData data = new LineData(dataSets);
            mLineChart.setData(data);
            mLineChart.getLineData().setDrawValues(false);
            mLineChart.invalidate();

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w("JSON Error %s", e.getMessage());
            AlertUtils.createErrorAlert(getActivity(), R.string.json_error_history, false);
        }

        toggleLoading(false);
    }

    private final int[] colors = new int[]{
            GRILL_COLORS[0],
            GRILL_COLORS[1],
            GRILL_COLORS[2],
            GRILL_COLORS[3],
            GRILL_COLORS[4],
            GRILL_COLORS[5],
    };

    public static final int[] GRILL_COLORS = {
            Color.rgb(0, 0, 127), Color.rgb(0, 0, 255),
            Color.rgb(127, 0, 0), Color.rgb(255, 0, 0),
            Color.rgb(0, 127, 0), Color.rgb(0, 255, 0)
    };
}