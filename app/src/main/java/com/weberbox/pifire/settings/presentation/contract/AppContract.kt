package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.common.presentation.util.UiText

class AppContract {

    sealed class Event : ViewEvent {
        data class UpdateAppTheme(val theme: AppTheme) : Event()
        data class UpdateUserEmail(val email: String) : Event()
        data class BiometricsEnabled(val enabled: Boolean) : Event()
    }

    data class State(
        val appTheme: AppTheme,
        val userEmail: String,
        val biometricsEnabled: Boolean,
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