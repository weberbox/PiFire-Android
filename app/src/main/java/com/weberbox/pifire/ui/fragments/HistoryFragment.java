package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentHistoryBinding;
import com.weberbox.pifire.model.remote.HistoryDataModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.utils.LineChartXAxisValueFormatter;
import com.weberbox.pifire.ui.views.CustomMarkerView;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Socket;
import timber.log.Timber;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private MainViewModel mainViewModel;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar loadingBar;
    private LineChart lineChart;
    private TextView refreshButton;
    private Handler handler;
    private Socket socket;

    private boolean started = false;
    private boolean isLoading = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler();

        lineChart = binding.historyLayout.historyLinechart;
        loadingBar = binding.loadingProgressbar;
        swipeRefresh = binding.historyPullRefresh;

        swipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        refreshButton = binding.historyRefreshButton;
        if (getActivity() != null) {
            if (Prefs.getBoolean(getString(R.string.prefs_history_refresh_app), true)) {
                refreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                        R.drawable.bg_ripple_refresh_on));
                refreshButton.setText(R.string.on);
            } else {
                refreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                        R.drawable.bg_ripple_refresh_off));
                refreshButton.setText(R.string.off);
            }
        }

        refreshButton.setOnClickListener(view1 -> {
            if (getActivity() != null) {
                if (Prefs.getBoolean(getString(R.string.prefs_history_refresh_app), true)) {
                    refreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                            R.drawable.bg_ripple_refresh_off));
                    refreshButton.setText(R.string.off);
                    Prefs.putBoolean(getString(R.string.prefs_history_refresh_app), false);
                    stopRefresh();
                } else {
                    refreshButton.setBackground(ContextCompat.getDrawable(getActivity(),
                            R.drawable.bg_ripple_refresh_on));
                    refreshButton.setText(R.string.on);
                    Prefs.putBoolean(getString(R.string.prefs_history_refresh_app), true);
                    if (!started) {
                        startRefresh();
                    }
                }
            }
        });

        ImageView deleteButton = binding.historyDeleteButton;
        deleteButton.setOnClickListener(view2 -> {
            if (socketConnected()) {
                BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                        .setTitle(getString(R.string.dialog_confirm_action))
                        .setMessage(getString(R.string.history_delete_content))
                        .setAutoDismiss(true)
                        .setNegativeButton(getString(R.string.cancel),
                                (dialogInterface, which) -> {
                                })
                        .setPositiveButtonWithColor(getString(R.string.delete),
                                R.color.dialog_positive_button_color_red,
                                (dialogInterface, which) -> ServerControl.sendHistoryDelete(socket,
                                        this::processPostResponse))
                        .build();
                dialog.show();
            }
        });

        if (getActivity() != null) {
            mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mainViewModel.getHistoryData().observe(getViewLifecycleOwner(), historyData -> {
                swipeRefresh.setRefreshing(false);
                if (historyData != null && historyData.getLiveData() != null) {
                    if (historyData.getIsNewData()) {
                        FileUtils.executorSaveJSON(getActivity(), Constants.JSON_HISTORY,
                                historyData.getLiveData());
                    }
                    updateUIWithData(historyData.getLiveData());
                }
            });

            mainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                toggleLoading(true);
                requestDataUpdate();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
        requestDataUpdate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        socket = null;
        stopRefresh();
    }

    private void showOfflineAlert() {
        if (getActivity() != null) {
            AlertUtils.createOfflineAlert(getActivity());
        }
    }

    private boolean socketConnected() {
        if (socket != null && socket.connected()) {
            return true;
        } else {
            showOfflineAlert();
            return false;
        }
    }

    private void requestDataUpdate() {
        if (socket != null && socket.connected()) {
            checkAutoRefreshGraph();
        } else {
            loadStoredData();
        }
    }

    private void loadStoredData() {
        FileUtils.executorLoadJSON(getActivity(), Constants.JSON_HISTORY, jsonString -> {
            if (jsonString != null && mainViewModel != null) {
                mainViewModel.setHistoryData(jsonString, false);
            } else {
                toggleLoading(false);
            }
        });
    }

    private void forceRefreshData() {
        isLoading = true;
        ServerControl.historyGetEmit(socket, response -> {
            if (mainViewModel != null) {
                mainViewModel.setHistoryData(response, true);
            }
        });
    }

    private void checkAutoRefreshGraph() {
        if (Prefs.getBoolean(getString(R.string.prefs_history_refresh_app),
                getResources().getBoolean(R.bool.def_history_refresh_enabled_app))) {
            if (!started) {
                if (!isLoading) {
                    forceRefreshData();
                }
                startRefresh();
            }
        } else {
            forceRefreshData();
        }
    }

    private void toggleLoading(boolean show) {
        if (show && socket != null && socket.connected()) {
            loadingBar.setVisibility(View.VISIBLE);
        } else {
            loadingBar.setVisibility(View.GONE);
        }
    }

    private void stopRefresh() {
        started = false;
        handler.removeCallbacks(runnable);
    }

    private void startRefresh() {
        if (isAdded()) {
            started = true;
            int customAmount = Integer.parseInt(Prefs.getString(
                    getString(R.string.prefs_history_refresh_interval_app),
                    getString(R.string.def_history_refresh_time_app)));
            handler.postDelayed(runnable, customAmount);
        }
    }

    private final Runnable runnable = () -> {
        forceRefreshData();
        if (started) {
            startRefresh();
        }
    };

    private void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResult().equals("error")) {
            requireActivity().runOnUiThread(() ->
                    AlertUtils.createErrorAlert(requireActivity(),
                            result.getMessage(), false));
        }
    }

    private void updateUIWithData(String responseData) {
        isLoading = false;

        try {

            HistoryDataModel historyDataModel = HistoryDataModel.parseJSON(responseData);

            List<String> timeList = historyDataModel.getLabelTimeList();
            List<Double> grillSetTemp = historyDataModel.getGrillSetTempList();
            List<Double> grillTemp = historyDataModel.getGrillTempList();
            List<Double> probe1SetTemp = historyDataModel.getProbe1SetTempList();
            List<Double> probe2SetTemp = historyDataModel.getProbe2SetTempList();
            List<Double> probe1Temp = historyDataModel.getProbe1TempList();
            List<Double> probe2Temp = historyDataModel.getProbe2TempList();

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

            LineDataSet probe1SetD = new LineDataSet(probe1STArray, getString(R.string.history_probe1_set));
            probe1SetD.setLineWidth(3.0f);
            probe1SetD.setDrawCircles(false);

            LineDataSet probe1TempD = new LineDataSet(probe1TArray, getString(R.string.history_probe1_temp));
            probe1TempD.setLineWidth(3.0f);
            probe1TempD.setDrawCircles(false);

            LineDataSet probe2SetD = new LineDataSet(probe2STArray, getString(R.string.history_probe2_set));
            probe2SetD.setLineWidth(3.0f);
            probe2SetD.setDrawCircles(false);

            LineDataSet probe2TempD = new LineDataSet(probe2TArray, getString(R.string.history_probe2_temp));
            probe2TempD.setLineWidth(3.0f);
            probe2TempD.setDrawCircles(false);

            grillSetD.setColor(colors[0]);
            grillTempD.setColor(colors[1]);
            probe1SetD.setColor(colors[2]);
            probe1TempD.setColor(colors[3]);
            probe2SetD.setColor(colors[4]);
            probe2TempD.setColor(colors[5]);

            dataSets.add(grillTempD);
            dataSets.add(grillSetD);
            dataSets.add(probe1TempD);
            dataSets.add(probe1SetD);
            dataSets.add(probe2TempD);
            dataSets.add(probe2SetD);

            lineChart.setGridBackgroundColor(Color.WHITE);
            lineChart.getXAxis().setTextColor(Color.WHITE);
            lineChart.getXAxis().setLabelRotationAngle(-45);
            //mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
            lineChart.getAxisLeft().setTextColor(Color.WHITE);
            lineChart.getLegend().setTextColor(Color.WHITE);
            lineChart.getLegend().setWordWrapEnabled(true);
            lineChart.getDescription().setEnabled(false);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.setDrawMarkers(true);
            lineChart.setMarker(markerView(requireActivity()));

            LineData data = new LineData(dataSets);
            lineChart.setData(data);
            lineChart.getLineData().setDrawValues(false);
            lineChart.invalidate();

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w("JSON Error %s", e.getMessage());
            AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                    getString(R.string.menu_history)), false);
        }

        toggleLoading(false);
    }

    public CustomMarkerView markerView(Context context) {
        CustomMarkerView mv = new CustomMarkerView(context, R.layout.layout_custom_marker,
                16, Color.WHITE);
        mv.setOffset((float) -mv.getWidth() / 2, -mv.getHeight() - 25);
        return mv;
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