package com.weberbox.pifire.core.ui.components.message

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.weberbox.pifire.core.ui.util.UiMessage

/** The message [MessageHost] shows. One at a time: a new message replaces the one showing. */
@Stable
class MessageHostState {
    var current: ShownMessage? by mutableStateOf(null)
        private set

    fun show(message: UiMessage) {
        if (current?.message == message) return
        current = ShownMessage(message = message)
    }

    /** Only dismisses [shown] if it's still the current message, not one that replaced it. */
    fun dismiss(shown: ShownMessage) {
        if (current === shown) current = null
    }
}

/** Not a data class: each [MessageHostState.show] is a new card, even with the same text. */
class ShownMessage(val message: UiMessage)
