package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.presentation.model.ManualData

class ManualContract {

    sealed class Event : ViewEvent {
        data class SetManualMode(val enabled: Boolean) : Event()
        data class SetFanEnabled(val enabled: Boolean) : Event()
        data class SetDutyCycle(val dutyCycle: Int) : Event()
        data class SetAugerEnabled(val enabled: Boolean) : Event()
        data class SetIgniterEnabled(val enabled: Boolean) : Event()
        data class SetPowerEnabled(val enabled: Boolean) : Event()

    }

    data class State(
        val manualData: ManualData,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }
    }
}