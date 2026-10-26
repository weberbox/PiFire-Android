package com.weberbox.pifire.core.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.composeunstyled.DialogState
import com.composeunstyled.rememberDialogState
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.DialogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * What the app-wide dialog shows. [event] keeps the last dialog after it closes, so its content
 * stays on screen while it animates out.
 */
class EventDialogState(
    val state: DialogState,
    private val scope: CoroutineScope,
    initialEvent: DialogEvent?,
) {
    var event: DialogEvent? by mutableStateOf(initialEvent)
        private set

    internal fun update(current: DialogEvent?) {
        if (current != null) event = current
        state.visible = current != null
    }

    fun dismiss() {
        state.visible = false
        event?.let(DialogController::dismiss)
    }

    /** Runs in the host's scope, which outlives the dismissed dialog's content. */
    fun runAction(action: DialogAction) {
        dismiss()
        scope.launch { action.action.invoke() }
    }
}

/** Follows [DialogController.current], so the dialog comes back (actions included) after rotation. */
@Composable
fun rememberEventDialogState(): EventDialogState {
    // Read once for the initial visibility; the effect below follows later changes.
    val restored = remember { DialogController.current.value }
    val state = rememberDialogState(initiallyVisible = restored != null)
    val scope = rememberCoroutineScope()
    val eventDialogState = remember(state, scope) {
        EventDialogState(
            state = state,
            scope = scope,
            initialEvent = restored,
        )
    }
    LaunchedEffect(eventDialogState) {
        DialogController.current.collect(eventDialogState::update)
    }
    return eventDialogState
}
