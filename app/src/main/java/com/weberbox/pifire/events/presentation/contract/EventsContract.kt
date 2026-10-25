package com.weberbox.pifire.events.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.events.presentation.model.EventsData.Events.Event

internal typealias GrillEvent = Event

class EventsContract {

    sealed class Event : ViewEvent {
        data object Refresh: Event()
    }

    data class State(
        val eventsList: List<GrillEvent>,
        val isInitialLoading: Boolean,
        val isDataError: Boolean,
        val isRefreshing: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()
    }
}