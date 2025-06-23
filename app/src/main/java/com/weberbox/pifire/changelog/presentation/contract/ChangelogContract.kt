package com.weberbox.pifire.changelog.presentation.contract

import com.weberbox.pifire.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText

class ChangelogContract {

    sealed class Event : ViewEvent

    data class State(
        val changelogData: ChangelogData,
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