package com.weberbox.pifire.feature.settings.presentation.contract

import com.weberbox.pifire.core.model.push.PushDevice
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText

class AppPushContract {

    sealed class Event : ViewEvent {
        data object Refresh : Event()
        data class DeleteDevice(val id: String) : Event()

        /** Turning on is sent once notification permission is granted. */
        data class SetThisPhoneOn(val isOn: Boolean) : Event()
    }

    data class State(
        val devices: List<PushDevice>,
        val isThisPhoneOn: Boolean,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
        val isDataError: Boolean,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean = false) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data class NavRoute(
                val route: Any,
                val popUpToRoute: Any? = null,
                val inclusive: Boolean = true,
            ) : Navigation()
        }
    }
}
