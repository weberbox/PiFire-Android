package com.weberbox.pifire.feature.landing.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText

class SettingsContract {

    sealed class Event : ViewEvent {
        data object Back : Event()
        data object AppInfoDialog : Event()
        data class AutoSelectEnabled(val enabled: Boolean) : Event()
        data class BiometricsEnabled(val enabled: Boolean) : Event()
    }

    data class State(
        val autSelectEnabled: Boolean,
        val biometricsEnabled: Boolean,
        val isInitialLoading: Boolean,
        val isDataError: Boolean,
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
