package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.databinding.FragmentHistoryBinding;
import com.weberbox.pifire.interfaces.HistoryCallbackInterface;
import com.weberbox.pifire.model.HistoryModel;
import com.weberbox.pifire.ui.dialogs.HistoryDeleteDialog;
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.LineChartXAxisValueFormatter;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.Log;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class HistoryFragment extends Fragment implements HistoryCallbackInterface {
    private static final String TAG = HistoryFragment.class.getSimpleName();

    private FragmentHistoryBinding mBinding;
    private MainViewModel mMainViewModel;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mLoadingBar;
    private LineChart mLineChart;
    private TextView mRefreshButton;
    private ImageView mDeleteButton;
    private Handler mHandler;
    private Socket mSocket;

    private boolean mStarted = false;

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
        mLoadingBar = mBinding.historyLayout.loadingProgressbar;
        mSwipeRefresh = mBinding.historyPullRefresh;


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

        mRefreshButton = mBinding.historyLayout.historyRefreshButton;
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

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        mDeleteButton = mBinding.historyLayout.historyDeleteButton;
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSocket.connected()) {
                    if (getActivity() != null) {
                        HistoryDeleteDialog historyActionDialog = new HistoryDeleteDialog(getActivity(),
                                HistoryFragment.this);
                        historyActionDialog.showDialog();
                    }
                } else {
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getHistoryData().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String response_data) {
                    mSwipeRefresh.setRefreshing(false);
                    if (getActivity() != null) {
                        FileUtils.saveJSONFile(getActivity(), Constants.JSON_HISTORY, response_data);
                    }
                    if (response_data != null) {
                        updateUIWithData(response_data);
                    }
                }
            });

            mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean enabled) {
                    autoRefreshGraph();
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefresh();
    }

    @Override
    public void onHistoryDelete() {
        if (mSocket != null) {
            GrillControl.setHistoryDelete(mSocket);
        }
    }

    private void autoRefreshGraph() {
        if (mSocket != null && mSocket.connected()) {
            mLoadingBar.setVisibility(View.VISIBLE);
            if (Prefs.getBoolean(getString(R.string.prefs_history_refresh_app), Boolean.parseBoolean(getString(R.string.def_history_refresh_enabled_app)))) {
                if (!mStarted) {
                    startRefresh();
                }
            } else {
                checkStoredData();
            }
        } else {
            if (getActivity() != null) {
                String jsonData = FileUtils.loadJSONFile(getActivity(), Constants.JSON_HISTORY);
                if (jsonData != null && mMainViewModel != null) {
                    mMainViewModel.setHistoryData(jsonData);
                }
            }
        }
    }

    private void checkStoredData() {
        if (mMainViewModel.getHistoryData().getValue() == null) {
            requestListData();
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    private void requestListData() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(ServerConstants.REQUEST_HISTORY_DATA, (Ack) args -> {
                if (mMainViewModel != null) {
                    mMainViewModel.setHistoryData(args[0].toString());
                }
            });
        }
    }

    public void stopRefresh() {
        mStarted = false;
        mHandler.removeCallbacks(runnable);
    }

    public void startRefresh() {
        mStarted = true;
        int customAmount = Integer.parseInt(Prefs.getString(
                getString(R.string.prefs_history_refresh_interval_app), "3000"));
        mHandler.postDelayed(runnable, customAmount);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            requestListData();
            if (mStarted) {
                startRefresh();
            }
        }
    };

    private void updateUIWithData(String response_data) {
        mLoadingBar.setVisibility(View.GONE);

        try {

            HistoryModel historyModel = HistoryModel.parseJSON(response_data);

            List<String> timeList = historyModel.getLabelTimeList();
            List<Integer> grillSetTemp = historyModel.getGrillSetTempList();
            List<Integer> grillTemp = historyModel.getGrillTempList();
            List<Integer> probe1SetTemp = historyModel.getProbe1SetTempList();
            List<Integer> probe2SetTemp = historyModel.getProbe2SetTempList();
            List<Integer> probe1Temp = historyModel.getProbe1TempList();
            List<Integer> probe2Temp = historyModel.getProbe2TempList();

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
                    Log.e(TAG, "Failed to parse time " + e.toString());
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
                probe1TArray.add(new Entry(time.get(i), probe1Temp.get(i)));
                probe2STArray.add(new Entry(time.get(i), probe2SetTemp.get(i)));
                probe2TArray.add(new Entry(time.get(i), probe2Temp.get(i)));
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
            Log.e(TAG, "JSON Error " + e.getMessage());
            if (getActivity() != null) {
                showSnackBarMessage(getActivity());
            }
        }
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

    private void showSnackBarMessage(Activity activity) {
        Snackbar snack = Snackbar.make(mBinding.getRoot(), R.string.json_error_history, Snackbar.LENGTH_LONG);
        snack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
        snack.setTextColor(activity.getColor(R.color.colorWhite));
        snack.show();
    }
}