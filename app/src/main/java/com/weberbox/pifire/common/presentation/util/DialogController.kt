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

    /**
     * The dialog on screen, kept here so it survives activity recreation with working actions
     * (lambdas can't be saved in a Bundle). Main thread only; cleared on dismiss.
     */
    internal var current: DialogEvent? = null

    suspend fun sendEvent(event: DialogEvent) {
        _events.send(event)
    }
}