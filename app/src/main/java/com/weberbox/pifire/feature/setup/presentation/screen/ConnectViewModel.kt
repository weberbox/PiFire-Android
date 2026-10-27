package com.weberbox.pifire.feature.setup.presentation.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.UrlBuilder
import com.weberbox.pifire.core.network.headers.HeadersData.Headers
import com.weberbox.pifire.core.network.headers.HeadersManager
import com.weberbox.pifire.core.remoteconfig.ServerInfo
import com.weberbox.pifire.core.remoteconfig.ServerSupportManager
import com.weberbox.pifire.core.remoteconfig.ServerSupportResult
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.model.FieldInput
import com.weberbox.pifire.core.ui.model.InputState
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.core.ui.util.createUrl
import com.weberbox.pifire.core.ui.util.isNotSecureUrl
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import com.weberbox.pifire.core.ui.util.validateUrl
import com.weberbox.pifire.feature.setup.data.model.VersionsData
import com.weberbox.pifire.feature.setup.data.repo.SetupRepo
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import com.weberbox.pifire.feature.setup.presentation.contract.ConnectContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val serverSupportManager: ServerSupportManager,
    private val serverDataCache: ServerDataCache,
    private val headersManager: HeadersManager,
    private val setupRepo: SetupRepo,
) : BaseViewModel<ConnectContract.Event, ConnectContract.State, ConnectContract.Effect>() {
    private var headersData by mutableStateOf(Headers())
    private var serverData by mutableStateOf(Server())

    init {
        collectServerData()
        collectHeadersData()
    }

    override fun setInitialState() = ConnectContract.State(
        serverAddress = InputState(),
        isLoading = false,
    )

    override fun handleEvents(event: ConnectContract.Event) {
        when (event) {
            is ConnectContract.Event.ValidateAddress -> validateAddress(event.address)
            is ConnectContract.Event.GetServerVersions -> checkSecureAuth()
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
                                hasInteracted = data.address.isNotBlank(),
                            ),
                        ),
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
        val normalizedAddress = lowercaseSchemeAndHost(address)
        val errorStatus = validateUrl(normalizedAddress)
        if (!errorStatus.isError) {
            serverData = serverData.copy(address = createUrl(normalizedAddress))
        }
        setState {
            copy(
                serverAddress = viewState.value.serverAddress.copy(
                    input = FieldInput(
                        value = address,
                        hasInteracted = true,
                    ),
                    error = errorStatus,
                ),
            )
        }
    }

    private fun checkSecureAuth() {
        viewModelScope.launch {
            if (serverData.address.isNotSecureUrl() &&
                serverData.headersEnabled or serverData.credentialsEnabled
            ) {
                DialogController.sendEvent(
                    event = DialogEvent(
                        title = UiText(R.string.dialog_auth_with_unsecure_protocol_title),
                        message = UiText(R.string.dialog_auth_with_unsecure_protocol_message),
                        dismissible = false,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.continue_text),
                            action = {
                                getServerVersions()
                            },
                        ),
                        negativeAction = DialogAction(
                            buttonText = UiText(R.string.cancel),
                            action = { },
                        ),
                    ),
                )
            } else {
                getServerVersions()
            }
        }
    }

    private fun getServerVersions() {
        toggleLoading(true)
        viewModelScope.launch {
            when (
                val result =
                    setupRepo.getVersions(
                        url = buildVersionsUrl(),
                        headerMap = headersManager.buildSetupServerHeaders(
                            credentials = headersData.credentials,
                            headers = headersData.extraHeaders,
                        ),
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
                                            R.string.setup_server_self_signed_title,
                                        ),
                                        message = UiText(
                                            R.string.setup_server_self_signed_message,
                                        ),
                                        positiveAction = DialogAction(
                                            buttonText = UiText(R.string.close),
                                            action = { },
                                        ),
                                    ),
                                )
                            }
                        }

                        DataError.Network.AUTHENTICATION -> {
                            setEffect {
                                ConnectContract.Effect.Dialog(
                                    dialogEvent = DialogEvent(
                                        title = UiText(
                                            R.string.setup_server_auth_error_title,
                                        ),
                                        message = UiText(
                                            R.string.setup_server_auth_error_message,
                                        ),
                                        positiveAction = DialogAction(
                                            buttonText = UiText(R.string.close),
                                            action = { },
                                        ),
                                    ),
                                )
                            }
                        }

                        DataError.Network.NOT_PIFIRE, DataError.Network.CLOUDFLARE_ACCESS -> {
                            setEffect {
                                ConnectContract.Effect.Dialog(
                                    dialogEvent = DialogEvent(
                                        title = UiText(
                                            R.string.setup_server_not_pifire_title,
                                        ),
                                        message = result.error.asUiText(),
                                        positiveAction = DialogAction(
                                            buttonText = UiText(R.string.close),
                                            action = { },
                                        ),
                                    ),
                                )
                            }
                        }

                        else ->
                            setEffect {
                                ConnectContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true,
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
                build = versionsData.build,
            ),
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
                                supportResult.currentBuild,
                            ),
                        ),
                        dismissible = !supportResult.isMandatory,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.close),
                            action = { },
                        ),
                        negativeAction = if (supportResult.isMandatory) {
                            null
                        } else {
                            DialogAction(
                                buttonText = UiText(R.string.continue_text),
                                action = {
                                    toggleLoading(true)
                                    getServerSettings()
                                },
                            )
                        },
                    ),
                )
            }

            is ServerSupportResult.UnsupportedMax -> {
                toggleLoading(false)
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
                            buttonText = UiText(R.string.close),
                            action = { },
                        ),
                        negativeAction = if (supportResult.isMandatory) {
                            null
                        } else {
                            DialogAction(
                                buttonText = UiText(R.string.continue_text),
                                action = {
                                    toggleLoading(true)
                                    getServerSettings()
                                },
                            )
                        },
                    ),
                )
            }

            is ServerSupportResult.Untested -> {
                toggleLoading(false)
                DialogController.sendEvent(
                    DialogEvent(
                        title = UiText(
                            R.string.dialog_untested_app_version_title,
                        ),
                        message = UiText(
                            R.string.dialog_untested_app_version_message,
                        ),
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.continue_text),
                            action = {
                                toggleLoading(true)
                                getServerSettings()
                            },
                        ),
                        negativeAction = DialogAction(
                            buttonText = UiText(R.string.close),
                            action = { },
                        ),
                    ),
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
                        error = true,
                    )
                }
            }

            is Result.Success -> {
                toggleLoading(false)
                serverData = serverData.copy(
                    uuid = result.data.uuid,
                    name = result.data.settings.grillName,
                    settings = result.data.settings,
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

    // A reverse proxy path, query or credentials can be case-sensitive; scheme and host are not
    private fun lowercaseSchemeAndHost(address: String): String {
        val schemeEnd = address.indexOf("://")
        val authorityStart = if (schemeEnd >= 0) schemeEnd + 3 else 0
        val authorityEnd = address.indexOfAny(
            chars = charArrayOf('/', '?', '#'),
            startIndex = authorityStart,
        ).takeIf { it >= 0 } ?: address.length
        val authority = address.substring(authorityStart, authorityEnd)
        val hostStart = authority.lastIndexOf('@') + 1
        return address.substring(0, authorityStart).lowercase() +
            authority.substring(0, hostStart) +
            authority.substring(hostStart).lowercase() +
            address.substring(authorityEnd)
    }
}
