package com.weberbox.pifire.feature.update.presentation.screen

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.remoteconfig.UpdateType
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.data.model.UpdateSource
import com.weberbox.pifire.feature.update.data.repo.AppUpdateRepo
import com.weberbox.pifire.feature.update.presentation.contract.AppUpdateContract
import com.weberbox.pifire.feature.update.presentation.model.UpdateSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * App updates over the whole app (MainActivity). Checks Remote Config on start and when the app
 * comes back after a while. A required update blocks the app; otherwise a newer version is
 * offered in a sheet.
 */
@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    private val appUpdateRepo: AppUpdateRepo,
    private val analytics: Analytics,
) : BaseViewModel<AppUpdateContract.Event, AppUpdateContract.State, AppUpdateContract.Effect>() {
    private var checkJob: Job? = null

    /** When the last check finished; null before the first. */
    private var lastCheck: TimeMark? = null

    init {
        setState { copy(source = appUpdateRepo.source) }
        observeProgress()
        check()
    }

    override fun setInitialState() = AppUpdateContract.State()

    override fun handleEvents(event: AppUpdateContract.Event) {
        when (event) {
            is AppUpdateContract.Event.OnAppResumed -> {
                val isDue = lastCheck?.let { it.elapsedNow() > RECHECK_INTERVAL } ?: true
                if (viewState.value.isRequired || isDue) check()
            }

            is AppUpdateContract.Event.OnUpdateClicked -> {
                startUpdate()
            }

            is AppUpdateContract.Event.OnInstallClicked -> {
                setState { copy(sheet = null) }
                appUpdateRepo.completeUpdate()
            }

            is AppUpdateContract.Event.OnSheetDismissed -> {
                dismissSheet()
            }

            is AppUpdateContract.Event.OnOpenStoreClicked -> {
                setEffect { AppUpdateContract.Effect.OpenStoreListing }
            }

            is AppUpdateContract.Event.OnCheckAgainClicked -> {
                check()
            }
        }
    }

    private fun check() {
        if (checkJob?.isActive == true) return
        checkJob = viewModelScope.launch {
            setState { copy(isChecking = true) }
            val policy = appUpdateRepo.fetchPolicy()
            lastCheck = TimeSource.Monotonic.markNow()
            val update = policy?.let { appUpdateRepo.findUpdate(policy = it) }
            val isRequired = policy?.isRequired == true
            val isOffered = !isRequired && update != null && !appUpdateRepo.isSnoozed(versionCode = update.versionCode)
            val previous = viewState.value
            setState {
                copy(
                    isChecking = false,
                    isRequired = isRequired,
                    update = update,
                    message = policy?.message,
                    sheet = when {
                        isRequired -> null
                        sheet != null && sheet != UpdateSheet.OFFER -> sheet
                        progress is InstallProgress.Downloaded -> UpdateSheet.READY
                        isOffered -> UpdateSheet.OFFER
                        else -> null
                    },
                )
            }
            val current = viewState.value
            if (current.isRequired && !previous.isRequired) {
                logUpdateAvailable(type = UpdateType.IMMEDIATE)
            } else if (current.sheet == UpdateSheet.OFFER && previous.sheet != UpdateSheet.OFFER) {
                logUpdateAvailable(type = UpdateType.FLEXIBLE)
            }
            // Play: an immediate update that was interrupted (e.g. the app was closed) goes on.
            if (isRequired && update?.isInProgress == true) {
                appUpdateRepo.startUpdate(immediate = true)
            }
        }
    }

    private fun observeProgress() {
        appUpdateRepo.progress
            .onEach { progress ->
                setState {
                    copy(
                        progress = progress,
                        sheet = when {
                            isRequired -> null

                            progress is InstallProgress.Downloading -> if (isDownloadHidden) sheet else UpdateSheet.DOWNLOADING

                            progress is InstallProgress.Downloaded -> UpdateSheet.READY

                            progress is InstallProgress.Failed -> UpdateSheet.FAILED

                            // Installing: Play's own screen takes over.
                            progress is InstallProgress.Installing -> null

                            else -> sheet.takeIf { it == UpdateSheet.OFFER }
                        },
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun startUpdate() {
        val state = viewState.value
        if (state.source == UpdateSource.GITHUB) {
            setState { copy(sheet = null) }
            setEffect { AppUpdateContract.Effect.OpenReleasePage }
            analytics.logEvent(
                name = AnalyticsEvent.ButtonClick.name,
                params = mapOf(AnalyticsEvent.Param.ButtonAction.key to "githubUpdateLink"),
            )
            return
        }
        if (state.update == null) {
            setEffect { AppUpdateContract.Effect.OpenStoreListing }
            return
        }
        setState { copy(sheet = null, isDownloadHidden = false) }
        viewModelScope.launch {
            // Play won't update it in the app (e.g. not installed from Play): its listing can.
            if (!appUpdateRepo.startUpdate(immediate = state.isRequired)) {
                setEffect { AppUpdateContract.Effect.OpenStoreListing }
            }
        }
    }

    private fun dismissSheet() {
        val state = viewState.value
        if (state.sheet == UpdateSheet.OFFER) {
            state.update?.let { appUpdateRepo.snooze(versionCode = it.versionCode) }
            analytics.logEvent(name = AnalyticsEvent.PostponeUpdate.name)
        }
        setState {
            copy(
                sheet = null,
                isDownloadHidden = isDownloadHidden || state.sheet == UpdateSheet.DOWNLOADING,
            )
        }
    }

    private fun logUpdateAvailable(type: UpdateType) {
        analytics.logEvent(
            name = AnalyticsEvent.UpdateAvailable.name,
            params = mapOf(AnalyticsEvent.Param.UpdateType.key to type.name),
        )
    }

    private companion object {
        /** How often coming back to the app checks again (a required update checks every time). */
        val RECHECK_INTERVAL = 30.minutes
    }
}
