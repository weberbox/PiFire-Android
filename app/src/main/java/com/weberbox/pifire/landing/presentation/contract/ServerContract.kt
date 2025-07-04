package com.weberbox.pifire.landing.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.BasicAuth
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server

class ServerContract {

    sealed class Event : ViewEvent {
        data object Back: Event()
        data class EnableBasicAuth(val enabled: Boolean) : Event()
        data class UpdateBasicAuth(val basicAuth: BasicAuth) : Event()
        data class EnableHeaders(val enabled: Boolean) : Event()
        data class UpdateAddress(val address: String) : Event()
        data class ValidateAddress(val address: String) : Event()
    }

    data class State(
        val serverData: Server,
        val basicAuth: BasicAuth,
        val serverAddress: InputState,
        val isInitialLoading: Boolean,
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