package com.weberbox.pifire.core.util

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.weberbox.pifire.R
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.common.data.repo.RemoteConfigRepository
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.SnackbarAction
import com.weberbox.pifire.common.presentation.util.SnackbarController
import com.weberbox.pifire.common.presentation.util.SnackbarEvent
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.settings.data.model.local.Pref
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import javax.inject.Inject

class UpdateManager @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val prefs: Prefs
) {
    private var currentUpdate: AppUpdateConfigData? = null
    private var appUpdateManager: AppUpdateManager? = null

    suspend fun checkForUpdate(activity: ComponentActivity) {
        remoteConfigRepository.fetchAppUpdateConfig()?.also { config ->
            currentUpdate = config
            when (config.type) {
                UpdateType.IMMEDIATE -> {
                    activity.lifecycleScope.launch {
                        DialogController.sendEvent(
                            event = DialogEvent(
                                title = UiText(
                                    R.string.inapp_update_available_title
                                ),
                                message = if (config.message.isNotBlank())
                                    UiText(config.message) else UiText(
                                    R.string.inapp_update_available_message_immediate
                                ),
                                dismissible = false,
                                positiveAction = DialogAction(
                                    buttonText = UiText(R.string.update),
                                    action = {
                                        activity.lifecycleScope.launch {
                                            if (AppConfig.IS_PLAY_BUILD) {
                                                launchUpdate(activity)
                                            } else {
                                                openGithubLink(activity)
                                            }
                                        }
                                    }
                                )
                            )
                        )
                    }
                }

                UpdateType.FLEXIBLE -> {
                    if (prefs.get(Pref.updatePostponeTime) < System.currentTimeMillis()) {
                        activity.lifecycleScope.launch {
                            DialogController.sendEvent(
                                event = DialogEvent(
                                    title = UiText(
                                        R.string.inapp_update_available_title
                                    ),
                                    message = if (config.message.isNotBlank())
                                        UiText(config.message) else UiText(
                                        R.string.inapp_update_available_message_flexible
                                    ),
                                    dismissible = false,
                                    positiveAction = DialogAction(
                                        buttonText = UiText(R.string.update),
                                        action = {
                                            if (AppConfig.IS_PLAY_BUILD) {
                                                launchUpdate(activity)
                                            } else {
                                                openGithubLink(activity)
                                            }
                                        },
                                    ),
                                    negativeAction = DialogAction(
                                        buttonText = UiText(R.string.postpone),
                                        action = {
                                            postponeUpdate()
                                        }
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun resumeUpdateIfNeeded(activity: ComponentActivity) {
        val update = currentUpdate ?: return
        if (update.type == UpdateType.FLEXIBLE) {
            appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    activity.lifecycleScope.launch {
                        SnackbarController.sendEvent(
                            event = SnackbarEvent(
                                message = UiText(R.string.inapp_update_downloaded),
                                action = SnackbarAction(
                                    name = UiText(R.string.install),
                                    action = {
                                        appUpdateManager?.completeUpdate()
                                    }
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun launchUpdate(activity: ComponentActivity) {
        val update = currentUpdate ?: return
        val manager = appUpdateManager ?: AppUpdateManagerFactory.create(activity)
        appUpdateManager = manager

        val appUpdateInfo = manager.appUpdateInfo.await()

        val updateOptions = when (update.type) {
            UpdateType.IMMEDIATE -> AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            UpdateType.FLEXIBLE -> AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
        }

        val updateFlowResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            when (update.type) {
                UpdateType.IMMEDIATE -> {
                    activity.lifecycleScope.launch {
                        when (result.resultCode) {
                            Activity.RESULT_OK -> {
                                SnackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = UiText(R.string.inapp_update_complete)
                                    )
                                )
                            }

                            else -> activity.finish()
                        }
                    }
                }

                UpdateType.FLEXIBLE -> {
                    activity.lifecycleScope.launch {
                        SnackbarController.sendEvent(
                            when (result.resultCode) {
                                Activity.RESULT_OK -> {
                                    SnackbarEvent(
                                        message = UiText(R.string.inapp_update_complete)
                                    )
                                }

                                Activity.RESULT_CANCELED -> {
                                    SnackbarEvent(
                                        message = UiText(R.string.inapp_update_canceled)
                                    )
                                }

                                else -> {
                                    SnackbarEvent(
                                        message = UiText(R.string.inapp_update_failed)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        val starter =
            IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                val request = IntentSenderRequest.Builder(intent).setFillInIntent(fillInIntent)
                    .setFlags(flagsValues, flagsMask).build()
                updateFlowResultLauncher.launch(request)
            }

        if (
            appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(updateOptions.appUpdateType())
        ) {
            manager.startUpdateFlowForResult(
                appUpdateInfo,
                starter,
                updateOptions,
                Constants.UPDATE_REQUEST_CODE,
            )
        }
    }

    private fun postponeUpdate() {
        val delay = 24 * 60 * 60 * 1000 // 24 hours
        val postponedTime = System.currentTimeMillis() + delay
        prefs.set(Pref.updatePostponeTime, postponedTime)
    }

    private fun openGithubLink(activity: Activity) {
        Intent(Intent.ACTION_VIEW).apply {
            data = activity.getString(R.string.def_github_releases_link).toUri()
            activity.startActivity(this)
            activity.finish()
        }
    }
}

@Serializable
data class AppUpdateConfigData(
    val appVersionCode: Int,
    val type: UpdateType,
    val message: String = ""
)

@Serializable
enum class UpdateType {
    IMMEDIATE, FLEXIBLE
}
