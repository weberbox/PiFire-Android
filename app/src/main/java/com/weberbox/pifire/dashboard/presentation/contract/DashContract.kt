package com.weberbox.pifire.dashboard.presentation.contract

import com.weberbox.pifire.common.data.model.PostDto.NotifyDto
import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import com.weberbox.pifire.dashboard.presentation.model.ElapsedData
import com.weberbox.pifire.dashboard.presentation.model.TimerData

class DashContract {

    sealed class Event : ViewEvent {
        data object Refresh: Event()
        data object PWMControl: Event()
        data object SmokePlus: Event()
        data object HideHoldTempTip: Event()
        data object ToggleLidDetect : Event()
        data object RestartControl : Event()
        data class ProbeNotify(val notifyDto: NotifyDto) : Event()
        data class SendEvent(val event: DashEvent): Event()
    }

    sealed class DashEvent {
        data object HoldDialog : DashEvent()
        data object PrimeDialog : DashEvent()
        data object Smoke : DashEvent()
        data object Stop : DashEvent()
        data object Start : DashEvent()
        data object Monitor : DashEvent()
        data object Shutdown : DashEvent()
        data object RecipeUnPause : DashEvent()
        data object HopperCheck : DashEvent()
        data class ToggleFan(val enabled: Boolean) : DashEvent()
        data class ToggleAuger(val enabled: Boolean) : DashEvent()
        data class ToggleIgniter(val enabled: Boolean) : DashEvent()
        data class TimerAction(val action: String) : DashEvent()
        data class PrimeGrill(val primeAmount: Int, val nextMode: String) : DashEvent()
        data class GrillHoldTemp(val temp: String) : DashEvent()
        data class TimerTime(
            val hours: Int,
            val minutes: Int,
            val shutdown: Boolean,
            val keepWarm: Boolean
        ) : DashEvent()
    }

    data class State(
        val dash: Dash,
        val timerData: TimerData,
        val elapsedData: ElapsedData,
        val incrementTemps: Boolean,
        val keepScreenOn: Boolean,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
        val isDataError: Boolean,
        val isRefreshing: Boolean,
        val isConnected: Boolean,
        val holdTempToolTip: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object HideHoldTempToolTip : Effect()
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }
    }
}