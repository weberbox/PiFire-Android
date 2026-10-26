package com.weberbox.pifire.core.ui.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DialogEvent(
    val title: UiText,
    val message: UiText,
    val dismissible: Boolean = true,
    val positiveAction: DialogAction,
    val negativeAction: DialogAction? = null,
    /** Identifies the dialog so it can be withdrawn (see [DialogController.withdraw]). */
    val tag: String? = null,
)

data class DialogAction(
    val buttonText: UiText,
    val action: suspend () -> Unit = { },
)

/**
 * The app-wide dialog (shown by MainActivity). The dialog is held here, not in saved UI state,
 * so it survives configuration changes (rotation) with its button actions intact; they can't be
 * saved to a bundle. After process death it's simply gone.
 */
object DialogController {
    private val _current = MutableStateFlow<DialogEvent?>(null)

    /** The dialog to show, or null for none. A new one replaces the one showing. */
    val current: StateFlow<DialogEvent?> = _current.asStateFlow()

    suspend fun sendEvent(event: DialogEvent) {
        _current.value = event
    }

    /** Closes [event] if it's still the one showing (not one that replaced it meanwhile). */
    fun dismiss(event: DialogEvent) {
        _current.compareAndSet(event, null)
    }

    /** Closes the dialog with [tag], e.g. when its question was answered somewhere else. */
    fun withdraw(tag: String) {
        _current.update { if (it?.tag == tag) null else it }
    }
}
