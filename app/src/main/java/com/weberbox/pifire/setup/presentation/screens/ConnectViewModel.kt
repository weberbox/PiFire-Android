package com.weberbox.pifire.setup.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.util.UrlBuilder
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.createUrl
import com.weberbox.pifire.common.presentation.util.uiTextArgsOf
import com.weberbox.pifire.common.presentation.util.validateUrl
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.util.ServerInfo
import com.weberbox.pifire.core.util.ServerSupportManager
import com.weberbox.pifire.core.util.ServerSupportResult
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers
import com.weberbox.pifire.settings.data.util.HeadersManager
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.setup.data.repo.SetupRepo
import com.weberbox.pifire.setup.data.util.ServerDataCache
import com.weberbox.pifire.setup.presentation.contract.ConnectContract
import com.weberbox.pifire.setup.presentation.model.VersionsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val serverSupportManager: ServerSupportManager,
    private val serverDataCache: ServerDataCache,
    private val headersManager: HeadersManager,
    private val setupRepo: SetupRepo
) : BaseViewModel<ConnectContract.Event, ConnectContract.State, ConnectContract.Effect>() {
    private var headersData by mutableStateOf(Headers())
    private var serverData by mutableStateOf(Server())

    init {
        collectServerData()
        collectHeadersData()
    }

    override fun setInitialState() = ConnectContract.State(
        serverAddress = InputState(),
        isLoading = false
    )

    override fun handleEvents(event: ConnectContract.Event) {
        when (event) {
            is ConnectContract.Event.ValidateAddress -> validateAddress(event.address)
            is ConnectContract.Event.GetServerVersions -> getServerVersions()
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            serverDataCache.getServerData().collect { data ->
                serverData = data
                setState {
                    copy(
                        serverAddress = InputState(
                            input = FieldInput(
                                value = data.address,
                                hasInteracted = data.address.isNotBlank()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun collectHeadersData() {
        viewModelScope.launch {
            serverDataCache.getHeaderData().collect { data ->
                headersData = data
            }
        }
    }

    private fun validateAddress(address: String) {
        val lowercaseAddress = address.lowercase()
        val errorStatus = validateUrl(lowercaseAddress)
        if (!errorStatus.isError) {
            serverData = serverData.copy(address = createUrl(lowercaseAddress))
        }
        setState {
            copy(
                serverAddress = viewState.value.serverAddress.copy(
                    input = FieldInput(
                        value = lowercaseAddress,
                        hasInteracted = true
                    ),
                    error = errorStatus
                )
            )
        }
    }

    private fun getServerVersions() {
        toggleLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result =
                setupRepo.getVersions(
                    url = buildVersionsUrl(),
                    headerMap = headersManager.buildSetupServerHeaders(
                        credentials = headersData.credentials,
                        headers = headersData.extraHeaders
                    )
                )
            ) {
                is Result.Error -> {
                    toggleLoading(false)
                    when (result.error) {
                        DataError.Network.CERTIFICATE -> {
                            setEffect {
                                ConnectContract.Effect.Dialog(
                                    dialogEvent = DialogEvent(
                                        title = UiText(
                                            R.string.setup_server_self_signed_title
                                        ),
                                        message = UiText(
                                            R.string.setup_server_self_signed_message
                                        ),
                                        positiveAction = DialogAction(
                                            buttonText = UiText(R.string.close),
                                            action = { }
                                        )
                                    )
                                )
                            }
                        }

                        DataError.Network.AUTHENTICATION -> {
                            setEffect {
                                ConnectContract.Effect.Dialog(
                                    dialogEvent = DialogEvent(
                                        title = UiText(
                                            R.string.setup_server_auth_error_title
                                        ),
                                        message = UiText(
                                            R.string.setup_server_auth_error_message
                                        ),
                                        positiveAction = DialogAction(
                                            buttonText = UiText(R.string.close),
                                            action = { }
                                        )
                                    )
                                )
                            }
                        }

                        else ->
                            setEffect {
                                ConnectContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true
                                )
                            }
                    }
                }

                is Result.Success -> {
                    verifyServerVersionSupported(result.data)
                }
            }
        }
    }

    private suspend fun verifyServerVersionSupported(versionsData: VersionsData) {
        val supportResult = serverSupportManager.isServerVersionSupported(
            ServerInfo(
                version = versionsData.version,
                build = versionsData.build
            )
        )

        when (supportResult) {
            is ServerSupportResult.Supported -> getServerSettings()

            is ServerSupportResult.UnsupportedMin -> {
                toggleLoading(false)
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
                            buttonText = UiText(R.string.close),
                            action = { }
                        ),
                        negativeAction = if (supportResult.isMandatory) null else
                            DialogAction(
                                buttonText = UiText(R.string.continue_),
                                action = {
                                    toggleLoading(true)
                                    getServerSettings()
                                }
                            )
                    )
                )
            }

            is ServerSupportResult.UnsupportedMax -> {
                toggleLoading(false)
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
                            buttonText = UiText(R.string.close),
                            action = { }
                        ),
                        negativeAction = if (supportResult.isMandatory) null else
                            DialogAction(
                                buttonText = UiText(R.string.continue_),
                                action = {
                                    toggleLoading(true)
                                    getServerSettings()
                                }
                            )
                    )
                )
            }

            is ServerSupportResult.Untested -> {
                toggleLoading(false)
                DialogController.sendEvent(
                    DialogEvent(
                        title = UiText(
                            R.string.dialog_untested_app_version_title
                        ),
                        message = UiText(
                            R.string.dialog_untested_app_version_message
                        ),
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.continue_),
                            action = {
                                toggleLoading(true)
                                getServerSettings()
                            }
                        ),
                        negativeAction = DialogAction(
                            buttonText = UiText(R.string.close),
                            action = { }
                        )
                    )
                )
            }
        }
    }

    private suspend fun getServerSettings() {
        when (val result = setupRepo.getSettingsData(server = serverData)) {
            is Result.Error -> {
                toggleLoading(false)
                setEffect {
                    ConnectContract.Effect.Notification(
                        text = result.error.asUiText(),
                        error = true
                    )
                }
            }

            is Result.Success -> {
                toggleLoading(false)
                serverData = serverData.copy(
                    uuid = result.data.uuid,
                    settings = result.data.settings
                )
                serverDataCache.saveServerData(serverData)

                setEffect {
                    ConnectContract.Effect.Navigation.Forward
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        setState { copy(isLoading = loading) }
    }

    private fun buildVersionsUrl(): String {
        val safeAddress = serverData.address.let {
            it.ifBlank { createUrl(Constants.BASE_URL) }
        }
        return UrlBuilder.from(safeAddress)
            .addPath("api")
            .addPath("get")
            .addPath("versions")
            .build()
    }
}