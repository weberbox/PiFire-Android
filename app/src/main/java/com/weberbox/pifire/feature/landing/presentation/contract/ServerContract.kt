package com.weberbox.pifire.feature.landing.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.model.InputState
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.settings.data.model.local.HeadersData.Headers.BasicAuth
import com.weberbox.pifire.feature.settings.presentation.model.SettingsData.Server

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
            data class NavRoute(
                val route: Any,
                val popUpToRoute: Any? = null,
                val inclusive: Boolean = true,
            ) : Navigation()
        }
    }
}