package com.weberbox.pifire.home.presentation.screens

import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.uiTextArgsOf
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.core.util.OneSignalManager
import com.weberbox.pifire.core.util.ServerInfo
import com.weberbox.pifire.core.util.ServerSupportManager
import com.weberbox.pifire.core.util.ServerSupportResult
import com.weberbox.pifire.dashboard.data.api.DashApi
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.home.presentation.contract.HomeContract
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val serverSupportManager: ServerSupportManager,
    private val sessionStateHolder: SessionStateHolder,
    private val oneSignalManager: OneSignalManager,
    private val socketManager: SocketManager,
    private val settingsRepo: SettingsRepo,
    private val dashApi: DashApi,
    private val prefs: Prefs
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    init {
        checkIfAppUpdated()
        collectPrefsFlow()
        collectConnectedState()
        collectDashDataState()
        checkOneSignalStatus()
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
        isDataError = false
    )

    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.TriggerLidOpen -> triggerLidOpen()
            is HomeContract.Event.SignOut -> signOut()
        }
    }

    private fun triggerLidOpen() {
        viewModelScope.launch {
            when (val result = dashApi.sendToggleLidOpen(true)) {
                is Result.Error -> {
                    setEffect {
                        HomeContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true
                        )
                    }
                }

                is Result.Success -> {}
            }
        }
    }

    private fun signOut() {
        socketManager.disconnect()
        sessionStateHolder.clearSessionState()
        setEffect {
            HomeContract.Effect.Navigation.NavRoute(
                route = NavGraph.LandingDest.Landing(false),
                popUp = true
            )
        }
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
                        isConnected = connected
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
                            isHoldMode = dashData.currentMode == RunningMode.Hold.name
                        )
                    }
                }
            }
        }
    }

    private fun checkOneSignalStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            oneSignalManager.checkOneSignalStatus()
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
                                build = settings.serverBuild
                            )
                        )
                        checkSupportResult(supportResult)
                    }
                }

                is Result.Success -> {
                    val supportResult = serverSupportManager.isServerVersionSupported(
                        ServerInfo(
                            version = result.data.settings.serverVersion,
                            build = result.data.settings.serverBuild
                        )
                    )
                    checkSupportResult(supportResult)
                }
            }
        }
    }

    private suspend fun checkSupportResult(
        supportResult: ServerSupportResult
    ) {
        when (supportResult) {
            is ServerSupportResult.Supported -> {}

            is ServerSupportResult.UnsupportedMin -> {
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
                                supportResult.currentBuild
                            )
                        ),
                        dismissible = !supportResult.isMandatory,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.exit),
                            action = { signOut() }
                        ),
                        negativeAction = if (supportResult.isMandatory) null else
                            DialogAction(
                                buttonText = UiText(R.string.close),
                                action = { }
                            )
                    )
                )
            }

            is ServerSupportResult.UnsupportedMax -> {
                DialogController.sendEvent(
                    DialogEvent(
                        title = UiText(
                            R.string.dialog_unsupported_server_version_title
                        ),
                        message = UiText(
                            R.string.dialog_unsupported_server_max_message,
                            uiTextArgsOf(
                                supportResult.maxVersion,
                                supportResult.maxBuild,
                                supportResult.currentVersion,
                                supportResult.currentBuild
                            )
                        ),
                        dismissible = !supportResult.isMandatory,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.update),
                            action = {
                                signOut()
                                setEffect {
                                    HomeContract.Effect.OpenUpdateIntent(
                                        Intent(Intent.ACTION_VIEW).apply {
                                            data = if (AppConfig.IS_PLAY_BUILD)
                                                Constants.PLAY_RELEASE_LINK.toUri()
                                            else
                                                Constants.GITHUB_RELEASE_LINK.toUri()
                                        }
                                    )
                                }
                            }
                        ),
                        negativeAction = if (supportResult.isMandatory) null else
                            DialogAction(
                                buttonText = UiText(R.string.close),
                                action = { }
                            )
                    )
                )
            }

            is ServerSupportResult.Untested -> {
                DialogController.sendEvent(
                    DialogEvent(
                        title = UiText(
                            R.string.dialog_untested_app_version_title
                        ),
                        message = UiText(
                            R.string.dialog_untested_app_version_message
                        ),
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.close),
                            action = { }
                        ),
                        negativeAction = DialogAction(
                            buttonText = UiText(R.string.exit),
                            action = { signOut() }
                        )
                    )
                )
            }
        }
    }

    private fun checkIfAppUpdated() {
        val storedAppVersion = prefs.get(Pref.storedAppVersion)
        val currentVersion = BuildConfig.VERSION_CODE
        if (storedAppVersion < currentVersion) {
            prefs.set(Pref.storedAppVersion, currentVersion)
            viewModelScope.launch {
                delay(1200)
                setEffect {
                    HomeContract.Effect.Navigation.Changelog
                }
            }
        }
    }

    private fun <T> collectAndUpdateState(
        pref: Pref<T>,
        update: HomeContract.State.(T) -> HomeContract.State
    ) {
        viewModelScope.launch {
            prefs.collectPrefsFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }
}