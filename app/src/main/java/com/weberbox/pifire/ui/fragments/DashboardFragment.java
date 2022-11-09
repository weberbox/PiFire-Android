package com.weberbox.pifire.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentDashboardBinding;
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
import com.weberbox.pifire.ui.dialogs.PrimePickerDialog;
import com.weberbox.pifire.ui.dialogs.TempPickerDialog;
import com.weberbox.pifire.ui.dialogs.TimePickerDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogDashboardCallback;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.CountDownTimer;
import com.weberbox.pifire.ui.utils.TextTransition;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.ui.views.DashProbeCard;
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

public class DashboardFragment extends Fragment implements DialogDashboardCallback {

    private FragmentDashboardBinding binding;
    private TextView timerCountDownText, grillTempText, probeOneTempText, probeTwoTempText;
    private TextView grillSetText, probeOneTargetText, probeTwoTargetText, pelletLevelText;
    private TextView currentModeText, smokePlusText, grillTargetText, pwmControlText;
    private ImageView probeOneShutdown, probeTwoShutdown, timerShutdown;
    private ImageView probeOneKeepWarm, probeTwoKeepWarm, timerKeepWarm;
    private ProgressBar grillTempProgress, probeOneProgress, probeTwoProgress, timerProgress;
    private ProgressBar loadingBar;
    private ConstraintLayout smokePlusBox, pwmControlBox;
    private SwipeRefreshLayout swipeRefresh;
    private TempPickerDialog tempPickerDialog;
    private FrameLayout timerPausedLayout;
    private ConstraintLayout rootContainer;
    private CountDownTimer countDownTimer;
    private PelletLevelView pelletLevelIndicator;
    private Socket socket;
    private TempUtils tempUtils;
    private boolean isFahrenheit;
    private boolean probeOneEnabled = true;
    private boolean probeTwoEnabled = true;
    private boolean isLoading = false;
    private boolean smokePlusEnabled = false;
    private boolean pwmControlEnabled = false;

    private String currentMode = Constants.GRILL_CURRENT_STOP;


    public DashboardFragment() {
        super();
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
        rootContainer = binding.dashLayout.dashRootContainer;

        // Mode
        ConstraintLayout currentModeBox = binding.dashLayout.dashStatusContainer;
        currentModeText = binding.dashLayout.dashGrillMode;

        // Grill Probe
        DashProbeCard dashGrillProbe = binding.dashLayout.dashGrillProbe;
        grillTempText = dashGrillProbe.getProbeTemp();
        grillSetText = dashGrillProbe.getProbeSetTemp();
        grillTargetText = dashGrillProbe.getProbeTargetTemp();
        grillTempProgress = dashGrillProbe.getProbeTempProgress();

        // Probe One
        DashProbeCard dashProbeOne = binding.dashLayout.dashProbeOne;
        probeOneShutdown = dashProbeOne.getProbeShutdown();
        probeOneKeepWarm = dashProbeOne.getProbeKeepWarm();
        probeOneTempText = dashProbeOne.getProbeTemp();
        probeOneTargetText = dashProbeOne.getProbeTargetTemp();
        probeOneProgress = dashProbeOne.getProbeTempProgress();

        // Probe Two
        DashProbeCard dashProbeTwo = binding.dashLayout.dashProbeTwo;
        probeTwoShutdown = dashProbeTwo.getProbeShutdown();
        probeTwoKeepWarm = dashProbeTwo.getProbeKeepWarm();
        probeTwoTempText = dashProbeTwo.getProbeTemp();
        probeTwoTargetText = dashProbeTwo.getProbeTargetTemp();
        probeTwoProgress = dashProbeTwo.getProbeTempProgress();

        // Timer
        ConstraintLayout timerBox = binding.dashLayout.dashTimerContainer;
        timerPausedLayout = binding.dashLayout.dashTimerPauseContainer;
        timerCountDownText = binding.dashLayout.dashTimerTime;
        timerProgress = binding.dashLayout.dashTimerProgress;
        timerShutdown = binding.dashLayout.dashTimerShutdown;
        timerKeepWarm = binding.dashLayout.dashTimerKeepWarm;

        // Pellet Level
        ConstraintLayout pelletLevelBox = binding.dashLayout.dashPelletContainer;
        pelletLevelText = binding.dashLayout.dashPelletLevel;
        pelletLevelIndicator = binding.dashLayout.dashPelletLevelIndicator;

        // Smoke Plus
        TextView smokePlusTitle = binding.dashLayout.dashSmokePlusTitle;
        smokePlusText = binding.dashLayout.dashSmokePlus;
        smokePlusBox = binding.dashLayout.dashSmokePlusContainer;

        // PWM Control
        pwmControlText = binding.dashLayout.dashPwmControl;
        pwmControlBox = binding.dashLayout.dashPwmContainer;

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
                    if (currentMode.equals(Constants.GRILL_CURRENT_STOP)) {
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
                                .setPositiveButton(getString(R.string.grill_mode_prime),
                                        R.drawable.ic_grill_prime, (dialogInterface, which) ->
                                                showPrimePickerDialog())
                                .setSwipeButton(getString(R.string.swipe_to_start), swipeEnabled,
                                        (dialogInterface, active) -> {
                                            if (active) ServerControl.modeStartGrill(socket,
                                                    this::processPostResponse);
                                        })
                                .build();
                        dialog.show();
                    } else if (currentMode.equals(Constants.GRILL_CURRENT_MONITOR) ||
                                    currentMode.equals(Constants.GRILL_CURRENT_PRIME)) {
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
                                                    showHoldPickerDialog())
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
                                                    showHoldPickerDialog())
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
                    AlertUtils.createManualAlert(getActivity(), R.string.control_manual_mode, false);
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

        if (Prefs.getBoolean(getString(R.string.prefs_dc_fan))) {
            pwmControlBox.setVisibility(View.VISIBLE);
            if (!ViewUtils.isTablet(requireActivity())) {
                smokePlusTitle.setText(R.string.grill_smoke_plus_abv);
            }
            pwmControlBox.setOnClickListener(v -> {
                if (currentMode.equals(Constants.GRILL_CURRENT_HOLD)) {
                    ServerControl.setPWMControl(socket, !pwmControlEnabled,
                            this::processPostResponse);
                } else if (!currentMode.equals(Constants.GRILL_CURRENT_STOP)) {
                    AlertUtils.createAlert(getActivity(), R.string.control_pwm_control_disabled,
                            1000);
                }
            });
        } else {
            pwmControlBox.setVisibility(View.GONE);
        }

        dashGrillProbe.setOnClickListener(v -> {
            if (socketConnected()) {
                int defaultTemp = tempUtils.getDefaultGrillTemp();
                boolean hold = !grillSetText.getText().toString().equals(getString(
                        R.string.placeholder_none));
                if (hold) {
                    defaultTemp = tempUtils.cleanTempString(grillSetText.getText().toString());
                } else if (!grillTargetText.getText().toString().equals(getString(
                        R.string.placeholder_none))) {
                    defaultTemp = tempUtils.cleanTempString(grillTargetText.getText().toString());
                }
                tempPickerDialog = new TempPickerDialog(getActivity(), Constants.PICKER_TYPE_GRILL,
                        defaultTemp, hold, false, DashboardFragment.this);
                tempPickerDialog.showDialog();
            }
        });

        if (VersionUtils.isSupported(ServerVersions.V_134)) {
            dashGrillProbe.setOnLongClickListener(v -> {
                if (socketConnected()) {
                    int defaultTemp = tempUtils.getDefaultGrillTemp();
                    if (!grillTargetText.getText().toString().equals(getString(
                            R.string.placeholder_none))) {
                        defaultTemp = tempUtils.cleanTempString(grillTargetText.getText().toString());
                    }
                    tempPickerDialog = new TempPickerDialog(getActivity(),
                            Constants.PICKER_TYPE_GRILL_NOTIFY, defaultTemp, false, false,
                            DashboardFragment.this);
                    tempPickerDialog.showDialog();
                }
                return true;
            });
        }

        dashProbeOne.setOnClickListener(v -> {
            if (socketConnected()) {
                int defaultTemp = tempUtils.getDefaultProbeTemp();
                if (!probeOneTempText.getText().toString().equals(getString(R.string.off))) {
                    if (!probeOneTargetText.getText().toString().equals(
                            getString(R.string.placeholder_none))) {
                        defaultTemp = tempUtils.cleanTempString(
                                probeOneTargetText.getText().toString());

                    }
                    tempPickerDialog = new TempPickerDialog(getActivity(),
                            Constants.PICKER_TYPE_PROBE_ONE, defaultTemp, false, false,
                            DashboardFragment.this);
                    tempPickerDialog.showDialog();
                }
            }
        });

        dashProbeOne.setOnLongClickListener(v -> {
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

        dashProbeTwo.setOnClickListener(v -> {
            if (socketConnected()) {
                int defaultTemp = tempUtils.getDefaultProbeTemp();
                if (!probeTwoTempText.getText().toString().equals(getString(R.string.off))) {
                    if (!probeTwoTargetText.getText().toString().equals("--")) {
                        defaultTemp = tempUtils.cleanTempString(
                                probeTwoTargetText.getText().toString());
                    }
                    tempPickerDialog = new TempPickerDialog(getActivity(),
                            Constants.PICKER_TYPE_PROBE_TWO, defaultTemp, false, false,
                            DashboardFragment.this);
                    tempPickerDialog.showDialog();
                }
            }
        });

        dashProbeTwo.setOnLongClickListener(v -> {
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
                                                        ServerConstants.PT_TIMER_STOP,
                                                        this::processPostResponse))
                                .setPositiveButton(getString(R.string.timer_start),
                                        R.drawable.ic_timer_start, (dialogInterface, which) ->
                                                ServerControl.sendTimerAction(socket,
                                                        ServerConstants.PT_TIMER_START,
                                                        this::processPostResponse))
                                .build();
                    } else {
                        dialog = new BottomIconDialog.Builder(requireActivity())
                                .setAutoDismiss(true)
                                .setNegativeButton(getString(R.string.timer_stop),
                                        R.drawable.ic_timer_stop, (dialogInterface, which) ->
                                                ServerControl.sendTimerAction(socket,
                                                        ServerConstants.PT_TIMER_STOP,
                                                        this::processPostResponse))
                                .setPositiveButton(getString(R.string.timer_pause),
                                        R.drawable.ic_timer_pause, (dialogInterface, which) ->
                                                ServerControl.sendTimerAction(socket,
                                                        ServerConstants.PT_TIMER_PAUSE,
                                                        this::processPostResponse))
                                .build();
                    }
                    dialog.show();
                } else {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            DashboardFragment.this);
                    timePickerDialog.showDialog();
                }
            }
        });

        pelletLevelBox.setOnClickListener(v -> {
            if (socketConnected()) {
                ServerControl.sendCheckHopperLevel(socket, this::processPostResponse);
                requestForcedDashData(true);
            }
        });

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(
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

    @Override
    public void onResume() {
        super.onResume();
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
        requestDataUpdate();
        checkForceScreenOn();
    }

    @Override
    public void onPause() {
        super.onPause();
        socket = null;
        clearForceScreenOn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
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
            if (VersionUtils.isSupported(ServerVersions.V_127)) {
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
            loadingBar.setVisibility(View.INVISIBLE);
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

    private void showHoldPickerDialog() {
        TempPickerDialog tempPickerDialog = new TempPickerDialog(requireActivity(),
                Constants.PICKER_TYPE_GRILL, new TempUtils(requireActivity()).getDefaultGrillTemp(),
                true, true, DashboardFragment.this);
        tempPickerDialog.showDialog();
    }

    private void showPrimePickerDialog() {
        PrimePickerDialog dialog = new PrimePickerDialog(requireActivity(),
                (amount, nextMode) -> ServerControl.modePrimeGrill(socket, amount, nextMode,
                        this::processPostResponse));
        dialog.showDialog();
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
    public void onTempConfirmClicked(int type, String temp, boolean hold, boolean shutdown,
                                     boolean keepWarm) {
        if (socket != null) {
            if (hold && type == Constants.PICKER_TYPE_GRILL) {
                ServerControl.setGrillTemp(socket, temp, this::processPostResponse);
            }
            ServerControl.setProbeNotify(socket, type, temp, hold, shutdown, keepWarm,
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
    public void onTimerConfirmClicked(String hours, String minutes, boolean shutdown,
                                      boolean keepWarm) {
        if (socket != null) {
            ServerControl.sendTimerTime(socket, hours, minutes, shutdown, keepWarm,
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

    public void updateUIWithData(DashDataModel dashDataModel) {

        try {

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
            Double grillTemp = probeTemps.getGrillTemp();
            Double probeOneTemp = probeTemps.getProbeOneTemp();
            Double probeTwoTemp = probeTemps.getProbeTwoTemp();
            Integer grillTarget = setPoints.getGrillTarget();
            Integer grillNotifyTarget = setPoints.getGrillNotifyTarget();
            Integer probeOneTarget = setPoints.getProbeOneTarget();
            Integer probeTwoTarget = setPoints.getProbeTwoTarget();
            Integer hopperLevel = dashDataModel.getHopperLevel();
            Boolean grillEnabled = probesEnabled.getGrillEnabled();
            Boolean grillNotify = notifyReq.getGrillNotify();
            Boolean probeOneNotify = notifyReq.getProbeOneNotify();
            Boolean probeTwoNotify = notifyReq.getProbeTwoNotify();
            Boolean smokePlus = dashDataModel.getSmokePlus();
            Boolean pwmControl = dashDataModel.getPwmControl();
            Boolean timerPaused = timerInfo.getTimerPaused();
            Boolean timerActive = timerInfo.getTimerActive();
            Boolean probeOneShutdown = notifyData.getP1Shutdown();
            Boolean probeTwoShutdown = notifyData.getP2Shutdown();
            Boolean timerShutdown = notifyData.getTimerShutdown();
            Boolean probeOneKeepWarm = notifyData.getP1KeepWarm();
            Boolean probeTwoKeepWarm = notifyData.getP2KeepWarm();
            Boolean timerKeepWarm = notifyData.getTimerKeepWarm();
            probeOneEnabled = probesEnabled.getProbeOneEnabled();
            probeTwoEnabled = probesEnabled.getProbeTwoEnabled();

            TransitionManager.beginDelayedTransition(rootContainer, new TextTransition());

            if (NullUtils.checkObjectNotNull(currentMode, smokePlus, hopperLevel, grillTarget)) {
                this.currentMode = currentMode;
                if (currentMode.equals(Constants.GRILL_CURRENT_STOP)) {
                    currentModeText.setText(R.string.off);
                    grillTempProgress.setProgress(0);
                    probeOneProgress.setProgress(0);
                    probeTwoProgress.setProgress(0);
                } else {
                    currentModeText.setText(currentMode);
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

                if (currentMode.equals(Constants.GRILL_CURRENT_HOLD) && pwmControl) {
                    pwmControlBox.setBackgroundResource(R.drawable.bg_ripple_smokep_enabled);
                    pwmControlText.setText(R.string.on);
                    pwmControlEnabled = true;
                } else {
                    pwmControlBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
                    pwmControlText.setText(R.string.off);
                    pwmControlEnabled = false;
                }

                if (currentMode.equals(Constants.GRILL_CURRENT_HOLD) && grillTarget > 0) {
                    grillSetText.setText(StringUtils.formatTemp(grillTarget, isFahrenheit));
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

            if (NullUtils.checkObjectNotNull(grillEnabled, grillNotify, grillTarget,
                    grillNotifyTarget, grillTemp)) {
                if (grillEnabled) {
                    int grillTempTarget;
                    if (VersionUtils.isSupported(ServerVersions.V_134)) {
                        grillTempTarget = grillNotifyTarget;
                    } else {
                        grillTempTarget = grillTarget;
                    }

                    if (grillNotify && grillTempTarget > 0) {
                        grillTempProgress.setMax(grillTempTarget);
                        grillTargetText.setText(StringUtils.formatTemp(grillTempTarget,
                                isFahrenheit));
                    } else {
                        grillTempProgress.setMax(tempUtils.getMaxGrillTemp());
                        grillTargetText.setText(R.string.placeholder_none);
                    }

                    if (grillTemp > 0) {
                        grillTempProgress.setProgress(grillTemp.intValue());
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
                        probeOneTargetText.setText(StringUtils.formatTemp(probeOneTarget,
                                isFahrenheit));
                    } else {
                        probeOneProgress.setMax(tempUtils.getMaxProbeTemp());
                        probeOneTargetText.setText(R.string.placeholder_none);
                    }

                    if (probeOneTemp > 0) {
                        probeOneProgress.setProgress(probeOneTemp.intValue());
                        probeOneTempText.setText(StringUtils.formatTemp(probeOneTemp,
                                isFahrenheit));
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
                        probeTwoTargetText.setText(StringUtils.formatTemp(probeTwoTarget,
                                isFahrenheit));
                    } else {
                        probeTwoProgress.setMax(tempUtils.getMaxProbeTemp());
                        probeTwoTargetText.setText(R.string.placeholder_none);
                    }

                    if (probeTwoTemp > 0) {
                        probeTwoProgress.setProgress(probeTwoTemp.intValue());
                        probeTwoTempText.setText(StringUtils.formatTemp(probeTwoTemp,
                                isFahrenheit));
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

            if (NullUtils.checkObjectNotNull(probeOneKeepWarm, probeTwoKeepWarm, timerKeepWarm)) {
                AnimUtils.fadeAnimation(this.probeOneKeepWarm, 300, probeOneKeepWarm ?
                        Constants.FADE_IN : Constants.FADE_OUT);
                AnimUtils.fadeAnimation(this.probeTwoKeepWarm, 300, probeTwoKeepWarm ?
                        Constants.FADE_IN : Constants.FADE_OUT);
                AnimUtils.fadeAnimation(this.timerKeepWarm, 300, timerKeepWarm ?
                        Constants.FADE_IN : Constants.FADE_OUT);
            }

        } catch (IllegalStateException | NullPointerException e) {
            Timber.e(e, "Dashboard JSON Error");
            AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                    getString(R.string.menu_dashboard)), false);
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
        currentModeText.setText(R.string.off);
        smokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
        smokePlusText.setText(R.string.off);
        smokePlusEnabled = false;
        pwmControlBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
        pwmControlText.setText(R.string.off);
        pwmControlEnabled = false;
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