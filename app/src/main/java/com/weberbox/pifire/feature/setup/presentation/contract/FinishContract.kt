package com.weberbox.pifire.feature.setup.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.model.InputState
import com.weberbox.pifire.core.ui.util.UiText

class FinishContract {

    sealed class Event : ViewEvent {
        data class ValidateName(val name: String) : Event()
        data class SaveGrillName(val name: String) : Event()
    }

    data class State(
        val grillName: InputState,
        val isLoading: Boolean,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object Forward : Navigation()
        }
    }
}
