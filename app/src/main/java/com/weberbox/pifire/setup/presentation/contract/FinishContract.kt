package com.weberbox.pifire.setup.presentation.contract

import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.model.InputState

class FinishContract {

    sealed class Event : ViewEvent {
        data class ValidateName(val name: String) : Event()
        data class SaveGrillName(val name: String) : Event()
    }

    data class State(
        val grillName: InputState,
        val isLoading: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object Forward : Navigation()
        }
    }
}