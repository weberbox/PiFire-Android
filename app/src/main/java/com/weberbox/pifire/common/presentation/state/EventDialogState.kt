package com.weberbox.pifire.common.presentation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.composables.core.DialogState
import com.composables.core.rememberDialogState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogEvent

class EventDialogState(
    val state: DialogState,
    dialogEvent: DialogEvent
) {
    private var eventState = mutableStateOf(dialogEvent)

    val event: DialogEvent
        get() = eventState.value

    fun show(dialogEvent: DialogEvent) {
        eventState.value = dialogEvent
        state.visible = true
    }

    fun dismiss() {
        state.visible = false
    }

    companion object {
        fun Saver(dialogState: DialogState):
                Saver<EventDialogState, DialogEvent> {
            return Saver(
                save = { it.eventState.value },
                restore = { EventDialogState(dialogState, it) }
            )
        }
    }
}

@Composable
fun rememberEventDialogState(
    initiallyVisible: Boolean = false,
): EventDialogState {
    val state = rememberDialogState(
        initiallyVisible = initiallyVisible,
    )
    return rememberSaveable(saver = EventDialogState.Saver(state)) {
        EventDialogState(
            state, DialogEvent(
                title = UiText("Error"),
                message = UiText("Error"),
                positiveAction = DialogAction(
                    buttonText = UiText("Error"),
                    action = {}
                )
            )
        )
    }
}