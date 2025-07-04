package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.presentation.model.PwmControl
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server

class PwmContract {

    sealed class Event : ViewEvent {
        data class SetPWMControlDefault(val enabled: Boolean) : Event()
        data class SetPWMTempUpdateTime(val time: Int) : Event()
        data class SetPWMFanFrequency(val frequency: Int) : Event()
        data class SetPWMMinDutyCycle(val dutyCycle: Int) : Event()
        data class SetPWMMaxDutyCycle(val dutyCycle: Int) : Event()
        data class DeletePWMControlItem(val controlItems: List<PwmControl>) : Event()
        data class SetPWMControlItem(
            val index: Int,
            val temp: Int,
            val dutyCycle: Int,
            val controlItems: List<PwmControl>
        ) : Event()
    }

    data class State(
        val serverData: Server,
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