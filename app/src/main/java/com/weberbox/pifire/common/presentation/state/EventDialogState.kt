package com.weberbox.pifire.common.presentation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.composeunstyled.DialogState
import com.composeunstyled.rememberDialogState
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EventDialogState(
    val state: DialogState,
    private val scope: CoroutineScope,
    dialogEvent: DialogEvent?,
) {
    var event: DialogEvent? by mutableStateOf(dialogEvent)
        private set

    fun show(dialogEvent: DialogEvent) {
        event = dialogEvent
        DialogController.current = dialogEvent
        state.visible = true
    }

    fun dismiss() {
        state.visible = false
        DialogController.current = null
    }

    /** Runs in the host's scope, which outlives the dismissed dialog's content. */
    fun runAction(action: DialogAction) {
        dismiss()
        scope.launch { action.action.invoke() }
    }
}

@Composable
fun rememberEventDialogState(): EventDialogState {
    // After process death there is no event to restore, so nothing is shown
    val restored = DialogController.current
    val state = rememberDialogState(initiallyVisible = restored != null)
    val scope = rememberCoroutineScope()
    return remember(state, scope) {
        EventDialogState(
            state = state,
            scope = scope,
            dialogEvent = restored,
        )
    }
}
