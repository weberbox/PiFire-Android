package com.weberbox.pifire.core.ui.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.weberbox.pifire.R

/** What to ask for, and what our dialog says when the user declines. */
@Immutable
data class PermissionSpec(
    /** All must be granted. */
    val permissions: List<String>,
    /** Shown while Android will still ask again. */
    @param:StringRes val rationale: Int,
    /** Shown once only the system settings can grant it. */
    @param:StringRes val declined: Int,
    val settingsAction: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
    /** Below this there's nothing to ask for, so [PermissionState.request] does nothing. */
    val minSdk: Int = Build.VERSION_CODES.BASE,
)

@SuppressLint("InlinedApi")
val NOTIFICATIONS_PERMISSION = PermissionSpec(
    permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
    rationale = R.string.dialog_notification_permission_rational,
    declined = R.string.dialog_notification_permission_declined,
    settingsAction = Settings.ACTION_APP_NOTIFICATION_SETTINGS,
    minSdk = Build.VERSION_CODES.TIRAMISU,
)

@Stable
class PermissionState internal constructor(
    private val granted: MutableState<Boolean>,
    private val checkGranted: () -> Boolean,
    private val launch: () -> Unit,
) {
    /** Also true below the spec's minSdk, where there's nothing to grant. */
    val isGranted: Boolean get() = granted.value

    /** Shows the system prompt; if declined, our dialog offers to ask again or the settings. */
    fun request() = launch()

    /** Re-reads the status, e.g. on returning from the system settings where it may have changed. */
    fun refresh() {
        granted.value = checkGranted()
    }
}

/** [onResult] runs with true once granted, and with false when the user cancels our dialog. */
@Composable
fun rememberPermissionState(
    spec: PermissionSpec,
    onResult: (granted: Boolean) -> Unit = {},
): PermissionState {
    if (LocalInspectionMode.current) {
        return remember { PermissionState(granted = mutableStateOf(true), checkGranted = { true }, launch = {}) }
    }

    val context = LocalContext.current
    val activity = LocalActivity.current
    val currentOnResult by rememberUpdatedState(onResult)
    val granted = remember(spec) { mutableStateOf(context.hasAll(spec)) }
    // Shown from an effect: asking again needs the launcher, which its own callback can't reach.
    var declined by remember { mutableStateOf<PermissionOutcome?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        granted.value = context.hasAll(spec)
        val outcome = outcomeAfterAsking(
            granted = granted.value,
            showRationale = activity.showsRationale(spec.permissions),
        )
        if (outcome == PermissionOutcome.Granted) currentOnResult(true) else declined = outcome
    }

    LaunchedEffect(declined) {
        val outcome = declined ?: return@LaunchedEffect
        DialogController.sendEvent(
            event = declinedDialog(
                spec = spec,
                canAskAgain = outcome == PermissionOutcome.AskAgain,
                onAskAgain = { requestIfSupported(spec = spec, launch = launcher::launch) },
                onOpenSettings = { activity?.openAppSettings(location = spec.settingsAction) },
                onCancel = { currentOnResult(false) },
            ),
        )
        declined = null
    }

    return remember(spec, launcher) {
        PermissionState(
            granted = granted,
            checkGranted = { context.hasAll(spec) },
            launch = { requestIfSupported(spec = spec, launch = launcher::launch) },
        )
    }
}

internal enum class PermissionOutcome { Granted, AskAgain, OpenSettings }

/** After asking: a denial without a rationale due means Android won't show the prompt again. */
internal fun outcomeAfterAsking(granted: Boolean, showRationale: Boolean): PermissionOutcome = when {
    granted -> PermissionOutcome.Granted
    showRationale -> PermissionOutcome.AskAgain
    else -> PermissionOutcome.OpenSettings
}

private fun requestIfSupported(spec: PermissionSpec, launch: (Array<String>) -> Unit) {
    if (Build.VERSION.SDK_INT >= spec.minSdk) launch(spec.permissions.toTypedArray())
}

private fun declinedDialog(
    spec: PermissionSpec,
    canAskAgain: Boolean,
    onAskAgain: () -> Unit,
    onOpenSettings: () -> Unit,
    onCancel: () -> Unit,
) = DialogEvent(
    title = UiText(resId = R.string.dialog_permission_required_title),
    message = UiText(resId = if (canAskAgain) spec.rationale else spec.declined),
    dismissible = false,
    positiveAction = DialogAction(
        buttonText = UiText(resId = if (canAskAgain) R.string.grant else R.string.settings),
        action = { if (canAskAgain) onAskAgain() else onOpenSettings() },
    ),
    negativeAction = DialogAction(
        buttonText = UiText(resId = R.string.cancel),
        action = { onCancel() },
    ),
)

// Below minSdk the permission doesn't exist and is implicitly granted.
private fun Context.hasAll(spec: PermissionSpec) =
    Build.VERSION.SDK_INT < spec.minSdk || spec.permissions.all { checkPermission(it) }

private fun Activity?.showsRationale(permissions: List<String>) =
    this != null && permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }

private fun Activity.openAppSettings(location: String) {
    Intent()
        .apply {
            // Channel and notification settings screens only exist from Android 8.
            if (location == Settings.ACTION_APPLICATION_DETAILS_SETTINGS || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                // The app details screen identifies the app by a package: URI, not an extra.
                data = Uri.fromParts("package", packageName, null)
            } else {
                action = location
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
        }.also(::startActivity)
}

fun Context.checkPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) ==
        PackageManager.PERMISSION_GRANTED
