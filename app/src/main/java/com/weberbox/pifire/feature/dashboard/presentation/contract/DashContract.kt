package com.weberbox.pifire.feature.dashboard.presentation.contract

import com.weberbox.pifire.core.model.dashboard.DashData.Dash
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.dashboard.data.model.ProbeNotify as ProbeNotifyData

class DashContract {

    sealed class Event : ViewEvent {
        data object Refresh: Event()
        data object PWMControl: Event()
        data object SmokePlus: Event()
        data object HideHoldTempTip: Event()
        data object ToggleLidDetect : Event()
        data object RestartControl : Event()
        data object CheckHopperLevel : Event()
        data class ProbeNotify(val probeNotify: ProbeNotifyData) : Event()
        data class SetStartupHoldTemp(val temp: String) : Event()
        data class StartUpRequested(val holdDialogSupported: Boolean) : Event()
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
        data object ShowStartupHoldDialog : Effect()
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data class NavRoute(
                val route: Any,
                val popUpToRoute: Any? = null,
                val inclusive: Boolean = true,
            ) : Navigation()
        }
    }
}