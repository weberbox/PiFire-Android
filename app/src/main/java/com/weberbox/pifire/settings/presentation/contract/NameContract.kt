package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText

class NameContract {

    sealed class Event : ViewEvent {
        data class SetGrillName(val name: String) : Event()
    }

    data class State(
        val grillName: String,
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