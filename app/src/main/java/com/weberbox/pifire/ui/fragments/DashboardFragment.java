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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentDashboardBinding;
import com.weberbox.pifire.interfaces.DashProbeCallback;
import com.weberbox.pifire.model.local.DashProbeModel.DashProbe;
import com.weberbox.pifire.model.local.ProbeOptionsModel;
import com.weberbox.pifire.model.remote.DashDataModel;
import com.weberbox.pifire.model.remote.DashDataModel.DashProbeInfo;
import com.weberbox.pifire.model.remote.DashDataModel.NotifyData;
import com.weberbox.pifire.model.remote.DashDataModel.TimerInfo;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeInfo;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeMap;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.recycler.adapter.DashProbeAdapter;
import com.weberbox.pifire.ui.dialogs.BottomIconDialog;
import com.weberbox.pifire.ui.dialogs.PrimePickerDialog;
import com.weberbox.pifire.ui.dialogs.TempPickerDialog;
import com.weberbox.pifire.ui.dialogs.TimePickerDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogDashboardCallback;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.CountDownTimer;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.ui.views.PelletLevelView;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.NullUtils;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TempUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import timber.log.Timber;

public class DashboardFragment extends Fragment implements DialogDashboardCallback,
        DashProbeCallback {

    private FragmentDashboardBinding binding;
    private TextView timerCountDownText, pelletLevelText;
    private TextView currentModeText, smokePlusText, pwmControlText;
    private ImageView timerShutdown, timerKeepWarm;
    private ProgressBar loadingBar, timerProgress;
    private ConstraintLayout smokePlusBox, pwmControlBox;
    private SwipeRefreshLayout swipeRefresh;
    private TempPickerDialog tempPickerDialog;
    private FrameLayout timerPausedLayout;
    private CountDownTimer countDownTimer;
    private PelletLevelView pelletLevelIndicator;
    private Socket socket;
    private TempUtils tempUtils;
    private DashProbeAdapter dashAdapter;
    private ArrayList<NotifyData> notifyData;

    private List<ProbeInfo> probeInfo;
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
        swipeRefresh = binding.dashPullRefresh;

        swipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                requestForcedDashData(false);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        // Mode
        ConstraintLayout currentModeBox = binding.dashLayout.dashStatusContainer;
        currentModeText = binding.dashLayout.dashGrillMode;

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

        tempUtils = new TempUtils(getContext());

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

        RecyclerView dashProbeRecycler = binding.dashLayout.dashProbeRecycler;
        if (dashProbeRecycler.getItemAnimator() != null) {
            dashProbeRecycler.getItemAnimator().setChangeDuration(0);
        }

        probeInfo = getProbeInfoList();

        dashAdapter = new DashProbeAdapter(getProbeList(probeInfo), tempUtils,this);

        int spanCount = ViewUtils.isLandscape(requireActivity()) ?
                AppConfig.DASH_RECYCLER_LAND : AppConfig.DASH_RECYCLER_PORT;
        dashProbeRecycler.setLayoutManager(new GridLayoutManager(requireActivity(), spanCount));
        dashProbeRecycler.setAdapter(dashAdapter);

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
        updateProbeNames();
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

    private void updateProbeNames() {
        if (probeInfo != null && probeInfo.size() > 0 && dashAdapter != null) {
            for (DashProbe cProbe : dashAdapter.getDashProbes()) {
                for (DashProbe nProbe : getProbeList(getProbeInfoList())) {
                    if (Objects.equals(cProbe.getLabel(), nProbe.getLabel())) {
                        cProbe.setName(nProbe.getName());
                        dashAdapter.updateProbe(cProbe);
                    }
                }
            }
        }
    }

    private List<ProbeInfo> getProbeInfoList() {
        ProbeMap probeMap = new Gson().fromJson(
                Prefs.getString(getString(R.string.prefs_probe_map)),
                new TypeToken<ProbeMap>() {}.getType());
        return probeMap.getProbeInfo();
    }

    private List<DashProbe> getProbeList(List<ProbeInfo> probeInfo) {
        List<DashProbe> list = new ArrayList<>();
        if (probeInfo != null && probeInfo.size() > 0) {
            for (ProbeInfo probe : probeInfo) {
                list.add(new DashProbe(
                        probe.getLabel(),
                        probe.getName(),
                        probe.getType(),
                        0.0,
                        0,
                        0.0,
                        false,
                        false));
            }
        }
        return list;
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
            socket.emit(ServerConstants.GE_GET_DASH_DATA, true);
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
        if (dashAdapter != null) {
            DashProbe grillProbe = dashAdapter.getGrillProbe();
            ProbeOptionsModel optionsModel = new ProbeOptionsModel(false, false);
            if (grillProbe != null) {
                tempPickerDialog = new TempPickerDialog(requireActivity(), grillProbe, optionsModel,
                        tempUtils.getDefaultGrillTemp(), true, true,
                        DashboardFragment.this);
                tempPickerDialog.showDialog();
            }
        }
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
    public void onTempConfirmClicked(DashProbe probe, ProbeOptionsModel probeOptions, String temp,
                                     boolean hold) {
        if (socket != null && notifyData != null) {
            if (hold) {
                ServerControl.setGrillHoldTemp(socket, temp, this::processPostResponse);
            }
            probe.setKeepWarm(probeOptions.getKeepWarm());
            probe.setShutdown(probeOptions.getShutdown());
            ServerControl.setProbeNotify(socket, probe, notifyData, temp, hold,
                    this::processPostResponse);
        }
    }

    @Override
    public void onTempClearClicked(DashProbe probe) {
        if (socket != null && notifyData != null) {
            ServerControl.clearProbeNotify(socket, probe, notifyData, this::processPostResponse);
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

    @Override
    public void onProbeClick(DashProbe probe) {
        switch (probe.getType()) {
            case Constants.DASH_PROBE_PRIMARY -> {
                if (socketConnected()) {
                    int defaultTemp = tempUtils.getDefaultGrillTemp();
                    boolean hold = probe.getSetTemp() > 0.0;
                    if (hold) {
                        defaultTemp = probe.getSetTemp().intValue();
                    } else if (probe.getTarget() > 0) {
                        defaultTemp = probe.getTarget();
                    }
                    ProbeOptionsModel probeOptions = new ProbeOptionsModel(false, false);
                    tempPickerDialog = new TempPickerDialog(requireActivity(), probe, probeOptions,
                            defaultTemp, hold, false, DashboardFragment.this);
                    tempPickerDialog.showDialog();
                }
            }
            case Constants.DASH_PROBE_FOOD -> {
                if (socketConnected()) {
                    int defaultTemp = tempUtils.getDefaultProbeTemp();
                    if (probe.getTarget() > 0) {
                        defaultTemp = probe.getTarget();
                    }
                    ProbeOptionsModel probeOptions = new ProbeOptionsModel(false, false);
                    tempPickerDialog = new TempPickerDialog(requireActivity(), probe, probeOptions,
                            defaultTemp, false, false, DashboardFragment.this);
                    tempPickerDialog.showDialog();
                }
            }
        }
    }

    @Override
    public void onProbeLongClick(DashProbe probe) {
        if (socketConnected()) {
            if (probe.getType().equals(Constants.DASH_PROBE_PRIMARY)) {
                int defaultTemp = tempUtils.getDefaultGrillTemp();
                if (probe.getValue() > 0) {
                    defaultTemp = probe.getTarget();
                }
                ProbeOptionsModel probeOptions = new ProbeOptionsModel(false, false);
                tempPickerDialog = new TempPickerDialog(getActivity(), probe, probeOptions, defaultTemp,
                        false, false, DashboardFragment.this);
                tempPickerDialog.showDialog();
            }
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

            DashProbeInfo dashProbeInfo = dashDataModel.getDashProbeInfo();
            TimerInfo timerInfo = dashDataModel.getTimerInfo();
            notifyData = dashDataModel.getNotifyData();

            String currentMode = dashDataModel.getCurrentMode();
            Double grillTarget = dashProbeInfo.getPrimarySetPoint();
            long timerStartTime = timerInfo.getTimerStartTime();
            long timerEndTime = timerInfo.getTimerEndTime();
            long timerPauseTime = timerInfo.getTimerPauseTime();
            Integer hopperLevel = dashDataModel.getHopperLevel();
            Boolean smokePlus = dashDataModel.getSmokePlus();
            Boolean pwmControl = dashDataModel.getPwmControl();
            Boolean timerPaused = timerInfo.getTimerPaused();
            Boolean timerActive = timerInfo.getTimerActive();

            List<DashProbe> probes = dashAdapter.getDashProbes();

            for (DashProbe probe : probes) {
                // Grill Probe
                for (Map.Entry<String, Double> gProbe : dashProbeInfo.getPrimaryProbe().entrySet()) {
                    if (gProbe.getKey() != null && gProbe.getKey().equals(probe.getLabel())) {
                        probe.setValue(gProbe.getValue());
                        probe.setSetTemp(dashProbeInfo.getPrimarySetPoint());
                        dashAdapter.updateProbe(probe);
                    }
                }

                // Food Probes
                for (Map.Entry<String, Double> fProbe : dashProbeInfo.getFoodProbes().entrySet()) {
                    if (fProbe.getKey() != null && fProbe.getKey().equals(probe.getLabel())) {
                        probe.setValue(fProbe.getValue());
                        dashAdapter.updateProbe(probe);
                    }
                }

                // Target/Notification Temps
                for (NotifyData notifyData : dashDataModel.getNotifyData()) {
                    if (notifyData.getType().equals("probe")) {
                        if (notifyData.getLabel().equals(probe.getLabel())) {
                            if (!probe.getTarget().equals((notifyData.getTarget()))) {
                                probe.setTarget(notifyData.getTarget());
                            }
                            if (probe.getShutdown() != notifyData.getShutdown()) {
                                probe.setShutdown(notifyData.getShutdown());
                            }
                            if (probe.getKeepWarm() != notifyData.getKeepWarm()) {
                                probe.setKeepWarm(notifyData.getKeepWarm());
                            }
                            dashAdapter.updateProbe(probe);
                        }
                    }
                }
            }

            // Timer Notifications
            for (NotifyData notifyData : notifyData) {
                if (notifyData.getType().equals("timer")) {
                    if (notifyData.getLabel().equals("Timer")) {
                        AnimUtils.fadeAnimation(timerShutdown, 300,
                                notifyData.getShutdown() ?
                                        Constants.FADE_IN : Constants.FADE_OUT);
                        AnimUtils.fadeAnimation(timerKeepWarm, 300,
                                notifyData.getKeepWarm() ?
                                        Constants.FADE_IN : Constants.FADE_OUT);
                    }
                }
            }

            if (NullUtils.checkObjectNotNull(currentMode, smokePlus, hopperLevel, grillTarget)) {
                this.currentMode = currentMode;
                if (currentMode.equals(Constants.GRILL_CURRENT_STOP)) {
                    currentModeText.setText(R.string.off);
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

    private void setOfflineMode() {
        if (dashAdapter != null) {
            for (DashProbe probe : dashAdapter.getDashProbes()) {
                probe.setSetTemp(0.0);
                probe.setTarget(0);
                probe.setValue(0.0);
                probe.setKeepWarm(false);
                probe.setShutdown(false);
                dashAdapter.updateProbe(probe);
            }
        }

        stopTimer();
        currentMode = Constants.GRILL_CURRENT_STOP;
        currentModeText.setText(R.string.off);
        smokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
        smokePlusText.setText(R.string.off);
        smokePlusEnabled = false;
        pwmControlBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
        pwmControlText.setText(R.string.off);
        pwmControlEnabled = false;
        timerCountDownText.setText(R.string.placeholder_time);
        pelletLevelText.setText(R.string.placeholder_percentage);
        if (getActivity() != null) {
            pelletLevelText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        }
        AnimUtils.fadeAnimation(timerShutdown, 300, Constants.FADE_OUT);
    }
}