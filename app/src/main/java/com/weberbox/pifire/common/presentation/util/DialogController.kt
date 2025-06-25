package com.weberbox.pifire.common.presentation.util

import android.os.Parcelable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogEvent(
    val title: UiText,
    val message: UiText,
    val dismissible: Boolean = true,
    val positiveAction: DialogAction,
    val negativeAction: DialogAction? = null
) : Parcelable

@Parcelize
data class DialogAction(
    val buttonText: UiText,
    @IgnoredOnParcel
    val action: suspend () -> Unit = { },
) : Parcelable

object DialogController {

    private val _events = Channel<DialogEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: DialogEvent) {
        _events.send(event)
    }
}