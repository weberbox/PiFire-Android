package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server

class SafetyContract {

    sealed class Event : ViewEvent {
        data class SetSafetyStartupCheck(val enabled: Boolean) : Event()
        data class SetMinStartTemp(val temp: Int) : Event()
        data class SetMaxStartTemp(val temp: Int) : Event()
        data class SetMaxGrillTemp(val temp: Int) : Event()
        data class SetReigniteRetries(val retries: Int) : Event()
    }

    data class State(
        val serverData: Server,
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