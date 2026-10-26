package com.weberbox.pifire.feature.update.data.installer

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.google.android.play.core.install.model.ActivityResult as PlayActivityResult

/**
 * Google Play's in-app updates. Play decides what this device can install (Remote Config only
 * says what exists and what's required); a flexible update downloads in the background and is
 * finished with a restart. Installs that didn't come from Play never see an update here.
 */
@Singleton
class PlayAppInstaller @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    // Created on first use, so the github flavor never touches Play.
    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(context).also { manager ->
            manager.registerListener(InstallStateUpdatedListener { state -> _progress.value = state.toProgress() })
        }
    }
    private val _progress = MutableStateFlow<InstallProgress>(InstallProgress.Idle)
    private var launcher: ActivityResultLauncher<IntentSenderRequest>? = null

    val progress: StateFlow<InstallProgress> = _progress.asStateFlow()

    /** Registers the update flow's result with [activity]; call from its `onCreate`. */
    fun bind(activity: ComponentActivity) {
        val registered = activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            when (result.resultCode) {
                // Flexible: the download starts and the listener follows it. Immediate: Play
                // installs and restarts the app.
                Activity.RESULT_OK -> {}

                PlayActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    _progress.value = InstallProgress.Failed(UiText(resId = R.string.update_failed_message))
                }

                // Put off: nothing is downloading.
                else -> {
                    if (_progress.value !is InstallProgress.Downloaded) _progress.value = InstallProgress.Idle
                }
            }
        }
        launcher = registered
        activity.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    if (launcher === registered) launcher = null
                }
            },
        )
    }

    suspend fun findUpdate(): AvailableUpdate? {
        val info = appUpdateInfo() ?: return null
        // Downloaded while the app was closed: it only needs the restart.
        if (info.installStatus() == InstallStatus.DOWNLOADED) _progress.value = InstallProgress.Downloaded
        val availability = info.updateAvailability()
        if (availability != UpdateAvailability.UPDATE_AVAILABLE &&
            availability != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
        ) {
            return null
        }
        return AvailableUpdate(
            versionCode = info.availableVersionCode(),
            sizeBytes = info.totalBytesToDownload().takeIf { it > 0 },
            isInProgress = availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
        )
    }

    /**
     * [immediate] asks for Play's full-screen flow, otherwise a background download. Play doesn't
     * always allow the type asked for, so the other one is used rather than nothing. False when
     * it couldn't be started.
     */
    suspend fun startUpdate(immediate: Boolean): Boolean {
        val launcher = launcher ?: return false
        // Each AppUpdateInfo starts one flow, so get a fresh one.
        val info = appUpdateInfo() ?: return false
        val preferred = if (immediate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
        val type = listOf(preferred, AppUpdateType.FLEXIBLE, AppUpdateType.IMMEDIATE)
            .firstOrNull { info.isUpdateTypeAllowed(it) }
            ?: return false
        return appUpdateManager.startUpdateFlowForResult(info, launcher, AppUpdateOptions.defaultOptions(type))
    }

    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private suspend fun appUpdateInfo(): AppUpdateInfo? = try {
        appUpdateManager.appUpdateInfo.await()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.w(e, "Google Play couldn't check for updates")
        null
    }

    private fun InstallState.toProgress(): InstallProgress = when (installStatus()) {
        InstallStatus.PENDING -> {
            InstallProgress.Downloading(fraction = null)
        }

        InstallStatus.DOWNLOADING -> {
            InstallProgress.Downloading(
                fraction = totalBytesToDownload().takeIf { it > 0 }?.let { bytesDownloaded().toFloat() / it },
            )
        }

        InstallStatus.DOWNLOADED -> {
            InstallProgress.Downloaded
        }

        InstallStatus.INSTALLING -> {
            InstallProgress.Installing
        }

        InstallStatus.FAILED -> {
            InstallProgress.Failed(UiText(resId = R.string.update_failed_message))
        }

        else -> {
            InstallProgress.Idle
        }
    }
}
