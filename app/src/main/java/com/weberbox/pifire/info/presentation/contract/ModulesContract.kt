package com.weberbox.pifire.info.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.info.presentation.model.InfoData.Info.Module

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