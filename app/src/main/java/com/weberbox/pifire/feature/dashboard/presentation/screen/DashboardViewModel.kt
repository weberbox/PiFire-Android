package com.weberbox.pifire.feature.dashboard.presentation.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.model.dashboard.DashData.Dash
import com.weberbox.pifire.core.model.dashboard.DashData.Dash.Outputs
import com.weberbox.pifire.core.model.dashboard.DashData.Dash.Probe
import com.weberbox.pifire.core.model.dashboard.DashData.Dash.Timer
import com.weberbox.pifire.core.model.dashboard.RunningMode
import com.weberbox.pifire.core.repo.PrefsRepo
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.dashboard.data.model.ProbeNotify
import com.weberbox.pifire.feature.dashboard.data.repo.DashRepo
import com.weberbox.pifire.feature.dashboard.presentation.contract.DashContract
import com.weberbox.pifire.feature.dashboard.presentation.contract.DashContract.DashEvent
import com.weberbox.pifire.feature.dashboard.presentation.model.ElapsedData
import com.weberbox.pifire.feature.dashboard.presentation.model.TimerData
import com.weberbox.pifire.feature.dashboard.presentation.util.CountDownTimer
import com.weberbox.pifire.feature.dashboard.presentation.util.ElapsedTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dashRepo: DashRepo,
    private val settingsRepo: SettingsRepo,
    private val prefsRepo: PrefsRepo,
) : BaseViewModel<DashContract.Event, DashContract.State, DashContract.Effect>() {
    private var showHoldTempTip by mutableStateOf(false)
    private val timerIntent = CountDownTimer(timerScope = viewModelScope)
    private val elapsedIntent = ElapsedTimer(timerScope = viewModelScope)

    // Separate from the screen state so the per-second ticks only recompose the timer cards
    val timerData: StateFlow<TimerData> = timerIntent.timerState
    val elapsedData: StateFlow<ElapsedData> = elapsedIntent.elapsedState

    init {
        collectPrefsFlow()
        collectConnectedState()
        listenDashData()
        getDashData(forced = false)
    }

    override fun setInitialState() = DashContract.State(
        dash = Dash(),
        incrementTemps = true,
        keepScreenOn = false,
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
        isRefreshing = false,
        isConnected = false,
        holdTempToolTip = false,
    )

    override fun handleEvents(event: DashContract.Event) {
        when (event) {
            is DashContract.Event.Refresh -> getDashData(forced = true)
            is DashContract.Event.PWMControl -> sendPwmControl()
            is DashContract.Event.SmokePlus -> sendSmokePlus()
            is DashContract.Event.SendEvent -> sendDashEvent(event.event)
            is DashContract.Event.ProbeNotify -> setProbeNotify(event.probeNotify)
            is DashContract.Event.HideHoldTempTip -> hideHoldTempTip()
            is DashContract.Event.ToggleLidDetect -> toggleLidOpen()
            is DashContract.Event.RestartControl -> restartControlDialog()
            is DashContract.Event.SetStartupHoldTemp -> setStartupHoldTemp(event.temp)
            is DashContract.Event.CheckHopperLevel -> checkHopperLevel()
            is DashContract.Event.StartUpRequested -> startUpRequested(event.holdDialogSupported)
        }
    }

    private fun sendDashEvent(event: DashEvent) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                when (event) {
                    is DashEvent.Monitor -> dashRepo.sendMonitorMode()

                    is DashEvent.RecipeUnPause -> dashRepo.sendRecipeUnPause()

                    is DashEvent.Shutdown -> dashRepo.sendShutdownMode()

                    is DashEvent.Smoke -> dashRepo.sendSmokeMode()

                    is DashEvent.Start -> dashRepo.sendStartGrill()

                    is DashEvent.Stop -> dashRepo.sendStopMode()

                    is DashEvent.GrillHoldTemp -> dashRepo.setGrillHoldTemp(event.temp)

                    is DashEvent.PrimeGrill -> dashRepo.sendPrimeGrill(
                        primeAmount = event.primeAmount,
                        nextMode = event.nextMode,
                    )

                    is DashEvent.TimerAction -> dashRepo.sendTimerAction(action = event.action)

                    is DashEvent.TimerTime -> dashRepo.sendTimerTime(
                        hours = event.hours,
                        minutes = event.minutes,
                        shutdown = event.shutdown,
                        keepWarm = event.keepWarm,
                    )

                    is DashEvent.ToggleFan -> dashRepo.sendToggleFan(enabled = event.enabled)

                    is DashEvent.ToggleAuger -> dashRepo.sendToggleAuger(enabled = event.enabled)

                    is DashEvent.ToggleIgniter -> dashRepo.sendToggleIgniter(enabled = event.enabled)

                    else -> Result.Success(data = Unit)
                },
            )
        }
    }

    private fun collectPrefsFlow() {
        viewModelScope.launch {
            prefsRepo.prefFlow(Pref.incrementTemps).collect {
                setState {
                    copy(
                        incrementTemps = it,
                    )
                }
            }
        }
        viewModelScope.launch {
            prefsRepo.prefFlow(Pref.keepScreenOn).collect {
                setState {
                    copy(
                        keepScreenOn = it,
                    )
                }
            }
        }
        viewModelScope.launch {
            prefsRepo.prefFlow(Pref.holdTempToolTip).collect {
                showHoldTempTip = it
            }
        }
    }

    private fun collectConnectedState() {
        viewModelScope.launch {
            sessionStateHolder.isConnectedState.collect { connected ->
                setState {
                    copy(isConnected = connected)
                }
                if (!connected) {
                    setState {
                        copy(
                            dash = buildOfflineDash(null),
                        )
                    }
                    toggleTimer(Timer())
                    toggleElapsed(0.0)
                }
            }
        }
    }

    private fun listenDashData() {
        viewModelScope.launch(Dispatchers.IO) {
            dashRepo.listenDashData().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Error -> {
                            if (result.error != DataError.Network.SOCKET_ERROR) {
                                setEffect {
                                    DashContract.Effect.Notification(
                                        text = result.error.asUiText(),
                                        error = true,
                                    )
                                }
                            }
                            setState { copy(isRefreshing = false, isLoading = false) }
                        }

                        is Result.Success -> {
                            checkShowGrillHoldToolTip(result.data)
                            setState {
                                copy(
                                    dash = result.data,
                                    isInitialLoading = false,
                                    isLoading = false,
                                    isRefreshing = false,
                                )
                            }
                            toggleElapsed(result.data.startupTimestamp.toDouble())
                            toggleTimer(result.data.timer)
                        }
                    }
                }
            }
        }
    }

    private fun checkShowGrillHoldToolTip(dash: Dash) {
        if (dash.currentMode == RunningMode.Hold.name && showHoldTempTip) {
            setState {
                copy(
                    holdTempToolTip = true,
                )
            }
        }
    }

    private fun hideHoldTempTip() {
        prefsRepo.set(Pref.holdTempToolTip, false)
        setState {
            copy(
                holdTempToolTip = false,
            )
        }
        setEffect {
            DashContract.Effect.HideHoldTempToolTip
        }
    }

    private fun toggleLidOpen() {
        viewModelScope.launch {
            DialogController.sendEvent(
                DialogEvent(
                    title = UiText(
                        R.string.dialog_cancel_lid_detect_title,
                    ),
                    message = UiText(
                        R.string.dialog_cancel_lid_detect_message,
                    ),
                    positiveAction = DialogAction(
                        buttonText = UiText(R.string.yes),
                        action = {
                            setLoadingState(true)
                            handleResult(dashRepo.sendToggleLidOpen(false))
                        },
                    ),
                    negativeAction =
                        DialogAction(
                            buttonText = UiText(R.string.no),
                            action = { },
                        ),
                ),
            )
        }
    }

    private fun checkHopperLevel() {
        if (viewState.value.dash.hasDistanceSensor) {
            setLoadingState(true)
            viewModelScope.launch(Dispatchers.IO) {
                handleResult(dashRepo.checkHopperLevel())
            }
        }
    }

    private fun startUpRequested(holdDialogSupported: Boolean) {
        when {
            holdDialogSupported -> {
                if (viewState.value.dash.startToHoldPrompt &&
                    viewState.value.dash.startupGotoMode.equals(
                        other = RunningMode.Hold.name,
                        ignoreCase = true,
                    )
                ) {
                    setEffect {
                        DashContract.Effect.ShowStartupHoldDialog
                    }
                } else {
                    sendDashEvent(DashEvent.Start)
                }
            }

            else -> sendDashEvent(DashEvent.Start)
        }
    }

    private fun restartControlDialog() {
        viewModelScope.launch {
            DialogController.sendEvent(
                DialogEvent(
                    title = UiText(
                        R.string.dialog_restart_control_title,
                    ),
                    message = UiText(
                        R.string.dialog_restart_control_message,
                    ),
                    positiveAction = DialogAction(
                        buttonText = UiText(R.string.yes),
                        action = {
                            setLoadingState(true)
                            restartControl()
                        },
                    ),
                    negativeAction =
                        DialogAction(
                            buttonText = UiText(R.string.no),
                            action = { },
                        ),
                ),
            )
        }
    }

    private fun setStartupHoldTemp(temp: String) {
        val holdTemp = temp.toIntOrNull() ?: return
        setLoadingState(true)
        viewModelScope.launch {
            // Only start once the hold temp is saved, or the grill would start at the old one
            when (val result = settingsRepo.setStartToModeTemp(holdTemp)) {
                is Result.Error -> handleResult(Result.Error(result.error))
                is Result.Success -> sendDashEvent(DashEvent.Start)
            }
        }
    }

    private fun restartControl() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = settingsRepo.restartControl()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        setLoadingState(false)
                        setEffect {
                            DashContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true,
                            )
                        }
                    }

                    is Result.Success -> {
                        setLoadingState(false)
                    }
                }
            }
        }
    }

    private fun toggleTimer(timer: Timer) {
        val currentEpochTimeSeconds = Instant.now().epochSecond
        val endTimeEpochSeconds = timer.end
        if (endTimeEpochSeconds > 0 && currentEpochTimeSeconds < endTimeEpochSeconds) {
            val totalSeconds = endTimeEpochSeconds - currentEpochTimeSeconds
            val totalDuration = endTimeEpochSeconds - timer.start.toLong()
            val secondsRemaining = endTimeEpochSeconds - timer.paused.toLong()
            if (timer.paused > 0) {
                timerIntent.pauseTimer(
                    secondsRemaining = secondsRemaining,
                    totalSeconds = totalSeconds,
                    totalDuration = totalDuration,
                )
            } else {
                timerIntent.startTimer(
                    endEpochSeconds = endTimeEpochSeconds.toLong(),
                    totalDuration = totalDuration,
                )
            }
        } else {
            timerIntent.cancelTimer()
        }
    }

    private fun toggleElapsed(startTime: Double) {
        val timeElapsed = Instant.now().epochSecond - startTime
        if (startTime > 0 && timeElapsed > 0) {
            elapsedIntent.startElapsed(startTime)
        } else {
            elapsedIntent.cancelElapsed()
        }
    }

    private fun sendSmokePlus() {
        viewModelScope.launch {
            when (viewState.value.dash.currentMode) {
                RunningMode.Hold.name, RunningMode.Smoke.name ->
                    setSmokePlus(!viewState.value.dash.smokePlus)

                RunningMode.Stop.name -> {}

                else -> setEffect {
                    DashContract.Effect.Notification(
                        text = UiText(R.string.alerter_smoke_plus_disabled),
                        error = false,
                    )
                }
            }
        }
    }

    private fun sendPwmControl() {
        viewModelScope.launch {
            when (viewState.value.dash.currentMode) {
                RunningMode.Hold.name -> setPWMControl(!viewState.value.dash.pwmControl)

                RunningMode.Stop.name -> {}

                else -> setEffect {
                    DashContract.Effect.Notification(
                        text = UiText(R.string.alerter_pwm_control_disabled),
                        error = false,
                    )
                }
            }
        }
    }

    private fun setSmokePlus(enabled: Boolean) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(dashRepo.setSmokePlus(enabled))
        }
    }

    private fun setPWMControl(enabled: Boolean) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(dashRepo.setPWMControl(enabled))
        }
    }

    private fun setProbeNotify(probeNotify: ProbeNotify) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = dashRepo.setProbeNotify(probeNotify)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        if (result.error != DataError.Network.SOCKET_ERROR) {
                            setEffect {
                                DashContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true,
                                )
                            }
                        }
                        setState {
                            copy(
                                isRefreshing = false,
                                isLoading = false,
                            )
                        }
                    }

                    is Result.Success -> {
                        setLoadingState(false)
                    }
                }
            }
        }
    }

    private suspend fun handleResult(result: Result<Unit, DataError>) {
        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Error -> {
                    if (result.error != DataError.Network.SOCKET_ERROR) {
                        setEffect {
                            DashContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true,
                            )
                        }
                    }
                    setState { copy(isRefreshing = false, isLoading = false) }
                }

                is Result.Success -> {
                    // listenDashData clears loading
                }
            }
        }
    }

    private fun getDashData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val result = dashRepo.getDashData()) {
                is Result.Error -> {
                    if (forced) {
                        setEffect {
                            DashContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true,
                            )
                        }
                        setState { copy(isRefreshing = false) }
                    } else {
                        getCachedData()
                    }
                }

                is Result.Success -> {
                    setState {
                        copy(
                            dash = result.data,
                            isInitialLoading = false,
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                }
            }
        }
    }

    private fun getCachedData() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = dashRepo.getCachedData()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        setState {
                            copy(
                                dash = Dash(),
                                isInitialLoading = false,
                                isRefreshing = false,
                                isDataError = true,
                            )
                        }
                    }

                    is Result.Success -> {
                        setState {
                            copy(
                                dash = buildOfflineDash(result.data),
                                isInitialLoading = false,
                                isRefreshing = false,
                            )
                        }
                        setEffect {
                            DashContract.Effect.Notification(
                                text = UiText(R.string.alerter_cached_results),
                                error = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        setState {
            copy(
                isLoading = loading,
            )
        }
    }

    private fun buildOfflineDash(data: Dash?): Dash {
        val dash = data ?: viewState.value.dash
        return dash.copy(
            currentMode = RunningMode.Stop.name,
            hopperLevel = 0,
            primaryProbe = Probe(
                probeType = dash.primaryProbe.probeType,
                title = dash.primaryProbe.title,
                label = dash.primaryProbe.label,
                maxTemp = dash.primaryProbe.maxTemp,
                status = dash.primaryProbe.status,
            ),
            foodProbes = dash.foodProbes.map {
                Probe(
                    probeType = it.probeType,
                    title = it.title,
                    label = it.label,
                    maxTemp = it.maxTemp,
                    status = it.status,
                )
            },
            smokePlus = false,
            pwmControl = false,
            outputs = Outputs(),
        )
    }
}
