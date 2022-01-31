package com.weberbox.pifire.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentDashboardBinding;
import com.weberbox.pifire.interfaces.DashboardCallback;
import com.weberbox.pifire.model.remote.DashDataModel;
import com.weberbox.pifire.model.remote.DashDataModel.NotifyData;
import com.weberbox.pifire.model.remote.DashDataModel.NotifyReq;
import com.weberbox.pifire.model.remote.DashDataModel.ProbeTemps;
import com.weberbox.pifire.model.remote.DashDataModel.ProbesEnabled;
import com.weberbox.pifire.model.remote.DashDataModel.SetPoints;
import com.weberbox.pifire.model.remote.DashDataModel.TimerInfo;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.ui.dialogs.BottomIconDialog;
import com.weberbox.pifire.ui.dialogs.TempPickerDialog;
import com.weberbox.pifire.ui.dialogs.TimerPickerDialog;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.CountDownTimer;
import com.weberbox.pifire.ui.utils.TextTransition;
import com.weberbox.pifire.ui.views.PelletLevelView;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.NullUtils;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TempUtils;
import com.weberbox.pifire.utils.VersionUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import timber.log.Timber;

public class DashboardFragment extends Fragment implements DashboardCallback {

    private FragmentDashboardBinding binding;
    private TextView timerCountDownText, grillTempText, probeOneTempText, probeTwoTempText;
    private TextView grillSetText, probeOneTargetText, probeTwoTargetText, pelletLevelText;
    private TextView currentStatusText, smokePlusText, grillTargetText;
    private ImageView probeOneShutdown, probeTwoShutdown, timerShutdown;
    private ProgressBar grillTempProgress, probeOneProgress, probeTwoProgress, timerProgress;
    private ProgressBar loadingBar;
    private LinearLayout smokePlusBox;
    private SwipeRefreshLayout swipeRefresh;
    private TempPickerDialog tempPickerDialog;
    private FrameLayout timerPausedLayout;
    private TableLayout rootContainer;
    private CountDownTimer countDownTimer;
    private PelletLevelView pelletLevelIndicator;
    private Socket socket;
    private TempUtils tempUtils;
    private boolean isFahrenheit;
    private boolean probeOneEnabled = true;
    private boolean probeTwoEnabled = true;
    private boolean isLoading = false;
    private boolean smokePlusEnabled = false;

    private String currentMode = Constants.GRILL_CURRENT_STOP;


    public DashboardFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingBar = binding.connectProgressbar;
        rootContainer = binding.dashLayout.grillControlsTable;

        timerPausedLayout = binding.dashLayout.grillTimerPauseContainer;
        timerCountDownText = binding.dashLayout.grillTimerTime;
        timerProgress = binding.dashLayout.grillTimerProgress;

        probeOneShutdown = binding.dashLayout.probeOneShutdown;
        probeTwoShutdown = binding.dashLayout.probeTwoShutdown;
        timerShutdown = binding.dashLayout.timerShutdown;

        grillTempText = binding.dashLayout.controlsGrillTemp;
        probeOneTempText = binding.dashLayout.controlsProbeOneTemp;
        probeTwoTempText = binding.dashLayout.controlsProbeTwoTemp;

        grillSetText = binding.dashLayout.controlsGrillSetTemp;
        grillTargetText = binding.dashLayout.controlsGrillTargetTemp;
        probeOneTargetText = binding.dashLayout.controlsProbeOneTargetTemp;
        probeTwoTargetText = binding.dashLayout.controlsProbeTwoTargetTemp;

        grillTempProgress = binding.dashLayout.controlsGrillTempProgress;
        probeOneProgress = binding.dashLayout.controlsProbeOneTempProgress;
        probeTwoProgress = binding.dashLayout.controlsProbeTwoTempProgress;

        pelletLevelText = binding.dashLayout.controlsPelletLevel;
        currentStatusText = binding.dashLayout.controlsGrillMode;
        smokePlusText = binding.dashLayout.controlsSmokePlus;
        smokePlusBox = binding.dashLayout.smokePlusStatusContainer;
        LinearLayout currentModeBox = binding.dashLayout.grillStatusContainer;
        LinearLayout grillTempBox = binding.dashLayout.grillTempContainer;
        LinearLayout probeOneTempBox = binding.dashLayout.probeOneContainer;
        LinearLayout probeTwoTempBox = binding.dashLayout.probeTwoContainer;
        FrameLayout timerBox = binding.dashLayout.grillTimerContainer;
        FrameLayout pelletLevelBox = binding.dashLayout.controlsPelletLevelContainer;
        pelletLevelIndicator = binding.dashLayout.pelletLevelIndicator;

        swipeRefresh = binding.dashPullRefresh;

        tempUtils = new TempUtils(getContext());

        isFahrenheit = tempUtils.isFahrenheit();

        swipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                requestForcedDashData(false);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        currentModeBox.setOnClickListener(v -> {
            if (socketConnected()) {
                if (!currentMode.equals(Constants.GRILL_CURRENT_MANUAL)) {
                    if (currentMode.equals(Constants.GRILL_CURRENT_STOP) ||
                            currentMode.equals(Constants.GRILL_CURRENT_MONITOR)) {
                        boolean swipeEnabled = Prefs.getBoolean(getString(
                                R.string.prefs_grill_swipe_start),
                                getResources().getBoolean(R.bool.def_grill_swipe_start));

                        BottomIconDialog dialog = new BottomIconDialog.Builder(requireActivity())
                                .setAutoDismiss(true)
                                .setNegativeButton(getString(R.string.grill_mode_start),
                                        R.drawable.ic_timer_start, (dialogInterface, which) ->
                                                ServerControl.modeStartGrill(socket,
                                                        this::processPostResponse))
                                .setNeutralButton(getString(R.string.grill_mode_monitor),
                                        R.drawable.ic_grill_monitor, (dialogInterface, which) ->
                                                ServerControl.modeMonitorGrill(socket,
                                                        this::processPostResponse))
                                .setPositiveButton(getString(R.string.grill_mode_stop),
                                        R.drawable.ic_timer_stop, (dialogInterface, which) ->
                                                ServerControl.modeStopGrill(socket,
                                                        this::processPostResponse))
                                .setSwipeButton(getString(R.string.swipe_to_start), swipeEnabled,
                                        (dialogInterface, active) -> {
                                            if (active) ServerControl.modeStartGrill(socket,
                                                    this::processPostResponse);
                                        })
                                .build();
                        dialog.show();
                    } else {
                        BottomIconDialog dialog;
                        if (currentMode.equals(Constants.GRILL_CURRENT_SHUTDOWN)) {
                            dialog = new BottomIconDialog.Builder(requireActivity())
                                    .setAutoDismiss(true)
                                    .setNegativeButton(getString(R.string.grill_mode_smoke),
                                            R.drawable.ic_grill_smoke, (dialogInterface, which) ->
                                                    ServerControl.modeSmokeGrill(socket,
                                                            this::processPostResponse))
                                    .setNeutralButton(getString(R.string.grill_mode_hold),
                                            R.drawable.ic_grill_hold, (dialogInterface, which) ->
                                                    showTempPickerDialog())
                                    .setPositiveButton(getString(R.string.grill_mode_stop),
                                            R.drawable.ic_timer_stop,
                                            (dialogInterface, which) ->
                                                    ServerControl.modeStopGrill(socket,
                                                            this::processPostResponse))
                                    .build();
                        } else {
                            dialog = new BottomIconDialog.Builder(requireActivity())
                                    .setAutoDismiss(true)
                                    .setNegativeButton(getString(R.string.grill_mode_smoke),
                                            R.drawable.ic_grill_smoke, (dialogInterface, which) ->
                                                    ServerControl.modeSmokeGrill(socket,
                                                            this::processPostResponse))
                                    .setNeutralButton(getString(R.string.grill_mode_hold),
                                            R.drawable.ic_grill_hold, (dialogInterface, which) ->
                                                    showTempPickerDialog())
                                    .setPositiveButton(getString(R.string.grill_mode_shutdown),
                                            R.drawable.ic_grill_shutdown,
                                            (dialogInterface, which) ->
                                                    ServerControl.modeShutdownGrill(socket,
                                                            this::processPostResponse))
                                    .build();
                        }
                        dialog.show();
                    }
                } else {
                    AlertUtils.createErrorAlert(getActivity(), R.string.control_manual_mode, false);
                }
            }
        });

        smokePlusBox.setOnClickListener(v -> {
            if (currentMode.equals(Constants.GRILL_CURRENT_HOLD)
                    || currentMode.equals(Constants.GRILL_CURRENT_SMOKE)) {
                ServerControl.setSmokePlus(socket, !smokePlusEnabled, this::processPostResponse);
            } else if (!currentMode.equals(Constants.GRILL_CURRENT_STOP)) {
                AlertUtils.createAlert(getActivity(), R.string.control_smoke_plus_disabled,
                        1000);
            }
        });

        grillTempBox.setOnClickListener(v -> {
            if (socketConnected()) {
                int defaultTemp = tempUtils.getDefaultGrillTemp();
                if (!grillSetText.getText().toString().equals(getString(
                        R.string.placeholder_none))) {
                    String temp = grillSetText.getText().toString()
                            .replaceAll(getString(R.string.regex_numbers), "");
                    defaultTemp = Integer.parseInt(temp);
                } else if (!grillTargetText.getText().toString().equals(getString(
                        R.string.placeholder_none))) {
                    String temp = grillTargetText.getText().toString()
                            .replaceAll(getString(R.string.regex_numbers), "");
                    defaultTemp = Integer.parseInt(temp);
                }
                tempPickerDialog = new TempPickerDialog(getActivity(),
                        DashboardFragment.this, Constants.PICKER_TYPE_GRILL,
                        defaultTemp, false);
                tempPickerDialog.showDialog();
            }
        });

        probeOneTempBox.setOnClickListener(v -> {
            if (socketConnected()) {
                int defaultTemp = tempUtils.getDefaultProbeTemp();
                if (!probeOneTempText.getText().toString().equals(getString(R.string.off))) {
                    if (!probeOneTargetText.getText().toString().equals(
                            getString(R.string.placeholder_none))) {
                        String temp = probeOneTargetText.getText().toString()
                                .replaceAll(getString(R.string.regex_numbers), "");
                        defaultTemp = Integer.parseInt(temp);
                    }
                    tempPickerDialog = new TempPickerDialog(getActivity(),
                            DashboardFragment.this, Constants.PICKER_TYPE_PROBE_ONE,
                            defaultTemp, false);
                    tempPickerDialog.showDialog();
                }
            }
        });

        probeOneTempBox.setOnLongClickListener(v -> {
            if (socketConnected()) {
                BottomIconDialog dialog;
                if (!probeOneTempText.getText().toString().equals(getString(R.string.off))) {
                    dialog = new BottomIconDialog.Builder(requireActivity())
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    R.drawable.ic_probe_cancel, (dialogInterface, which) -> {
                                    })
                            .setPositiveButton(getString(R.string.probe_disable),
                                    R.drawable.ic_probe_disable, (dialogInterface, which) ->
                                            ServerControl.probeOneToggle(socket,
                                                    getProbesEnabled(false, probeTwoEnabled),
                                            this::processPostResponse))
                            .build();
                } else {
                    dialog = new BottomIconDialog.Builder(requireActivity())
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    R.drawable.ic_probe_cancel, (dialogInterface, which) -> {
                                    })
                            .setPositiveButton(getString(R.string.probe_enable),
                                    R.drawable.ic_grill_monitor, (dialogInterface, which) ->
                                            ServerControl.probeOneToggle(socket,
                                                    getProbesEnabled(true, probeTwoEnabled),
                                                    this::processPostResponse))
                            .build();
                }
                dialog.show();
            }
            return true;
        });

        probeTwoTempBox.setOnClickListener(v -> {
            if (socketConnected()) {
                int defaultTemp = tempUtils.getDefaultProbeTemp();
                if (!probeTwoTempText.getText().toString().equals(getString(R.string.off))) {
                    if (!probeTwoTargetText.getText().toString().equals("--")) {
                        String temp = probeTwoTargetText.getText().toString()
                                .replaceAll(getString(R.string.regex_numbers), "");
                        defaultTemp = Integer.parseInt(temp);
                    }
                    tempPickerDialog = new TempPickerDialog(getActivity(),
                            DashboardFragment.this, Constants.PICKER_TYPE_PROBE_TWO,
                            defaultTemp, false);
                    tempPickerDialog.showDialog();
                }
            }
        });

        probeTwoTempBox.setOnLongClickListener(v -> {
            if (socketConnected()) {
                BottomIconDialog dialog;
                if (!probeTwoTempText.getText().toString().equals(getString(R.string.off))) {
                    dialog = new BottomIconDialog.Builder(requireActivity())
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    R.drawable.ic_probe_cancel, (dialogInterface, which) -> {
                                    })
                            .setPositiveButton(getString(R.string.probe_disable),
                                    R.drawable.ic_probe_disable, (dialogInterface, which) ->
                                            ServerControl.probeTwoToggle(socket,
                                                    getProbesEnabled(probeOneEnabled, false),
                                                    this::processPostResponse))
                            .build();
                } else {
                    dialog = new BottomIconDialog.Builder(requireActivity())
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    R.drawable.ic_probe_cancel, (dialogInterface, which) -> {
                                    })
                            .setPositiveButton(getString(R.string.probe_enable),
                                    R.drawable.ic_grill_monitor, (dialogInterface, which) ->
                                            ServerControl.probeTwoToggle(socket,
                                                    getProbesEnabled(probeOneEnabled, true),
                                                    this::processPostResponse))
                            .build();
                }
                dialog.show();
            }
            return true;
        });

        timerBox.setOnClickListener(v -> {
            if (socketConnected()) {
                if (countDownTimer != null && countDownTimer.isActive()) {
                    BottomIconDialog dialog;
                    if (countDownTimer.isPaused()) {
                        dialog = new BottomIconDialog.Builder(requireActivity())
                                .setAutoDismiss(true)
                                .setNegativeButton(getString(R.string.timer_stop),
                                        R.drawable.ic_timer_stop, (dialogInterface, which) ->
                                                ServerControl.sendTimerAction(socket,
                                                        Constants.ACTION_TIMER_STOP, true,
                                                        countDownTimer.getEndTimeSecs(),
                                                        countDownTimer.getPauseTimeSecs(),
                                                        countDownTimer.isRunning(),
                                                        this::processPostResponse))
                                .setPositiveButton(getString(R.string.grill_mode_start),
                                        R.drawable.ic_timer_start, (dialogInterface, which) ->
                                                ServerControl.sendTimerAction(socket,
                                                        Constants.ACTION_TIMER_RESTART, true,
                                                        countDownTimer.getEndTimeSecs(),
                                                        countDownTimer.getPauseTimeSecs(),
                                                        countDownTimer.isRunning(),
                                                        this::processPostResponse))
                                .build();
                    } else {
                        dialog = new BottomIconDialog.Builder(requireActivity())
                                .setAutoDismiss(true)
                                .setNegativeButton(getString(R.string.timer_stop),
                                        R.drawable.ic_timer_stop, (dialogInterface, which) ->
                                                ServerControl.sendTimerAction(socket,
                                                        Constants.ACTION_TIMER_STOP, false,
                                                        countDownTimer.getEndTimeSecs(),
                                                        countDownTimer.getPauseTimeSecs(),
                                                        countDownTimer.isRunning(),
                                                        this::processPostResponse))
                                .setPositiveButton(getString(R.string.timer_pause),
                                        R.drawable.ic_timer_pause, (dialogInterface, which) ->
                                                ServerControl.sendTimerAction(socket,
                                                        Constants.ACTION_TIMER_PAUSE, false,
                                                        countDownTimer.getEndTimeSecs(),
                                                        countDownTimer.getPauseTimeSecs(),
                                                        countDownTimer.isRunning(),
                                                        this::processPostResponse))
                                .build();
                    }
                    dialog.show();
                } else {
                    TimerPickerDialog timerPickerDialog = new TimerPickerDialog(getActivity(),
                            DashboardFragment.this);
                    timerPickerDialog.showDialog();
                }
            }
        });

        pelletLevelBox.setOnClickListener(v -> {
            if (socketConnected()) {
                ServerControl.sendCheckHopperLevel(socket, this::processPostResponse);
                requestForcedDashData(true);
            }
        });

        if (getActivity() != null) {
            MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(
                    MainViewModel.class);
            mainViewModel.getDashData().observe(getViewLifecycleOwner(), dashData -> {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                if (dashData != null) {
                    updateUIWithData(dashData);
                }
            });

            mainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                if (enabled != null && enabled) {
                    if (!isLoading) {
                        requestDataUpdate();
                    }
                } else {
                    toggleLoading(false);
                    setOfflineMode();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestDataUpdate();
        checkForceScreenOn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
        clearForceScreenOn();
        binding = null;
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
        if (socket != null && !isLoading) {
            isLoading = true;
            requestForcedDashData(true);
        }
    }

    private void requestForcedDashData(boolean showLoading) {
        toggleLoading(showLoading);
        if (socket != null) {
            if (VersionUtils.isSupported("1.2.6")) {
                socket.emit(ServerConstants.GE_GET_DASH_DATA, true);
            } else {
                socket.emit(ServerConstants.REQUEST_GRILL_DATA, true);
            }
        }
    }

    private void toggleLoading(boolean show) {
        if (show && socket != null) {
            if (!Alerter.isShowing()) {
                loadingBar.setVisibility(View.VISIBLE);
            }
        } else {
            loadingBar.setVisibility(View.GONE);
        }
    }

    private void checkForceScreenOn() {
        if (getActivity() != null) {
            if (Prefs.getBoolean(getString(R.string.prefs_keep_screen_on),
                    getResources().getBoolean(R.bool.def_keep_screen_on))) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    private void clearForceScreenOn() {
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showTempPickerDialog() {
        TempPickerDialog tempPickerDialog = new TempPickerDialog(requireActivity(),
                DashboardFragment.this, Constants.PICKER_TYPE_GRILL,
                new TempUtils(requireActivity()).getDefaultGrillTemp(), true);
        tempPickerDialog.showDialog();
    }

    private CountDownTimer getCountdownTimer() {
        if (countDownTimer == null) {
            return new CountDownTimer() {
                @Override
                public void onDuration(int duration) {
                    if (timerProgress.getMax() != duration) {
                        timerProgress.setMax(duration);
                    }
                }

                @Override
                public void onRemainingTime(String timeRemaining) {
                    timerCountDownText.setText(timeRemaining);
                }

                @Override
                public void onTimerTick(int secondsUntilFinished) {
                    timerProgress.setProgress(secondsUntilFinished);
                }

                @Override
                public void onFinished() {
                    timerProgress.setProgress(0);
                    timerCountDownText.setText(R.string.placeholder_time);
                }
            };
        }
        return countDownTimer;
    }

    @Override
    public void onTempConfirmClicked(int type, String temp, boolean hold, boolean shutdown) {
        if (socket != null) {
            if (hold && type == Constants.PICKER_TYPE_GRILL) {
                ServerControl.setGrillTemp(socket, temp, this::processPostResponse);
            }
            ServerControl.setProbeNotify(socket, type, temp, hold, shutdown,
                    this::processPostResponse);
        }
    }

    @Override
    public void onTempClearClicked(int type) {
        if (socket != null) {
            ServerControl.clearProbeNotify(socket, type, this::processPostResponse);
        }
    }

    @Override
    public void onTimerConfirmClicked(String hours, String minutes, Boolean shutdown) {
        if (socket != null) {
            ServerControl.sendTimerTime(socket, hours, minutes, shutdown,
                    this::processPostResponse);
        }
    }

    private void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResult().equals("error")) {
            requireActivity().runOnUiThread(() ->
                    AlertUtils.createErrorAlert(requireActivity(),
                            result.getMessage(), false));
        }
    }

    public void updateUIWithData(String responseData) {
        DashDataModel dashDataModel;

        try {
            dashDataModel = DashDataModel.parseJSON(responseData);

            ProbeTemps probeTemps = dashDataModel.getProbeTemps();
            ProbesEnabled probesEnabled = dashDataModel.getProbesEnabled();
            SetPoints setPoints = dashDataModel.getSetPoints();
            NotifyReq notifyReq = dashDataModel.getNotifyReq();
            NotifyData notifyData = dashDataModel.getNotifyData();
            TimerInfo timerInfo = dashDataModel.getTimerInfo();

            String currentMode = dashDataModel.getCurrentMode();
            long timerStartTime = timerInfo.getTimerStartTime();
            long timerEndTime = timerInfo.getTimerEndTime();
            long timerPauseTime = timerInfo.getTimerPauseTime();
            double grillTemp = probeTemps.getGrillTemp();
            double probeOneTemp = probeTemps.getProbeOneTemp();
            double probeTwoTemp = probeTemps.getProbeTwoTemp();
            int grillTarget = setPoints.getGrillTarget();
            int probeOneTarget = setPoints.getProbeOneTarget();
            int probeTwoTarget = setPoints.getProbeTwoTarget();
            int hopperLevel = dashDataModel.getHopperLevel();
            boolean grillEnabled = probesEnabled.getGrillEnabled();
            boolean grillNotify = notifyReq.getGrillNotify();
            boolean probeOneNotify = notifyReq.getProbeOneNotify();
            boolean probeTwoNotify = notifyReq.getProbeTwoNotify();
            boolean smokePlus = dashDataModel.getSmokePlus();
            boolean timerPaused = timerInfo.getTimerPaused();
            boolean timerActive = timerInfo.getTimerActive();
            boolean probeOneShutdown = notifyData.getP1Shutdown();
            boolean probeTwoShutdown = notifyData.getP2Shutdown();
            boolean timerShutdown = notifyData.getTimerShutdown();
            probeOneEnabled = probesEnabled.getProbeOneEnabled();
            probeTwoEnabled = probesEnabled.getProbeTwoEnabled();

            TransitionManager.beginDelayedTransition(rootContainer, new TextTransition());

            if (NullUtils.checkObjectNotNull(currentMode, smokePlus, hopperLevel, grillTarget)) {
                this.currentMode = currentMode;
                if (currentMode.equals(Constants.GRILL_CURRENT_STOP)) {
                    currentStatusText.setText(R.string.off);
                    grillTempProgress.setProgress(0);
                    probeOneProgress.setProgress(0);
                    probeTwoProgress.setProgress(0);
                } else {
                    currentStatusText.setText(currentMode);
                }

                if (currentMode.equals(Constants.GRILL_CURRENT_HOLD) |
                        currentMode.equals(Constants.GRILL_CURRENT_SMOKE) && smokePlus) {
                    smokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_enabled);
                    smokePlusText.setText(R.string.on);
                    smokePlusEnabled = true;
                } else {
                    smokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
                    smokePlusText.setText(R.string.off);
                    smokePlusEnabled = false;
                }

                if (currentMode.equals(Constants.GRILL_CURRENT_HOLD) && grillTarget > 0) {
                    grillSetText.setText(StringUtils.formatTemp(grillTarget));
                } else {
                    grillSetText.setText(R.string.placeholder_none);
                }

                if (hopperLevel >= 0) {
                    pelletLevelIndicator.setLevel(hopperLevel);
                    pelletLevelText.setText(StringUtils.formatPercentage(hopperLevel));
                    if (getActivity() != null) {
                        int color = hopperLevel < AppConfig.LOW_PELLET_WARNING ?
                                R.color.colorPelletDanger : R.color.colorWhite;
                        pelletLevelText.setTextColor(ContextCompat.getColor(getActivity(), color));
                    }
                } else {
                    pelletLevelText.setText(R.string.placeholder_percentage);
                }
            }

            if (NullUtils.checkObjectNotNull(grillEnabled, grillNotify, grillTarget, grillTemp)) {
                if (grillEnabled) {
                    if (grillNotify && grillTarget > 0) {
                        grillTempProgress.setMax(grillTarget);
                        grillTargetText.setText(StringUtils.formatTemp(grillTarget));
                    } else {
                        grillTempProgress.setMax(tempUtils.getMaxGrillTemp());
                        grillTargetText.setText(R.string.placeholder_none);
                    }

                    if (grillTemp > 0) {
                        grillTempProgress.setProgress((int) grillTemp);
                        grillTempText.setText(StringUtils.formatTemp(grillTemp, isFahrenheit));
                    } else {
                        grillTempText.setText(R.string.placeholder_temp);
                    }
                } else {
                    grillTempProgress.setMax(0);
                    grillTargetText.setText(R.string.placeholder_none);
                    grillTempText.setText(R.string.off);
                }
            }

            if (NullUtils.checkObjectNotNull(probeOneEnabled, probeOneNotify, probeOneTarget,
                    probeOneTemp)) {
                if (probeOneEnabled) {
                    if (probeOneNotify && probeOneTarget > 0) {
                        probeOneProgress.setMax(probeOneTarget);
                        probeOneTargetText.setText(StringUtils.formatTemp(probeOneTarget));
                    } else {
                        probeOneProgress.setMax(tempUtils.getMaxProbeTemp());
                        probeOneTargetText.setText(R.string.placeholder_none);
                    }

                    if (probeOneTemp > 0) {
                        probeOneProgress.setProgress((int) probeOneTemp);
                        probeOneTempText.setText(StringUtils.formatTemp(probeOneTemp, isFahrenheit));
                    } else {
                        probeOneTempText.setText(R.string.placeholder_temp);
                    }
                } else {
                    probeOneProgress.setMax(0);
                    probeOneTargetText.setText(R.string.placeholder_none);
                    probeOneTempText.setText(R.string.off);
                }
            }

            if (NullUtils.checkObjectNotNull(probeTwoEnabled, probeTwoNotify, probeTwoTarget,
                    probeTwoTemp)) {
                if (probeTwoEnabled) {
                    if (probeTwoNotify && probeTwoTarget > 0) {
                        probeTwoProgress.setMax(probeTwoTarget);
                        probeTwoTargetText.setText(StringUtils.formatTemp(probeTwoTarget));
                    } else {
                        probeTwoProgress.setMax(tempUtils.getMaxProbeTemp());
                        probeTwoTargetText.setText(R.string.placeholder_none);
                    }

                    if (probeTwoTemp > 0) {
                        probeTwoProgress.setProgress((int) probeTwoTemp);
                        probeTwoTempText.setText(StringUtils.formatTemp(probeTwoTemp, isFahrenheit));
                    } else {
                        probeTwoTempText.setText(R.string.placeholder_temp);
                    }
                } else {
                    probeTwoProgress.setMax(0);
                    probeTwoTargetText.setText(R.string.placeholder_none);
                    probeTwoTempText.setText(R.string.off);
                }
            }

            if (NullUtils.checkObjectNotNull(timerActive, timerStartTime, timerEndTime,
                    timerPauseTime, timerPaused)) {
                if (timerActive) {
                    countDownTimer = getCountdownTimer();
                    countDownTimer.startTimer(timerStartTime, timerEndTime, timerPauseTime);
                    toggleTimerPaused(timerPaused);
                    if (timerPaused) {
                        countDownTimer.pauseTimer();
                        timerCountDownText.setText(countDownTimer.formatTimeRemaining(
                                TimeUnit.SECONDS.toMillis(timerEndTime) -
                                        TimeUnit.SECONDS.toMillis(timerPauseTime)));
                    } else {
                        countDownTimer.resumeTimer();
                    }
                } else {
                    stopTimer();
                }
            }

            if (NullUtils.checkObjectNotNull(probeOneShutdown, probeTwoShutdown, timerShutdown)) {
                AnimUtils.fadeAnimation(this.probeOneShutdown, 300, probeOneShutdown ?
                        Constants.FADE_IN : Constants.FADE_OUT);
                AnimUtils.fadeAnimation(this.probeTwoShutdown, 300, probeTwoShutdown ?
                        Constants.FADE_IN : Constants.FADE_OUT);
                AnimUtils.fadeAnimation(this.timerShutdown, 300, timerShutdown ?
                        Constants.FADE_IN : Constants.FADE_OUT);
            }

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e, "JSON Error");
            AlertUtils.createErrorAlert(getActivity(), R.string.json_error_dash, false);
        }

        toggleLoading(false);
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.stopTimer();
        }
        toggleTimerPaused(false);
        timerProgress.setProgress(0);
        timerCountDownText.setText(R.string.placeholder_time);
    }

    private void toggleTimerPaused(boolean show) {
        if (show) {
            AnimUtils.fadeInAnimation(timerPausedLayout, 300);
        } else {
            AnimUtils.fadeOutAnimation(timerPausedLayout, 300);
        }
    }

    private List<Integer> getProbesEnabled(boolean probeOne, boolean probeTwo) {
        return Arrays.asList(1, probeOne ? 1 : 0, probeTwo ? 1 : 0);
    }

    private void setOfflineMode() {
        stopTimer();
        currentMode = Constants.GRILL_CURRENT_STOP;
        grillTempProgress.setProgress(0);
        probeOneProgress.setProgress(0);
        probeTwoProgress.setProgress(0);
        currentStatusText.setText(R.string.off);
        smokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
        smokePlusText.setText(R.string.off);
        grillTempText.setText(R.string.placeholder_temp);
        grillSetText.setText(R.string.placeholder_none);
        grillTargetText.setText(R.string.placeholder_none);
        probeOneTargetText.setText(R.string.placeholder_none);
        probeOneTempText.setText(R.string.placeholder_temp);
        probeTwoTargetText.setText(R.string.placeholder_none);
        probeTwoTempText.setText(R.string.placeholder_temp);
        timerCountDownText.setText(R.string.placeholder_time);
        pelletLevelText.setText(R.string.placeholder_percentage);
        if (getActivity() != null) {
            pelletLevelText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        }
        AnimUtils.fadeAnimation(probeOneShutdown, 300, Constants.FADE_OUT);
        AnimUtils.fadeAnimation(probeTwoShutdown, 300, Constants.FADE_OUT);
        AnimUtils.fadeAnimation(timerShutdown, 300, Constants.FADE_OUT);
    }
}