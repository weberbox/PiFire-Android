package com.weberbox.pifire.common.presentation.util

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarEvent(
    val message: UiText,
    val duration: SnackbarDuration = SnackbarDuration.Long,
    val action: SnackbarAction? = null,
    val showProgress: Boolean = false
)

data class SnackbarAction(
    val name: UiText,
    val action: suspend () -> Unit
)

object SnackbarController {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    private val _updateProgress = MutableStateFlow(0f)
    val updateProgress = _updateProgress.asStateFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }

    fun setUpdateProgress(progress: Float) {
        _updateProgress.value = progress
    }
}