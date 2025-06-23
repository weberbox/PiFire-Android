package com.weberbox.pifire.setup.presentation.contract

import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.DialogEvent

class PushContract {

    sealed class Event : ViewEvent {
        data class ToggleConsent(val enabled: Boolean) : Event()
        data object NavigateToFinish : Event()
    }

    data class State(
        val consent: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()
        data class Dialog(val dialogEvent: DialogEvent) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object Forward : Navigation()
        }
    }
}