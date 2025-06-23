package com.weberbox.pifire.setup.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.model.Credentials
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.ExtraHeader
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.setup.data.util.ServerDataCache
import com.weberbox.pifire.setup.presentation.contract.AuthContract
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val serverDataCache: ServerDataCache
) : BaseViewModel<AuthContract.Event, AuthContract.State, AuthContract.Effect>() {
    private var serverData by mutableStateOf(Server())
    private var headersData by mutableStateOf(Headers())

    init {
        collectServerData()
        collectHeadersData()
    }

    override fun setInitialState() = AuthContract.State(
        credentials = Credentials(),
        extraHeaders = emptyList()
    )

    override fun handleEvents(event: AuthContract.Event) {
        when (event) {
            is AuthContract.Event.UpdatePassword -> updatePassword(event.password)
            is AuthContract.Event.UpdateUsername -> updateUsername(event.username)
            is AuthContract.Event.UpdateHeader -> updateExtraHeader(event.header)
            is AuthContract.Event.DeleteHeader -> deleteExtraHeader(event.header)
            is AuthContract.Event.NavigateToConnect -> navigateToConnect()
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            serverDataCache.getServerData().collect { data ->
                serverData = data
            }
        }
    }

    private fun collectHeadersData() {
        viewModelScope.launch {
            serverDataCache.getHeaderData().collect { data ->
                headersData = data
                setState {
                    copy(
                        credentials = Credentials(
                            username = InputState(
                                input = FieldInput(
                                    value = data.credentials.user,
                                    hasInteracted = data.credentials.user.isNotBlank()
                                )
                            ),
                            password = InputState(
                                input = FieldInput(
                                    value = data.credentials.pass,
                                    hasInteracted = data.credentials.user.isNotBlank()
                                )
                            )
                        ),
                        extraHeaders = data.extraHeaders
                    )
                }
            }
        }
    }

    private fun updateUsername(username: String) {
        headersData = headersData.copy(
            credentials = headersData.credentials.copy(
                user = username
            )
        )
        serverData = serverData.copy(
            credentialsEnabled = headersData.credentials.user.isNotBlank() or
                    headersData.credentials.pass.isNotBlank()
        )
        setState {
            copy(
                credentials = viewState.value.credentials.copy(
                    username = InputState(
                        input = FieldInput(value = headersData.credentials.user),
                    )
                )
            )
        }
    }

    private fun updatePassword(password: String) {
        headersData = headersData.copy(
            credentials = headersData.credentials.copy(
                pass = password
            )
        )
        serverData = serverData.copy(
            credentialsEnabled = headersData.credentials.user.isNotBlank() or
                    headersData.credentials.pass.isNotBlank()
        )

        setState {
            copy(
                credentials = viewState.value.credentials.copy(
                    password = InputState(
                        input = FieldInput(value = headersData.credentials.pass),
                    )
                )
            )
        }
    }

    private fun updateExtraHeader(extraHeader: ExtraHeader) {
        headersData = headersData.copy(
            extraHeaders = headersData.extraHeaders.plus(extraHeader)
        )
        serverData = serverData.copy(
            headersEnabled = true
        )

        setState {
            copy(
                extraHeaders = headersData.extraHeaders
            )
        }
    }

    private fun deleteExtraHeader(header: ExtraHeader) {
        headersData = headersData.copy(
            extraHeaders = headersData.extraHeaders.filterNot {
                it.value == header.value && it.key == header.key
            }
        )

        setState {
            copy(
                extraHeaders = headersData.extraHeaders
            )
        }
    }

    private fun navigateToConnect() {
        serverDataCache.saveServerData(serverData)
        serverDataCache.saveHeaderData(headersData)
        setEffect {
            AuthContract.Effect.Navigation.Forward
        }
    }
}