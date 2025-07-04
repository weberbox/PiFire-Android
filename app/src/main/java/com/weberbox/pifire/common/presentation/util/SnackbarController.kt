package com.weberbox.pifire.common.presentation.util

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarEvent(
    val message: UiText,
    val duration: SnackbarDuration = SnackbarDuration.Long,
    val action: SnackbarAction? = null
)

data class SnackbarAction(
    val name: UiText,
    val action: suspend () -> Unit
)

object SnackbarController {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}