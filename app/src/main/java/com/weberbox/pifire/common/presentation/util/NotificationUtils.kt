package com.weberbox.pifire.common.presentation.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tapadoo.alerter.Alerter
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.Alerter
import com.weberbox.pifire.common.presentation.component.iconPulse
import com.weberbox.pifire.common.presentation.theme.alerterError
import com.weberbox.pifire.common.presentation.theme.alerterInfo
import com.weberbox.pifire.common.presentation.theme.spacing

fun Activity.showToast(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_LONG
) {
    Toast.makeText(this, this.getString(message), duration).show()
}

fun Context.showToast(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_LONG
) {
    Toast.makeText(this, this.getString(message), duration).show()
}

fun Activity.showAlerter(
    @StringRes title: Int? = null,
    @StringRes message: Int,
    @DrawableRes icon: Int = R.drawable.ic_info,
    backgroundColor: Color = alerterInfo,
    duration: Long = 3000,
    enableVibration: Boolean = true,
    enableSwipeToDismiss: Boolean = false,
    disableOutsideTouch: Boolean = false,
    enableInfiniteDuration: Boolean = false,
    gravity: Int = Gravity.TOP,
    isError: Boolean = false
) {
    this.showAlerter(
        title = title?.let { UiText(it) },
        message = UiText(message),
        icon = icon,
        backgroundColor = backgroundColor,
        duration = duration,
        enableVibration = enableVibration,
        enableSwipeToDismiss = enableSwipeToDismiss,
        disableOutsideTouch = disableOutsideTouch,
        enableInfiniteDuration = enableInfiniteDuration,
        gravity = gravity,
        isError = isError
    )
}

fun Activity.showAlerter(
    title: UiText? = null,
    message: UiText,
    @DrawableRes icon: Int = R.drawable.ic_info,
    backgroundColor: Color = alerterInfo,
    duration: Long = 3000,
    enableVibration: Boolean = true,
    enableSwipeToDismiss: Boolean = false,
    disableOutsideTouch: Boolean = false,
    enableInfiniteDuration: Boolean = false,
    gravity: Int = Gravity.TOP,
    isError: Boolean = false
) {
    Alerter.create(this)
        .setText(message.asString(this))
        .setIcon(icon)
        .setBackgroundColorInt(
            if (isError)
                alerterError.toArgb() else backgroundColor.toArgb()
        )
        .enableClickAnimation(false)
        .enableSwipeToDismiss()
        .enableVibration(enableVibration)
        .setTextAppearance(R.style.Text14QuestrialBold)
        .enableInfiniteDuration(enableInfiniteDuration)
        .setIconSize(R.dimen.alerter_icon_size_small)
        .setLayoutGravity(gravity)
        .setDuration(duration)
        .also { alerter ->
            if (enableSwipeToDismiss) alerter.enableSwipeToDismiss()
            if (disableOutsideTouch) alerter.disableOutsideTouch()
            if (title != null) alerter.setTitle(title.asString(this))
        }
        .show()
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

@Suppress("unused")
@Composable
fun ErrorAlerter(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    showAlert: Boolean, onChanged: (showAlert: Boolean) -> Unit,
) {
    Alerter(
        isShown = showAlert,
        onChanged = onChanged,
        backgroundColor = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.smallTwo)
                    .iconPulse()
            )

            Column(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.smallTwo)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Suppress("unused")
@Composable
fun InfoAlerter(
    modifier: Modifier = Modifier,
    showAlert: Boolean = false,
    title: String,
    message: String,
) {
    var show by remember(showAlert) { mutableStateOf(showAlert) }
    Alerter(
        isShown = show,
        onChanged = {
            show = it
        },
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.smallTwo)
                    .iconPulse()
            )

            Column(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.smallTwo)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            }
        }
    }
}