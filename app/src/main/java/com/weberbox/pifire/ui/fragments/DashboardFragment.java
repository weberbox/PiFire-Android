package com.weberbox.pifire.ui.fragments;

import android.app.Activity;
import android.content.res.ColorStateList;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.databinding.FragmentDashboardBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;
import com.weberbox.pifire.model.GrillResponseModel;
import com.weberbox.pifire.model.GrillResponseModel.NotifyReq;
import com.weberbox.pifire.model.GrillResponseModel.NotifyData;
import com.weberbox.pifire.model.GrillResponseModel.ProbeTemps;
import com.weberbox.pifire.model.GrillResponseModel.ProbesEnabled;
import com.weberbox.pifire.model.GrillResponseModel.SetPoints;
import com.weberbox.pifire.model.GrillResponseModel.TimerInfo;
import com.weberbox.pifire.ui.dialogs.ProbeToggleDialog;
import com.weberbox.pifire.ui.dialogs.RunModeActionDialog;
import com.weberbox.pifire.ui.dialogs.StartModeActionDialog;
import com.weberbox.pifire.ui.dialogs.TemperaturePickerDialog;
import com.weberbox.pifire.ui.dialogs.TimerActionDialog;
import com.weberbox.pifire.ui.dialogs.TimerPickerDialog;
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.TextTransition;
import com.weberbox.pifire.ui.views.PelletLevelView;
import com.weberbox.pifire.utils.NullUtils;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.ui.utils.CountDownTimer;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import timber.log.Timber;

public class DashboardFragment extends Fragment implements DashboardCallbackInterface {

    private FragmentDashboardBinding mBinding;
    private TextView mTimerCountDownText;
    private TextView mGrillTempText;
    private TextView mProbeOneTempText;
    private TextView mProbeTwoTempText;
    private TextView mGrillSetText;
    private TextView mProbeOneTargetText;
    private TextView mProbeTwoTargetText;
    private TextView mPelletLevelText;
    private TextView mCurrentStatusText;
    private TextView mSmokePlusText;
    private TextView mGrillTargetText;
    private ImageView mProbeOneShutdown;
    private ImageView mProbeTwoShutdown;
    private ImageView mTimerShutdown;
    private ProgressBar mGrillTempProgress;
    private ProgressBar mProbeOneProgress;
    private ProgressBar mProbeTwoProgress;
    private ProgressBar mTimerProgress;
    private ProgressBar mLoadingBar;
    private LinearLayout mSmokePlusBox;
    private SwipeRefreshLayout mSwipeRefresh;
    private TemperaturePickerDialog mTemperaturePickerDialog;
    private FrameLayout mTimerPausedLayout;
    private TableLayout mRootContainer;
    private Snackbar mErrorSnack;
    private CountDownTimer mCountDownTimer;
    private PelletLevelView mPelletLevelIndicator;
    private Socket mSocket;

    private Boolean mSmokePlusEnabled = false;

    private String mCurrentMode = Constants.GRILL_CURRENT_STOP;
    private boolean mIsLoading = false;


    public DashboardFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentDashboardBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mErrorSnack = Snackbar.make(view, R.string.control_send_error, Snackbar.LENGTH_LONG);

        mLoadingBar = mBinding.connectProgressbar;
        mRootContainer = mBinding.dashLayout.grillControlsTable;

        mTimerPausedLayout = mBinding.dashLayout.grillTimerPauseContainer;
        mTimerCountDownText = mBinding.dashLayout.grillTimerTime;
        mTimerProgress = mBinding.dashLayout.grillTimerProgress;

        mProbeOneShutdown = mBinding.dashLayout.probeOneShutdown;
        mProbeTwoShutdown = mBinding.dashLayout.probeTwoShutdown;
        mTimerShutdown = mBinding.dashLayout.timerShutdown;

        mGrillTempText = mBinding.dashLayout.controlsGrillTemp;
        mProbeOneTempText = mBinding.dashLayout.controlsProbeOneTemp;
        mProbeTwoTempText = mBinding.dashLayout.controlsProbeTwoTemp;

        mGrillSetText = mBinding.dashLayout.controlsGrillSetTemp;
        mGrillTargetText = mBinding.dashLayout.controlsGrillTargetTemp;
        mProbeOneTargetText = mBinding.dashLayout.controlsProbeOneTargetTemp;
        mProbeTwoTargetText = mBinding.dashLayout.controlsProbeTwoTargetTemp;

        mGrillTempProgress = mBinding.dashLayout.controlsGrillTempProgress;
        mProbeOneProgress = mBinding.dashLayout.controlsProbeOneTempProgress;
        mProbeTwoProgress = mBinding.dashLayout.controlsProbeTwoTempProgress;

        mPelletLevelText = mBinding.dashLayout.controlsPelletLevel;
        mCurrentStatusText = mBinding.dashLayout.controlsGrillMode;
        mSmokePlusText = mBinding.dashLayout.controlsSmokePlus;
        mSmokePlusBox = mBinding.dashLayout.smokePlusStatusContainer;
        LinearLayout currentModeBox = mBinding.dashLayout.grillStatusContainer;
        LinearLayout grillTempBox = mBinding.dashLayout.grillTempContainer;
        LinearLayout probeOneTempBox = mBinding.dashLayout.probeOneContainer;
        LinearLayout probeTwoTempBox = mBinding.dashLayout.probeTwoContainer;
        FrameLayout timerBox = mBinding.dashLayout.grillTimerContainer;
        FrameLayout pelletLevelBox = mBinding.dashLayout.controlsPelletLevelContainer;
        mPelletLevelIndicator = mBinding.dashLayout.pelletLevelIndicator;

        mSwipeRefresh = mBinding.dashPullRefresh;

        mSwipeRefresh.setOnRefreshListener(() -> {
            if (mSocket != null && mSocket.connected()) {
                requestForcedDashData(false);
            } else {
                mSwipeRefresh.setRefreshing(false);
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        currentModeBox.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                if (mCurrentMode.equals(Constants.GRILL_CURRENT_STOP) ||
                        mCurrentMode.equals(Constants.GRILL_CURRENT_MONITOR)) {
                    StartModeActionDialog startModeActionDialog = new StartModeActionDialog(getActivity(),
                            DashboardFragment.this);
                    startModeActionDialog.showDialog();
                } else {
                    RunModeActionDialog runModeActionDialog = new RunModeActionDialog(getActivity(),
                            DashboardFragment.this,
                            mCurrentMode.equals(Constants.GRILL_CURRENT_SHUTDOWN));
                    runModeActionDialog.showDialog();
                }
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        mSmokePlusBox.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                if (mCurrentMode.equals(Constants.GRILL_CURRENT_HOLD)
                        || mCurrentMode.equals(Constants.GRILL_CURRENT_SMOKE)) {
                    GrillControl.setSmokePlus(mSocket, !mSmokePlusEnabled);
                } else {
                    if (getActivity() != null) {
                        showSnackBarMessage(getActivity(), R.string.control_smoke_plus_disabled,
                                false);
                    }
                }
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        grillTempBox.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                int defaultTemp = Constants.DEFAULT_GRILL_TEMP_SET;
                if (!mGrillSetText.getText().toString().equals(getString(
                        R.string.placeholder_none))) {
                    String temp = mGrillSetText.getText().toString()
                            .replaceAll(getString(R.string.regex_numbers), "");
                    defaultTemp = Integer.parseInt(temp);

                } else if (!mGrillTargetText.getText().toString().equals(getString(
                        R.string.placeholder_none))) {
                    String temp = mGrillTargetText.getText().toString()
                            .replaceAll(getString(R.string.regex_numbers), "");
                    defaultTemp = Integer.parseInt(temp);
                }
                mTemperaturePickerDialog = new TemperaturePickerDialog(getActivity(),
                        DashboardFragment.this, Constants.PICKER_TYPE_GRILL,
                        defaultTemp, false);
                mTemperaturePickerDialog.showDialog();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        probeOneTempBox.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                int defaultTemp = Constants.DEFAULT_PROBE_TEMP_SET;

                if (!mProbeOneTempText.getText().toString().equals(getString(R.string.off))) {
                    if (!mProbeOneTargetText.getText().toString().equals(
                            getString(R.string.placeholder_none))) {
                        String temp = mProbeOneTargetText.getText().toString()
                                .replaceAll(getString(R.string.regex_numbers), "");
                        defaultTemp = Integer.parseInt(temp);
                    }
                    mTemperaturePickerDialog = new TemperaturePickerDialog(getActivity(),
                            DashboardFragment.this, Constants.PICKER_TYPE_PROBE_ONE,
                            defaultTemp, false);
                    mTemperaturePickerDialog.showDialog();
                }
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        probeOneTempBox.setOnLongClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                ProbeToggleDialog probeToggleDialog = new ProbeToggleDialog(getActivity(),
                        DashboardFragment.this, Constants.ACTION_MODE_PROBE_ONE,
                        !mProbeOneTempText.getText().toString().equals(getString(R.string.off)));
                probeToggleDialog.showDialog();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
            return true;
        });

        probeTwoTempBox.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                int defaultTemp = Constants.DEFAULT_PROBE_TEMP_SET;
                if (!mProbeTwoTempText.getText().toString().equals(getString(R.string.off))) {
                    if (!mProbeTwoTargetText.getText().toString().equals("--")) {
                        String temp = mProbeTwoTargetText.getText().toString()
                                .replaceAll(getString(R.string.regex_numbers), "");
                        defaultTemp = Integer.parseInt(temp);
                    }
                    mTemperaturePickerDialog = new TemperaturePickerDialog(getActivity(),
                            DashboardFragment.this, Constants.PICKER_TYPE_PROBE_TWO,
                            defaultTemp, false);
                    mTemperaturePickerDialog.showDialog();
                }
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        probeTwoTempBox.setOnLongClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                ProbeToggleDialog probeToggleDialog = new ProbeToggleDialog(getActivity(),
                        DashboardFragment.this, Constants.ACTION_MODE_PROBE_TWO,
                        !mProbeTwoTempText.getText().toString().equals(getString(R.string.off)));
                probeToggleDialog.showDialog();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
            return true;
        });

        timerBox.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                if (mCountDownTimer != null && mCountDownTimer.isActive()) {
                    TimerActionDialog timerActionDialog = new TimerActionDialog(getActivity(),
                            DashboardFragment.this, mCountDownTimer.isPaused());
                    timerActionDialog.showDialog();
                } else {
                    TimerPickerDialog timerPickerDialog = new TimerPickerDialog(getActivity(),
                            DashboardFragment.this);
                    timerPickerDialog.showDialog();
                }
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        pelletLevelBox.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                GrillControl.setCheckHopperLevel(mSocket);
                requestForcedDashData(true);
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        mCountDownTimer = new CountDownTimer() {
            @Override
            public void onDuration(int duration) {
                if (mTimerProgress.getMax() != duration) {
                    mTimerProgress.setMax(duration);
                }
            }

            @Override
            public void onRemainingTime(String timeRemaining) {
                mTimerCountDownText.setText(timeRemaining);
            }

            @Override
            public void onTimerTick(int secondsUntilFinished) {
                mTimerProgress.setProgress(secondsUntilFinished);
            }

            @Override
            public void onFinished() {
                mTimerProgress.setProgress(0);
                mTimerCountDownText.setText(R.string.placeholder_time);
            }
        };

        if (getActivity() != null) {
            MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mainViewModel.getDashData().observe(getViewLifecycleOwner(), dashData -> {
                mIsLoading = false;
                mSwipeRefresh.setRefreshing(false);
                if (dashData != null) {
                    updateUIWithData(dashData);
                }
            });

            mainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                if (enabled != null && enabled) {
                    if (!mIsLoading) {
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
        mBinding = null;
    }

    private void requestDataUpdate() {
        if (mSocket != null && !mIsLoading) {
            mIsLoading = true;
            requestForcedDashData(true);
        }
    }

    private void requestForcedDashData(boolean showLoading) {
        toggleLoading(showLoading);
        if (mSocket != null) {
            mSocket.emit(ServerConstants.REQUEST_GRILL_DATA, true);
        }
    }

    private void toggleLoading(boolean show) {
        if (show && mSocket != null) {
            if (getBannerVisibility() == View.GONE) {
                mLoadingBar.setVisibility(View.VISIBLE);
            }
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    private int getBannerVisibility() {
        if (getActivity() != null && getActivity().findViewById(R.id.offline_banner) != null) {
            return getActivity().findViewById(R.id.offline_banner).getVisibility();
        }
        return View.GONE;
    }

    private void checkForceScreenOn() {
        if (getActivity() != null) {
            if (Prefs.getBoolean(getString(R.string.prefs_keep_screen_on),
                    getResources().getBoolean(R.bool.def_keep_screen_on))){
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    private void clearForceScreenOn() {
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onModeActionClicked(int mode) {
        if (mSocket != null) {
            switch (mode) {
                case Constants.ACTION_MODE_START:
                    GrillControl.modeStartGrill(mSocket);
                    break;
                case Constants.ACTION_MODE_MONITOR:
                    GrillControl.modeMonitorGrill(mSocket);
                    break;
                case Constants.ACTION_MODE_STOP:
                    GrillControl.modeStopGrill(mSocket);
                    break;
                case Constants.ACTION_MODE_SMOKE:
                    GrillControl.modeSmokeGrill(mSocket);
                    break;
                case Constants.ACTION_MODE_HOLD:
                    mTemperaturePickerDialog = new TemperaturePickerDialog(getActivity(),
                            DashboardFragment.this, Constants.PICKER_TYPE_GRILL,
                            Constants.DEFAULT_GRILL_TEMP_SET, true);
                    mTemperaturePickerDialog.showDialog();
                    break;
                case Constants.ACTION_MODE_SHUTDOWN:
                    GrillControl.modeShutdownGrill(mSocket);
                    break;
                case Constants.ACTION_MODE_PROBE_ONE:
                    GrillControl.probeOneToggle(mSocket,
                            mProbeOneTempText.getText().toString().equals(getString(R.string.off)));
                    break;
                case Constants.ACTION_MODE_PROBE_TWO:
                    GrillControl.probeTwoToggle(mSocket,
                            mProbeTwoTempText.getText().toString().equals(getString(R.string.off)));
                    break;
            }
        }
    }

    @Override
    public void onTempConfirmClicked(int type, String temp, boolean hold, boolean shutdown) {
        if (mSocket != null) {
            if (hold && type == Constants.PICKER_TYPE_GRILL) {
                GrillControl.setGrillTemp(mSocket, temp);
            }
            GrillControl.setProbeNotify(mSocket, type, temp, shutdown);
        }
    }

    @Override
    public void onTempClearClicked(int type) {
        if (mSocket != null) {
            GrillControl.clearProbeNotify(mSocket, type);
        }
    }

    @Override
    public void onTimerActionClicked(int type) {
        if (mSocket != null) {
            GrillControl.setTimerAction(mSocket, type);
        }
    }

    @Override
    public void onTimerConfirmClicked(String hours, String minutes, Boolean shutdown) {
        if (mSocket != null) {
            GrillControl.setTimerTime(mSocket, hours, minutes, shutdown);
        }
    }

    public void updateUIWithData(String response_data) {
        GrillResponseModel grillResponseModel;

        try {
            grillResponseModel = GrillResponseModel.parseJSON(response_data);

            ProbeTemps probeTemps = grillResponseModel.getProbeTemps();
            ProbesEnabled probesEnabled = grillResponseModel.getProbesEnabled();
            SetPoints setPoints = grillResponseModel.getSetPoints();
            NotifyReq notifyReq = grillResponseModel.getNotifyReq();
            NotifyData notifyData = grillResponseModel.getNotifyData();
            TimerInfo timerInfo = grillResponseModel.getTimerInfo();

            String currentMode = grillResponseModel.getCurrentMode();
            long timerStartTime = timerInfo.getTimerStartTime();
            long timerEndTime = timerInfo.getTimerEndTime();
            long timerPauseTime = timerInfo.getTimerPauseTime();
            int grillTemp = probeTemps.getGrillTemp();
            int probeOneTemp = probeTemps.getProbeOneTemp();
            int probeTwoTemp = probeTemps.getProbeTwoTemp();
            int grillTarget = setPoints.getGrillTarget();
            int probeOneTarget = setPoints.getProbeOneTarget();
            int probeTwoTarget = setPoints.getProbeTwoTarget();
            int hopperLevel = grillResponseModel.getHopperLevel();
            boolean grillEnabled = probesEnabled.getGrillEnabled();
            boolean probeOneEnabled = probesEnabled.getProbeOneEnabled();
            boolean probeTwoEnabled = probesEnabled.getProbeTwoEnabled();
            boolean grillNotify = notifyReq.getGrillNotify();
            boolean probeOneNotify = notifyReq.getProbeOneNotify();
            boolean probeTwoNotify = notifyReq.getProbeTwoNotify();
            boolean smokePlus = grillResponseModel.getSmokePlus();
            boolean timerPaused = timerInfo.getTimerPaused();
            boolean timerActive = timerInfo.getTimerActive();
            boolean probeOneShutdown = notifyData.getP1Shutdown();
            boolean probeTwoShutdown = notifyData.getP2Shutdown();
            boolean timerShutdown = notifyData.getTimerShutdown();


            TransitionManager.beginDelayedTransition(mRootContainer, new TextTransition());

            if (NullUtils.checkObjectNotNull(currentMode, smokePlus, hopperLevel, grillTarget)) {
                mCurrentMode = currentMode;
                if (currentMode.equals(Constants.GRILL_CURRENT_STOP)) {
                    mCurrentStatusText.setText(R.string.off);
                    mGrillTempProgress.setProgress(0);
                    mProbeOneProgress.setProgress(0);
                    mProbeTwoProgress.setProgress(0);
                } else {
                    mCurrentStatusText.setText(currentMode);
                }

                if (currentMode.equals(Constants.GRILL_CURRENT_HOLD) |
                        currentMode.equals(Constants.GRILL_CURRENT_SMOKE) && smokePlus) {
                    mSmokePlusEnabled = true;
                    mSmokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_enabled);
                    mSmokePlusText.setText(R.string.on);
                } else {
                    mSmokePlusEnabled = false;
                    mSmokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
                    mSmokePlusText.setText(R.string.off);
                }

                if (currentMode.equals(Constants.GRILL_CURRENT_HOLD) && grillTarget > 0) {
                    mGrillSetText.setText(StringUtils.formatTemp(grillTarget));
                } else {
                    mGrillSetText.setText(R.string.placeholder_none);
                }

                if (hopperLevel > 0) {
                    mPelletLevelIndicator.setLevel(hopperLevel);
                    mPelletLevelText.setText(StringUtils.formatPercentage(hopperLevel));
                    if (getActivity() != null) {
                        int color = hopperLevel < Constants.LOW_PELLET_WARNING ?
                                R.color.colorPelletDanger : R.color.colorWhite;
                        mPelletLevelText.setTextColor(ContextCompat.getColor(getActivity(), color));
                    }
                } else {
                    mPelletLevelText.setText(R.string.placeholder_percentage);
                }
            }

            if (NullUtils.checkObjectNotNull(grillEnabled, grillNotify, grillTarget, grillTemp)) {
                if (grillEnabled) {
                    if (grillNotify && grillTarget > 0) {
                        mGrillTempProgress.setMax(grillTarget);
                        mGrillTargetText.setText(StringUtils.formatTemp(grillTarget));
                    } else {
                        mGrillTempProgress.setMax(Constants.MAX_GRILL_TEMP_SET);
                        mGrillTargetText.setText(R.string.placeholder_none);
                    }

                    if (grillTemp > 0) {
                        mGrillTempProgress.setProgress(grillTemp);
                        mGrillTempText.setText(StringUtils.formatTemp(grillTemp));
                    } else {
                        mGrillTempText.setText(R.string.placeholder_temp);
                    }
                } else {
                    mGrillTempProgress.setMax(0);
                    mGrillTargetText.setText(R.string.placeholder_none);
                    mGrillTempText.setText(R.string.off);
                }
            }

            if (NullUtils.checkObjectNotNull(probeOneEnabled, probeOneNotify, probeOneTarget,
                    probeOneTemp)) {
                if (probeOneEnabled) {
                    if (probeOneNotify && probeOneTarget > 0) {
                        mProbeOneProgress.setMax(probeOneTarget);
                        mProbeOneTargetText.setText(StringUtils.formatTemp(probeOneTarget));
                    } else {
                        mProbeOneProgress.setMax(Constants.MAX_PROBE_TEMP_SET);
                        mProbeOneTargetText.setText(R.string.placeholder_none);
                    }

                    if (probeOneTemp > 0) {
                        mProbeOneProgress.setProgress(probeOneTemp);
                        mProbeOneTempText.setText(StringUtils.formatTemp(probeOneTemp));
                    } else {
                        mProbeOneTempText.setText(R.string.placeholder_temp);
                    }
                } else {
                    mProbeOneProgress.setMax(0);
                    mProbeOneTargetText.setText(R.string.placeholder_none);
                    mProbeOneTempText.setText(R.string.off);
                }
            }

            if (NullUtils.checkObjectNotNull(probeTwoEnabled, probeTwoNotify, probeTwoTarget,
                    probeTwoTemp)) {
                if (probeTwoEnabled) {
                    if (probeTwoNotify && probeTwoTarget > 0) {
                        mProbeTwoProgress.setMax(probeTwoTarget);
                        mProbeTwoTargetText.setText(StringUtils.formatTemp(probeTwoTarget));
                    } else {
                        mProbeTwoProgress.setMax(Constants.MAX_PROBE_TEMP_SET);
                        mProbeTwoTargetText.setText(R.string.placeholder_none);
                    }

                    if (probeTwoTemp > 0) {
                        mProbeTwoProgress.setProgress(probeTwoTemp);
                        mProbeTwoTempText.setText(StringUtils.formatTemp(probeTwoTemp));
                    } else {
                        mProbeTwoTempText.setText(R.string.placeholder_temp);
                    }
                } else {
                    mProbeTwoProgress.setMax(0);
                    mProbeTwoTargetText.setText(R.string.placeholder_none);
                    mProbeTwoTempText.setText(R.string.off);
                }
            }

            if (NullUtils.checkObjectNotNull(timerActive, timerStartTime, timerEndTime,
                    timerPauseTime, timerPaused, mCountDownTimer)) {
                if (timerActive) {
                    mCountDownTimer.startTimer(timerStartTime, timerEndTime, timerPauseTime);
                    toggleTimerPaused(timerPaused);
                    if (timerPaused) {
                        mCountDownTimer.pauseTimer();
                        mTimerCountDownText.setText(mCountDownTimer.formatTimeRemaining(
                                TimeUnit.SECONDS.toMillis(timerEndTime) -
                                        TimeUnit.SECONDS.toMillis(timerPauseTime)));
                    } else {
                        mCountDownTimer.resumeTimer();
                    }
                } else {
                    stopTimer();
                }
            }

            if (NullUtils.checkObjectNotNull(probeOneShutdown, probeTwoShutdown, timerShutdown)) {
                AnimUtils.fadeAnimation(mProbeOneShutdown, 300, probeOneShutdown ?
                        Constants.FADE_IN : Constants.FADE_OUT);
                AnimUtils.fadeAnimation(mProbeTwoShutdown, 300, probeTwoShutdown ?
                        Constants.FADE_IN : Constants.FADE_OUT);
                AnimUtils.fadeAnimation(mTimerShutdown, 300, timerShutdown ?
                        Constants.FADE_IN : Constants.FADE_OUT);
            }

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e, "JSON Error");
            if (getActivity() != null) {
                showSnackBarMessage(getActivity(), R.string.json_error_dash, true);
            }
        }

        toggleLoading(false);
    }

    private void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.stopTimer();
        }
        toggleTimerPaused(false);
        mTimerProgress.setProgress(0);
        mTimerCountDownText.setText(R.string.placeholder_time);
    }

    private void toggleTimerPaused(boolean show) {
        if (show) {
            AnimUtils.fadeInAnimation(mTimerPausedLayout, 300);
        } else {
            AnimUtils.fadeOutAnimation(mTimerPausedLayout, 300);
        }
    }

    private void setOfflineMode() {
        stopTimer();
        mCurrentMode = Constants.GRILL_CURRENT_STOP;
        mGrillTempProgress.setProgress(0);
        mProbeOneProgress.setProgress(0);
        mProbeTwoProgress.setProgress(0);
        mCurrentStatusText.setText(R.string.off);
        mSmokePlusBox.setBackgroundResource(R.drawable.bg_ripple_smokep_disabled);
        mSmokePlusText.setText(R.string.off);
        mGrillTempText.setText(R.string.placeholder_temp);
        mGrillSetText.setText(R.string.placeholder_none);
        mGrillTargetText.setText(R.string.placeholder_none);
        mProbeOneTargetText.setText(R.string.placeholder_none);
        mProbeOneTempText.setText(R.string.placeholder_temp);
        mProbeTwoTargetText.setText(R.string.placeholder_none);
        mProbeTwoTempText.setText(R.string.placeholder_temp);
        mTimerCountDownText.setText(R.string.placeholder_time);
        mPelletLevelText.setText(R.string.placeholder_percentage);
        AnimUtils.fadeAnimation(mProbeOneShutdown, 300, Constants.FADE_OUT);
        AnimUtils.fadeAnimation(mProbeTwoShutdown, 300, Constants.FADE_OUT);
        AnimUtils.fadeAnimation(mTimerShutdown, 300, Constants.FADE_OUT);
    }

    private void showSnackBarMessage(Activity activity, int message, boolean error) {
        if (error) {
            mErrorSnack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(
                    R.color.colorAccentRed)));
        }
        mErrorSnack.setTextColor(activity.getColor(R.color.colorWhite));
        mErrorSnack.setText(message);
        mErrorSnack.show();
    }
}