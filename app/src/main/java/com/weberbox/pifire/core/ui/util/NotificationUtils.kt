package com.weberbox.pifire.core.ui.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.weberbox.pifire.core.ui.components.message.MessageHostOwner
import timber.log.Timber

fun Activity.showToast(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_LONG,
) {
    Toast.makeText(this, this.getString(message), duration).show()
}

fun Context.showToast(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_LONG,
) {
    Toast.makeText(this, this.getString(message), duration).show()
}

fun Activity.showToast(
    message: UiText,
    duration: Int = Toast.LENGTH_LONG,
) {
    Toast.makeText(this, message.asString(this), duration).show()
}

/** Shows [message] the way its style calls for (see [UiMessage]). */
fun Activity.showMessage(message: UiMessage) {
    when (message.style) {
        UiMessage.Style.Toast -> showToast(message = message.text, duration = Toast.LENGTH_SHORT)

        UiMessage.Style.Alert, UiMessage.Style.Error -> {
            val host = (this as? MessageHostOwner)?.messageHost
            if (host != null) {
                host.show(message = message)
            } else {
                Timber.w("No message host in ${javaClass.simpleName}, showing a toast instead")
                showToast(message = message.text)
            }
        }
    }
}

@Suppress("unused")
fun Activity.showAlertDialog(
    @StringRes title: Int? = null,
    @StringRes message: Int,
    @DrawableRes icon: Int? = null,
    @StringRes positiveButtonText: Int,
    @StringRes negativeButtonText: Int? = null,
    onPositiveClick: (DialogInterface) -> Unit,
    onNegativeClick: ((DialogInterface) -> Unit)? = null,
) {
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClick(dialog)
        }
        .also { dialog ->
            if (title != null) dialog.setTitle(title)
            if (icon != null) dialog.setIcon(icon)
            if (negativeButtonText != null && onNegativeClick != null) {
                dialog.setNegativeButton(negativeButtonText) { d, _ ->
                    onNegativeClick(d)
                }
            }
        }
        .show()
}
