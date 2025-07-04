package com.weberbox.pifire.setup.presentation.contract

import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.util.DialogEvent

class ConnectContract {

    sealed class Event : ViewEvent {
        data class ValidateAddress(val address: String) : Event()
        data object GetServerVersions : Event()
    }

    data class State(
        val serverAddress: InputState,
        val isLoading: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()
        data class Dialog(val dialogEvent: DialogEvent) : Effect()

        sealed class Navigation : Effect() {
            data object Forward : Navigation()
            data object Back : Navigation()
        }
    }
}