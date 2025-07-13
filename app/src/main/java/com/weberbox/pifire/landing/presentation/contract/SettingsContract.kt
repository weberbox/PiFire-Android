package com.weberbox.pifire.landing.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText

class SettingsContract {

    sealed class Event : ViewEvent {
        data object Back : Event()
        data class AutoSelectEnabled(val enabled: Boolean) : Event()
        data class BiometricsEnabled(val enabled: Boolean) : Event()
    }

    data class State(
        val autSelectEnabled: Boolean,
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