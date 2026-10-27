package com.weberbox.pifire.feature.setup.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.UiText

class PushContract {

    sealed class Event : ViewEvent {
        data class ToggleConsent(val enabled: Boolean) : Event()

        /** Notification permission is granted: turn on app push for this server. */
        data object AllowAppPush : Event()
        data object NavigateToFinish : Event()
    }

    data class State(
        val consent: Boolean,
        /** API v2 server: app push through the relay instead of OneSignal. */
        val isAppPush: Boolean,
        val isAppPushAllowed: Boolean,
        val isLoading: Boolean,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()
        data class Dialog(val dialogEvent: DialogEvent) : Effect()
        data object RequestPermission : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object Forward : Navigation()
        }
    }
}
