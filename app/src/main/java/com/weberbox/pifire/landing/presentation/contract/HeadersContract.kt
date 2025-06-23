package com.weberbox.pifire.landing.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.ExtraHeader

class HeadersContract {

    sealed class Event : ViewEvent {
        data class UpdateHeader(val header: ExtraHeader) : Event()
        data class DeleteHeader(val header: ExtraHeader) : Event()
    }

    data class State(
        val extraHeaders: List<ExtraHeader>,
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