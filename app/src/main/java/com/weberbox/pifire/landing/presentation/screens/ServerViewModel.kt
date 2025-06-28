package com.weberbox.pifire.landing.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.createUrl
import com.weberbox.pifire.common.presentation.util.isNotSecureUrl
import com.weberbox.pifire.common.presentation.util.validateUrl
import com.weberbox.pifire.landing.presentation.contract.ServerContract
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.BasicAuth
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.data.util.HeadersManager
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val headersManager: HeadersManager,
    val savedStateHandle: SavedStateHandle
) : BaseViewModel<ServerContract.Event, ServerContract.State, ServerContract.Effect>() {
    private var currentUuid by mutableStateOf("")

    init {
        currentUuid = savedStateHandle.toRoute<NavGraph.LandingDest.ServerSettings>().uuid
        collectSettingsData()
        collectBasicAuthData()
    }

    override fun setInitialState() = ServerContract.State(
        serverData = Server(),
        basicAuth = BasicAuth(),
        serverAddress = InputState(),
        isInitialLoading = true,
        isDataError = false
    )

    override fun handleEvents(event: ServerContract.Event) {
        when (event) {
            is ServerContract.Event.Back -> { }
            is ServerContract.Event.EnableBasicAuth -> toggleCredentialsEnabled(event.enabled)
            is ServerContract.Event.EnableHeaders -> toggleHeadersEnabled(event.enabled)
            is ServerContract.Event.UpdateBasicAuth -> updateCredentials(event.basicAuth)
            is ServerContract.Event.UpdateAddress -> updateAddress(event.address)
            is ServerContract.Event.ValidateAddress -> validateAddress(event.address)
        }
    }

    private fun collectSettingsData() {
        viewModelScope.launch {
            settingsRepo.getCurrentServerFlow(currentUuid).collect { server ->
                setState {
                    copy(
                        serverData = server,
                        serverAddress = InputState(
                            input = FieldInput(
                                value = server.address,
                                hasInteracted = server.address.isNotBlank()
                            ),
                            error = ErrorStatus(isError = false)
                        ),
                        isInitialLoading = false
                    )
                }
            }
        }
    }

    private fun collectBasicAuthData() {
        viewModelScope.launch {
            headersManager.getBasicAuthFlow(currentUuid).collect { basicAuth ->
                setState {
                    copy(
                        basicAuth = basicAuth
                    )
                }
            }
        }
    }

    private fun toggleHeadersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (viewState.value.serverAddress.input.value.isNotSecureUrl() && enabled) {
                DialogController.sendEvent(
                    event = DialogEvent(
                        title = UiText(R.string.dialog_auth_with_unsecure_protocol_title),
                        message = UiText(R.string.dialog_auth_with_unsecure_protocol_message),
                        dismissible = false,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.enable),
                            action = {
                                settingsRepo.toggleHeadersEnabled(enabled, currentUuid)
                            }
                        ),
                        negativeAction = DialogAction(
                            buttonText = UiText(R.string.cancel),
                            action = { }
                        )
                    )
                )
            } else settingsRepo.toggleHeadersEnabled(enabled, currentUuid)
        }
    }

    private fun toggleCredentialsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (viewState.value.serverAddress.input.value.isNotSecureUrl() && enabled) {
                DialogController.sendEvent(
                    event = DialogEvent(
                        title = UiText(R.string.dialog_auth_with_unsecure_protocol_title),
                        message = UiText(R.string.dialog_auth_with_unsecure_protocol_message),
                        dismissible = false,
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.enable),
                            action = {
                                settingsRepo.toggleCredentialsEnabled(enabled, currentUuid)
                            }
                        ),
                        negativeAction = DialogAction(
                            buttonText = UiText(R.string.cancel),
                            action = { }
                        )
                    )
                )
            } else settingsRepo.toggleCredentialsEnabled(enabled, currentUuid)
        }
    }

    private fun updateCredentials(basicAuth: BasicAuth) {
        viewModelScope.launch {
            headersManager.setBasicAuth(currentUuid, basicAuth)
        }
    }

    private fun updateAddress(address: String) {
        viewModelScope.launch {
            settingsRepo.updateServerAddress(createUrl(address), currentUuid)
        }
    }

    private fun validateAddress(address: String) {
        val errorStatus = validateUrl(address)
        setState {
            copy(
                serverAddress = InputState(
                    input = FieldInput(
                        value = address,
                        hasInteracted = true
                    ),
                    error = errorStatus
                )
            )
        }
    }
}