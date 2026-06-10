package com.weberbox.pifire.core.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Analytics
import com.weberbox.pifire.common.data.repo.RemoteConfigRepository
import com.weberbox.pifire.common.domain.AnalyticsEvent
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.SnackbarAction
import com.weberbox.pifire.common.presentation.util.SnackbarController
import com.weberbox.pifire.common.presentation.util.SnackbarEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.settings.data.model.local.Pref
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject

class UpdateManager @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val analytics: Analytics,
    private val prefs: Prefs
) {
    private var currentUpdate: AppUpdateConfigData? = null
    private var appUpdateManager: AppUpdateManager? = null
    private var updateLauncher: ActivityResultLauncher<IntentSenderRequest>? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    private fun getAppUpdateManager(context: Context): AppUpdateManager {
        return appUpdateManager ?: AppUpdateManagerFactory.create(context).also {
            appUpdateManager = it
        }
    }

    suspend fun checkForUpdate(activity: ComponentActivity) {
        remoteConfigRepository.fetchAppUpdateConfig()?.also { config ->
            currentUpdate = config
            when (config.type) {
                UpdateType.IMMEDIATE -> {
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
                    analytics.logEvent(
                        name = AnalyticsEvent.UpdateAvailable.name,
                        params = mapOf(
                            AnalyticsEvent.Param.UpdateType.key to UpdateType.IMMEDIATE.name
                        )
                    )
                }

                UpdateType.FLEXIBLE -> {
                    if (prefs.get(Pref.updatePostponeTime) < System.currentTimeMillis()) {
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
                        analytics.logEvent(
                            name = AnalyticsEvent.UpdateAvailable.name,
                            params = mapOf(
                                AnalyticsEvent.Param.UpdateType.key to UpdateType.FLEXIBLE.name
                            )
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    fun register(activity: ComponentActivity) {
        val manager = getAppUpdateManager(activity)

        // Monitor flexible updates
        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            when (state.installStatus()) {
                InstallStatus.DOWNLOADING -> {
                    val bytesDownloaded = state.bytesDownloaded()
                    val totalBytes = state.totalBytesToDownload()
                    if (totalBytes > 0) {
                        val progress = bytesDownloaded.toFloat() / totalBytes
                        SnackbarController.setUpdateProgress(progress)
                    }
                }

                InstallStatus.DOWNLOADED -> {
                    notifyUpdateDownloaded(activity)
                }
            }
        }.also {
            manager.registerListener(it)
        }

        updateLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            val update = currentUpdate ?: return@registerForActivityResult

            when (update.type) {
                UpdateType.IMMEDIATE -> {
                    if (result.resultCode != Activity.RESULT_OK) {
                        // If an immediate update is canceled or fails,
                        // you should usually exit the app.
                        activity.finish()
                    }
                }

                UpdateType.FLEXIBLE -> {
                    activity.lifecycleScope.launch {
                        when (result.resultCode) {
                            Activity.RESULT_OK -> {
                                SnackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = UiText(R.string.inapp_update_downloading),
                                        duration = SnackbarDuration.Indefinite,
                                        showProgress = true
                                    )
                                )
                            }

                            Activity.RESULT_CANCELED -> {
                                SnackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = UiText(R.string.inapp_update_canceled)
                                    )
                                )
                            }

                            else -> {
                                SnackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = UiText(R.string.inapp_update_failed)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun unregister() {
        installStateUpdatedListener?.let {
            appUpdateManager?.unregisterListener(it)
        }
        installStateUpdatedListener = null
    }

    fun resumeUpdateIfNeeded(activity: ComponentActivity) {
        val manager = getAppUpdateManager(activity)
        manager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                notifyUpdateDownloaded(activity)
            } else if (appUpdateInfo.updateAvailability() ==
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                startUpdateFlow(activity, appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }
    }

    private suspend fun launchUpdate(activity: ComponentActivity) {
        val update = currentUpdate ?: return
        val type = when (update.type) {
            UpdateType.IMMEDIATE -> AppUpdateType.IMMEDIATE
            UpdateType.FLEXIBLE -> AppUpdateType.FLEXIBLE
        }

        val manager = getAppUpdateManager(activity)
        try {
            val appUpdateInfo = manager.appUpdateInfo.await()
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(type)
            ) {
                startUpdateFlow(activity, appUpdateInfo, type)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch update flow")
        }
    }

    private fun startUpdateFlow(
        activity: ComponentActivity,
        appUpdateInfo: AppUpdateInfo,
        type: Int
    ) {
        val launcher = updateLauncher ?: return
        val manager = getAppUpdateManager(activity)

        val starter = IntentSenderForResultStarter { intent,
                                                     _,
                                                     fillInIntent,
                                                     flagsMask,
                                                     flagsValues,
                                                     _,
                                                     _ ->

            val request = IntentSenderRequest.Builder(intent)
                .setFillInIntent(fillInIntent)
                .setFlags(flagsValues, flagsMask)
                .build()

            launcher.launch(request)
        }

        manager.startUpdateFlowForResult(
            appUpdateInfo,
            starter,
            AppUpdateOptions.newBuilder(type).build(),
            Constants.UPDATE_REQUEST_CODE,
        )
    }

    private fun notifyUpdateDownloaded(activity: ComponentActivity) {
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

    private fun postponeUpdate() {
        val delay = 24 * 60 * 60 * 1000 // 24 hours
        val postponedTime = System.currentTimeMillis() + delay
        prefs.set(Pref.updatePostponeTime, postponedTime)
        analytics.logEvent(AnalyticsEvent.PostponeUpdate.name)
    }

    private fun openGithubLink(activity: Activity) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Constants.GITHUB_RELEASE_LINK.toUri()
            activity.startActivity(this)
            activity.finish()
        }
        analytics.logEvent(
            name = AnalyticsEvent.ButtonClick.name,
            params = mapOf(
                AnalyticsEvent.Param.ButtonAction.key to "githubUpdateLink"
            )
        )
    }
}

@Serializable
data class AppUpdateConfigData(
    val appVersionCode: Int,
    val type: UpdateType,
    val message: String = "",
    val isAlpha: Boolean = false
)

@Serializable
enum class UpdateType {
    IMMEDIATE, FLEXIBLE
}
