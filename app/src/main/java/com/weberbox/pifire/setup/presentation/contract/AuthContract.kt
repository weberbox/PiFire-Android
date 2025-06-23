package com.weberbox.pifire.setup.presentation.contract

import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.model.Credentials
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.ExtraHeader

class AuthContract {

    sealed class Event : ViewEvent {
        data class UpdateUsername(val username: String) : Event()
        data class UpdatePassword(val password: String) : Event()
        data class UpdateHeader(val header: ExtraHeader) : Event()
        data class DeleteHeader(val header: ExtraHeader) : Event()
        data object NavigateToConnect : Event()
    }

    data class State(
        val credentials: Credentials,
        val extraHeaders: List<ExtraHeader>
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object Forward : Navigation()
        }
    }
}