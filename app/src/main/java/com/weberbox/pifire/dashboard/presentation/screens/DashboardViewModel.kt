package com.weberbox.pifire.dashboard.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto.NotifyDto
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.dashboard.data.api.DashApi
import com.weberbox.pifire.dashboard.data.repo.DashRepo
import com.weberbox.pifire.dashboard.presentation.contract.DashContract
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Outputs
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Timer
import com.weberbox.pifire.dashboard.presentation.model.ElapsedData
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.dashboard.presentation.model.TimerData
import com.weberbox.pifire.dashboard.presentation.util.CountDownTimer
import com.weberbox.pifire.dashboard.presentation.util.ElapsedTimer
import com.weberbox.pifire.settings.data.model.local.Pref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dashRepo: DashRepo,
    private val dashApi: DashApi,
    private val prefs: Prefs,
) : BaseViewModel<DashContract.Event, DashContract.State, DashContract.Effect>() {
    private var showHoldTempTip by mutableStateOf(false)
    private val timerIntent = CountDownTimer(viewModelScope)
    private val elapsedIntent = ElapsedTimer(viewModelScope)

    init {
        collectPrefsFlow()
        collectConnectedState()
        collectTimerState()
        collectElapsedState()
        listenDashData()
        getDashData(forced = false)
    }

    override fun setInitialState() = DashContract.State(
        dash = Dash(),
        timerData = TimerData(),
        elapsedData = ElapsedData(),
        incrementTemps = true,
        keepScreenOn = false,
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
        isRefreshing = false,
        isConnected = false,
        holdTempToolTip = false
    )

    override fun handleEvents(event: DashContract.Event) {
        when (event) {
            is DashContract.Event.Refresh -> getDashData(forced = true)
            is DashContract.Event.PWMControl -> sendPwmControl()
            is DashContract.Event.SmokePlus -> sendSmokePlus()
            is DashContract.Event.SendEvent -> sendDashEvent(event.event)
            is DashContract.Event.ProbeNotify -> setProbeNotify(event.notifyDto)
            is DashContract.Event.HideHoldTempTip -> hideHoldTempTip()
        }
    }

    private fun sendDashEvent(event: DashEvent) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                when (event) {
                    is DashEvent.Monitor -> dashApi.sendMonitorMode()
                    is DashEvent.RecipeUnPause -> dashApi.sendRecipeUnPause()
                    is DashEvent.Shutdown -> dashApi.sendShutdownMode()
                    is DashEvent.Smoke -> dashApi.sendSmokeMode()
                    is DashEvent.Start -> dashApi.sendStartGrill()
                    is DashEvent.Stop -> dashApi.sendStopMode()
                    is DashEvent.HopperCheck -> dashApi.checkHopperLevel()
                    is DashEvent.GrillHoldTemp -> dashApi.setGrillHoldTemp(event.temp)
                    is DashEvent.PrimeGrill -> dashApi.sendPrimeGrill(
                        event.primeAmount,
                        event.nextMode
                    )

                    is DashEvent.TimerAction -> dashApi.sendTimerAction(event.action)
                    is DashEvent.TimerTime -> dashApi.sendTimerTime(
                        event.hours,
                        event.minutes,
                        event.shutdown,
                        event.keepWarm
                    )

                    is DashEvent.ToggleFan -> dashApi.sendToggleFan(event.enabled)
                    is DashEvent.ToggleAuger -> dashApi.sendToggleAuger(event.enabled)
                    is DashEvent.ToggleIgniter -> dashApi.sendToggleIgniter(event.enabled)

                    else -> Result.Success(Unit)
                }
            )
        }
    }

    private fun collectPrefsFlow() {
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.incrementTemps).collect {
                setState {
                    copy(
                        incrementTemps = it
                    )
                }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.keepScreenOn).collect {
                setState {
                    copy(
                        keepScreenOn = it
                    )
                }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.holdTempToolTip).collect {
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
                            dash = buildOfflineDash(null)
                        )
                    }
                    toggleTimer(Timer())
                    toggleElapsed(0.0)
                }
            }
        }
    }

    private fun collectTimerState() {
        viewModelScope.launch {
            timerIntent.timerState.collect {
                setState {
                    copy(
                        timerData = it
                    )
                }
            }
        }
    }

    private fun collectElapsedState() {
        viewModelScope.launch {
            elapsedIntent.elapsedState.collect {
                setState {
                    copy(
                        elapsedData = it
                    )
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
                                        error = true
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
                                    isRefreshing = false
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
                    holdTempToolTip = true
                )
            }
        }
    }

    private fun hideHoldTempTip() {
        prefs.set(Pref.holdTempToolTip, false)
        setState {
            copy(
                holdTempToolTip = false
            )
        }
        setEffect {
            DashContract.Effect.HideHoldTempToolTip
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
                timerIntent.pauseTimer(secondsRemaining, totalSeconds, totalDuration)
            } else {
                timerIntent.startTimer(totalSeconds, totalDuration)
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
                        text = UiText(R.string.control_smoke_plus_disabled),
                        error = false
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
                        text = UiText(R.string.control_pwm_control_disabled),
                        error = false
                    )
                }
            }
        }
    }

    private fun setSmokePlus(enabled: Boolean) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(dashApi.setSmokePlus(enabled))
        }
    }

    private fun setPWMControl(enabled: Boolean) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(dashApi.setPWMControl(enabled))
        }
    }

    private fun setProbeNotify(notifyDto: NotifyDto) {
        setLoadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = dashApi.setProbeNotify(notifyDto)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        if (result.error != DataError.Network.SOCKET_ERROR) {
                            setEffect {
                                DashContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true
                                )
                            }
                        }
                        setState {
                            copy(
                                isRefreshing = false,
                                isLoading = false
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
                                error = true
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
                                error = true
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
                            isRefreshing = false
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
                                isDataError = true
                            )
                        }
                    }

                    is Result.Success -> {
                        setState {
                            copy(
                                dash = buildOfflineDash(result.data),
                                isInitialLoading = false,
                                isRefreshing = false
                            )
                        }
                        setEffect {
                            DashContract.Effect.Notification(
                                text = UiText(R.string.not_connected_cached_results),
                                error = true
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
                isLoading = loading
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
                status = dash.primaryProbe.status
            ),
            foodProbes = dash.foodProbes.map {
                Probe(
                    probeType = it.probeType,
                    title = it.title,
                    label = it.label,
                    maxTemp = it.maxTemp,
                    status = it.status
                )
            },
            smokePlus = false,
            pwmControl = false,
            outputs = Outputs()
        )
    }
}