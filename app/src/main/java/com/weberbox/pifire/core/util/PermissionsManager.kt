/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weberbox.pifire.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.isNotNullOrBlank
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// PermissionManager derived from Accompanist Permissions, modified to support permissionDetailsProvider
@Composable
fun rememberPermissionState(
    permissionDetailsProvider: PermissionDetailsProvider,
    onPermissionResult: (Boolean) -> Unit = {},
    previewPermissionStatus: PermissionStatus = PermissionStatus.Granted
): PermissionState {
    return when {
        LocalInspectionMode.current -> PreviewPermissionState(
            permissionDetailsProvider = permissionDetailsProvider,
            status = previewPermissionStatus
        )

        else -> rememberPermissionState(permissionDetailsProvider, onPermissionResult)
    }
}

@Composable
private fun rememberPermissionState(
    permissionDetailsProvider: PermissionDetailsProvider,
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState {
    val context = LocalContext.current
    val activity = context.findActivity()
    val scope = rememberCoroutineScope()
    val permissionState = remember(permissionDetailsProvider) {
        MutablePermissionState(permissionDetailsProvider, context, activity)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                permissionState.refreshPermissionStatus()
                scope.showPermissionDialog(
                    permissionDetailsProvider = permissionDetailsProvider,
                    shouldShowRationale = permissionState.status.shouldShowRationale,
                    onDismiss = {
                        onPermissionResult(false)
                    },
                    onOkClick = {
                        permissionState.launchPermissionRequest()
                    },
                    onGoToAppSettingsClick = {
                        activity.openAppSettings(
                            permissionDetailsProvider.getSettingsLocation()
                        )
                    }
                )
            } else {
                onPermissionResult(true)
            }
        }
    )

    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}

@Immutable
internal class PreviewPermissionState(
    override val permissionDetailsProvider: PermissionDetailsProvider,
    override val status: PermissionStatus
) : PermissionState {
    override fun launchPermissionRequest() {}
}

internal class MutablePermissionState(
    override val permissionDetailsProvider: PermissionDetailsProvider,
    private val context: Context,
    private val activity: Activity
) : PermissionState {
    private val permission = permissionDetailsProvider.getPermission()
    override var status: PermissionStatus by mutableStateOf(getPermissionStatus())

    override fun launchPermissionRequest() {
        if (Build.VERSION.SDK_INT >= permissionDetailsProvider.getRequiredApi()) {
            launcher?.launch(
                permission
            ) ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
        }
    }

    internal var launcher: ActivityResultLauncher<String>? = null

    internal fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    private fun getPermissionStatus(): PermissionStatus {
        val hasPermission = context.checkPermission(permission)
        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(activity.shouldShowRationale(permission))
        }
    }
}

private fun CoroutineScope.showPermissionDialog(
    permissionDetailsProvider: PermissionDetailsProvider,
    shouldShowRationale: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    launch {
        DialogController.sendEvent(
            event = DialogEvent(
                title = UiText(R.string.dialog_permission_required_title),
                message = permissionDetailsProvider.getDescription(shouldShowRationale),
                dismissible = false,
                positiveAction = DialogAction(
                    buttonText = if (shouldShowRationale) UiText(R.string.grant) else
                        UiText(R.string.settings),
                    action = {
                        if (shouldShowRationale) onOkClick() else onGoToAppSettingsClick()
                    }
                ),
                negativeAction = DialogAction(
                    buttonText = UiText(R.string.cancel),
                    action = {
                        onDismiss()
                    }
                )
            )
        )
    }
}

class NotificationsPermissionDetailsProvider : PermissionDetailsProvider {
    override fun getDescription(shouldShowRationale: Boolean): UiText {
        return UiText(
            if (shouldShowRationale) {
                R.string.dialog_notification_permission_rational
            } else {
                R.string.dialog_notification_permission_declined
            }
        )
    }

    @SuppressLint("InlinedApi")
    override fun getPermission(): String {
        return Manifest.permission.POST_NOTIFICATIONS
    }

    override fun getRequiredApi(): Int {
        return Build.VERSION_CODES.TIRAMISU
    }

    @SuppressLint("InlinedApi")
    override fun getSettingsLocation(): String {
        return Settings.ACTION_APP_NOTIFICATION_SETTINGS
    }
}

@Stable
interface PermissionDetailsProvider {
    fun getDescription(shouldShowRationale: Boolean): UiText
    fun getPermission(): String
    fun getRequiredApi(): Int
    fun getSettingsLocation(): String? = null
}

@Stable
interface PermissionState {
    val permissionDetailsProvider: PermissionDetailsProvider
    val status: PermissionStatus
    fun launchPermissionRequest()
}

@Stable
sealed interface PermissionStatus {
    object Granted : PermissionStatus
    data class Denied(
        val shouldShowRationale: Boolean
    ) : PermissionStatus
}

private val PermissionStatus.shouldShowRationale: Boolean
    get() = when (this) {
        is PermissionStatus.Granted -> false
        is PermissionStatus.Denied -> shouldShowRationale
    }

private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

private fun Activity.shouldShowRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

private fun Activity.openAppSettings(location: String?) {
    Intent().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && location.isNotNullOrBlank()) {
            action = location
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.fromParts("package", packageName, null)
        }
    }.also(::startActivity)
}

fun Context.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}