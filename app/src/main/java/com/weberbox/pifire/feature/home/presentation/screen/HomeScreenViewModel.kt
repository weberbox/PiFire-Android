package com.weberbox.pifire.feature.home.presentation.screen

import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.model.dashboard.RunningMode
import com.weberbox.pifire.core.notification.OneSignalManager
import com.weberbox.pifire.core.remoteconfig.ServerInfo
import com.weberbox.pifire.core.remoteconfig.ServerSupportManager
import com.weberbox.pifire.core.remoteconfig.ServerSupportResult
import com.weberbox.pifire.core.repo.ConnectionRepo
import com.weberbox.pifire.core.repo.PrefsRepo
import com.weberbox.pifire.core.repo.PushRepo
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import com.weberbox.pifire.feature.dashboard.data.repo.DashRepo
import com.weberbox.pifire.feature.home.presentation.contract.HomeContract
import com.weberbox.pifire.navigation.NavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val dashRepo: DashRepo,
    private val serverSupportManager: ServerSupportManager,
    private val sessionStateHolder: SessionStateHolder,
    private val oneSignalManager: OneSignalManager,
    private val connectionRepo: ConnectionRepo,
    private val settingsRepo: SettingsRepo,
    private val analytics: Analytics,
    private val prefsRepo: PrefsRepo,
    private val pushRepo: PushRepo,
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    init {
        checkIfAppUpdated()
        collectPrefsFlow()
        collectConnectedState()
        collectDashDataState()
        setUpPush()
        checkServerSupported()
    }

    override fun setInitialState() = HomeContract.State(
        showBottomBar = true,
        isConnected = true,
        isHoldMode = false,
        lidOpenDetectEnabled = false,
        biometricSettingsPrompt = false,
        grillName = "",
        isInitialLoading = true,
        isDataError = false,
    )

    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.TriggerLidOpen -> triggerLidOpen()
            is HomeContract.Event.SignOut -> signOut()
        }
    }

    private fun triggerLidOpen() {
        viewModelScope.launch {
            when (val result = dashRepo.sendToggleLidOpen(true)) {
                is Result.Error -> {
                    setEffect {
                        HomeContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true,
                        )
                    }
                }

                is Result.Success -> {}
            }
        }
    }

    private fun signOut() {
        connectionRepo.disconnect()
        sessionStateHolder.clearSessionState()
        setEffect {
            HomeContract.Effect.Navigation.NavRoute(
                route = NavGraph.LandingDest.Landing(false),
                popUpToRoute = NavGraph.HomeDest.DashHome,
            )
        }
        analytics.logEvent(AnalyticsEvent.SignOut.name)
    }

    private fun collectPrefsFlow() {
        collectAndUpdateState(Pref.showBottomBar) { copy(showBottomBar = it) }
        collectAndUpdateState(Pref.biometricSettingsPrompt) { copy(biometricSettingsPrompt = it) }
    }

    private fun collectConnectedState() {
        viewModelScope.launch {
            sessionStateHolder.isConnectedState.collect { connected ->
                setState {
                    copy(
                        isConnected = connected,
                    )
                }
            }
        }
    }

    private fun collectDashDataState() {
        viewModelScope.launch {
            sessionStateHolder.dashDataState.collect { dashData ->
                dashData?.also {
                    setState {
                        copy(
                            grillName = dashData.grillName,
                            lidOpenDetectEnabled = dashData.lidOpenDetectEnabled,
                            isHoldMode = dashData.currentMode == RunningMode.Hold.name,
                        )
                    }
                }
            }
        }
    }

    // API v2 servers get relay push (and lose this phone's old OneSignal entry); Socket.IO ones keep OneSignal.
    private fun setUpPush() {
        viewModelScope.launch {
            if (!connectionRepo.isApiV2) {
                oneSignalManager.checkOneSignalStatus()
                return@launch
            }
            // Same consent as OneSignal: the user agreed to push notifications in setup.
            if (!pushRepo.isRelayConfigured || !oneSignalManager.getUserConsent()) return@launch
            val result = pushRepo.registerWithServer(oneSignalPlayerId = oneSignalManager.playerId())
            if (result is Result.Error) Timber.w("App push registration failed: %s", result.error)
        }
    }

    private fun checkServerSupported() {
        viewModelScope.launch {
            when (val result = settingsRepo.getSettings()) {
                is Result.Error -> {
                    settingsRepo.getCurrentServer()?.settings?.also { settings ->
                        val supportResult = serverSupportManager.isServerVersionSupported(
                            ServerInfo(
                                version = settings.serverVersion,
                                build = settings.serverBuild,
                            ),
                        )
                        checkSupportResult(supportResult)
                    }
                }

                is Result.Success -> {
                    val supportResult = serverSupportManager.isServerVersionSupported(
                        ServerInfo(
                            version = result.data.settings.serverVersion,
                            build = result.data.settings.serverBuild,
                        ),
                    )
                    checkSupportResult(supportResult)
                }
            }
        }
    }

    private suspend fun checkSupportResult(
        supportResult: ServerSupportResult,
    ) {
        when (supportResult) {
            is ServerSupportResult.Supported -> {}

            is ServerSupportResult.UnsupportedMin -> {
                analytics.logEvent(
                    name = AnalyticsEvent.UnsupportedServerMin.name,
                    params = mapOf(
                        AnalyticsEvent.Param.ServerVersion.key to supportResult.currentVersion,
                        AnalyticsEvent.Param.ServerBuild.key to supportResult.currentBuild,
                        AnalyticsEvent.Param.AppVersion.key to BuildConfig.VERSION_CODE,
                    ),
                )
                DialogController.sendEvent(
                    DialogEvent(
                        title = UiText(
                            R.string.dialog_unsupported_server_version_title,
                        ),
                        message = UiText(
                            R.string.dialog_unsupported_server_min_message,
                            uiTextArgsOf(
                                supportResult.minVersion,
                                supportResult.minBuild,
                                supportResult.currentVersion,
                                supportResult.currentBuild,
                            ),
                        ),
                        dismissible = !supportResult.isMandatory,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.exit),
                            action = { signOut() },
                        ),
                        negativeAction = if (supportResult.isMandatory) {
                            null
                        } else {
                            DialogAction(
                                buttonText = UiText(R.string.close),
                                action = { },
                            )
                        },
                    ),
                )
            }

            is ServerSupportResult.UnsupportedMax -> {
                analytics.logEvent(
                    name = AnalyticsEvent.UnsupportedServerMax.name,
                    params = mapOf(
                        AnalyticsEvent.Param.ServerVersion.key to supportResult.currentVersion,
                        AnalyticsEvent.Param.ServerBuild.key to supportResult.currentBuild,
                        AnalyticsEvent.Param.AppVersion.key to BuildConfig.VERSION_CODE,
                    ),
                )
                DialogController.sendEvent(
                    DialogEvent(
                        title = UiText(
                            R.string.dialog_unsupported_server_version_title,
                        ),
                        message = UiText(
                            R.string.dialog_unsupported_server_max_message,
                            uiTextArgsOf(
                                supportResult.maxVersion,
                                supportResult.maxBuild,
                                supportResult.currentVersion,
                                supportResult.currentBuild,
                            ),
                        ),
                        dismissible = !supportResult.isMandatory,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.update),
                            action = {
                                // Before signing out, which navigates away from the screen
                                // that handles this effect
                                setEffect {
                                    HomeContract.Effect.OpenUpdateIntent(
                                        Intent(Intent.ACTION_VIEW).apply {
                                            data = if (AppConfig.IS_PLAY_BUILD) {
                                                Constants.PLAY_RELEASE_LINK.toUri()
                                            } else {
                                                Constants.GITHUB_RELEASE_LINK.toUri()
                                            }
                                        },
                                    )
                                }
                                signOut()
                            },
                        ),
                        negativeAction = if (supportResult.isMandatory) {
                            null
                        } else {
                            DialogAction(
                                buttonText = UiText(R.string.close),
                                action = { },
                            )
                        },
                    ),
                )
            }

            is ServerSupportResult.Untested -> {
                analytics.logEvent(
                    name = AnalyticsEvent.UntestedServer.name,
                    params = mapOf(
                        AnalyticsEvent.Param.AppVersion.key to BuildConfig.VERSION_CODE,
                    ),
                )
                DialogController.sendEvent(
                    DialogEvent(
                        title = UiText(
                            R.string.dialog_untested_app_version_title,
                        ),
                        message = UiText(
                            R.string.dialog_untested_app_version_message,
                        ),
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.close),
                            action = { },
                        ),
                        negativeAction = DialogAction(
                            buttonText = UiText(R.string.exit),
                            action = { signOut() },
                        ),
                    ),
                )
            }
        }
    }

    private fun checkIfAppUpdated() {
        val storedAppVersion = prefsRepo.get(Pref.storedAppVersion)
        val currentVersion = BuildConfig.VERSION_CODE
        if (storedAppVersion < currentVersion) {
            prefsRepo.set(Pref.storedAppVersion, currentVersion)
            viewModelScope.launch {
                delay(1200.milliseconds)
                setEffect {
                    HomeContract.Effect.Navigation.Changelog
                }
            }
        }
    }

    private fun <T> collectAndUpdateState(
        pref: Pref<T>,
        update: HomeContract.State.(T) -> HomeContract.State,
    ) {
        viewModelScope.launch {
            prefsRepo.prefFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }
}
