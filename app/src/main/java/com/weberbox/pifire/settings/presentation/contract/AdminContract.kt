package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText

class AdminContract {

    sealed class Event : ViewEvent {
        data object DeleteEvents : Event()
        data object DeleteHistory : Event()
        data object DeletePellets : Event()
        data object DeletePelletsLog : Event()
        data object FactoryReset : Event()
        data object RebootSystem : Event()
        data object RestartControl : Event()
        data object RestartWebApp : Event()
        data object RestartSupervisor : Event()
        data object ShutdownSystem : Event()
        data class SetBootToMonitor(val enabled: Boolean) : Event()
        data class SetDebugMode(val enabled: Boolean) : Event()
    }

    data class State(
        val adminDebug: Boolean,
        val bootToMonitor: Boolean,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
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