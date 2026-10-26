package com.weberbox.pifire.feature.settings.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.model.AppTheme
import com.weberbox.pifire.core.ui.util.UiText

class AppContract {

    sealed class Event : ViewEvent {
        data class UpdateAppTheme(val theme: AppTheme) : Event()
        data class DynamicColorEnabled(val enabled: Boolean) : Event()
        data class KeepScreenOn(val enabled: Boolean) : Event()
        data class ShowBottomBar(val enabled: Boolean) : Event()
        data class BiometricsEnabled(val enabled: Boolean) : Event()
        data class SetEventsAmount(val amount: Int) : Event()
        data class IncrementTemps(val enabled: Boolean) : Event()
        data class SentryEnabled(val enabled: Boolean) : Event()
        data class SentryDebugEnabled(val enabled: Boolean) : Event()
        data class UpdateUserEmail(val email: String) : Event()
        data object SendTestPush : Event()
    }

    data class State(
        val appTheme: AppTheme,
        val dynamicColorEnabled: Boolean,
        val keepScreenOn: Boolean,
        val showBottomBar: Boolean,
        val biometricsEnabled: Boolean,
        val eventsAmount: Int,
        val incrementTemps: Boolean,
        val sentryEnabled: Boolean,
        val sentryDebugEnabled: Boolean,
        val userEmail: String,
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
