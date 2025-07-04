package com.weberbox.pifire.home.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText

class HomeContract {

    sealed class Event : ViewEvent {
        data object SignOut: Event()
        data object TriggerLidOpen : Event()
    }

    data class State(
        val showBottomBar: Boolean,
        val isConnected: Boolean,
        val isHoldMode: Boolean,
        val lidOpenDetectEnabled: Boolean,
        val grillName: String,
        val isInitialLoading: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object Changelog : Navigation()
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }
    }
}