package com.weberbox.pifire.feature.info.presentation.contract

import com.weberbox.pifire.core.model.info.InfoData.Info.Module
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText

class ModulesContract {

    sealed class Event : ViewEvent

    data class State(
        val pipModules: List<Module>,
        val isInitialLoading: Boolean,
        val isRefreshing: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
        }
    }
}