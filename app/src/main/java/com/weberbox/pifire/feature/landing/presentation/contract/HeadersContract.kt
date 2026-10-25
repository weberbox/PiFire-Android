package com.weberbox.pifire.feature.landing.presentation.contract

import com.weberbox.pifire.core.network.headers.HeadersData.Headers.ExtraHeader
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText

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
            data class NavRoute(
                val route: Any,
                val popUpToRoute: Any? = null,
                val inclusive: Boolean = true,
            ) : Navigation()
        }
    }
}