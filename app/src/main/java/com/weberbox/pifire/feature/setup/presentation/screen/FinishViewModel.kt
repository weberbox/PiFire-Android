package com.weberbox.pifire.feature.setup.presentation.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.model.FieldInput
import com.weberbox.pifire.core.ui.model.InputState
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.validateName
import com.weberbox.pifire.feature.settings.data.api.SettingsApiImpl
import com.weberbox.pifire.feature.settings.data.model.local.HeadersData
import com.weberbox.pifire.feature.settings.data.model.local.HeadersData.Headers
import com.weberbox.pifire.feature.settings.presentation.model.SettingsData
import com.weberbox.pifire.feature.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import com.weberbox.pifire.feature.setup.presentation.contract.FinishContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FinishViewModel @Inject constructor(
    private val serverDataCache: ServerDataCache,
    private val headersDataStore: DataStore<HeadersData>,
    private val dataStore: DataStore<SettingsData>,
    private val settingsApi: SettingsApiImpl,
    private val analytics: Analytics
) : BaseViewModel<FinishContract.Event, FinishContract.State, FinishContract.Effect>() {
    private var headersData by mutableStateOf(Headers())
    private var serverData by mutableStateOf(Server())

    init {
        collectServerData()
        collectHeadersData()
    }

    override fun setInitialState() = FinishContract.State(
        grillName = InputState(),
        isLoading = false
    )

    override fun handleEvents(event: FinishContract.Event) {
        when (event) {
            is FinishContract.Event.ValidateName -> validateGrillName(event.name)
            is FinishContract.Event.SaveGrillName -> saveGrillName(event.name)
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            serverDataCache.getServerData().collect { data ->
                serverData = data
                setState {
                    copy(
                        grillName = InputState(
                            input = FieldInput(
                                value = data.name,
                                hasInteracted = data.name.isNotBlank()
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

    private fun validateGrillName(name: String) {
        val errorStatus = validateName(name)
        setState {
            copy(
                grillName = viewState.value.grillName.copy(
                    input = FieldInput(
                        value = name,
                        hasInteracted = true
                    ),
                    error = errorStatus
                )
            )
        }
    }

    private fun saveGrillName(name: String) {
        toggleLoading(true)
        serverData = serverData.copy(name = name)
        viewModelScope.launch {
            setGrillName(name)
        }
    }

    private suspend fun setGrillName(name: String) {
        settingsApi.setGrillName(name).let { result ->
            withContext(Dispatchers.Main) {
                toggleLoading(false)
                when (result) {
                    is Result.Error -> {
                        setEffect {
                            FinishContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true
                            )
                        }
                    }

                    is Result.Success -> {
                        dataStore.updateData { data ->
                            data.copy(
                                serverMap = data.serverMap.plus(serverData.uuid to serverData)
                            )
                        }
                        headersDataStore.updateData { data ->
                            data.copy(
                                headersMap = data.headersMap.plus(serverData.uuid to headersData)
                            )
                        }
                        serverDataCache.clearServerData()

                        analytics.logEvent(
                            name = AnalyticsEvent.ButtonClick.name,
                            params = mapOf(
                                AnalyticsEvent.Param.ButtonAction.key to "setupComplete"
                            )
                        )

                        setEffect {
                            FinishContract.Effect.Navigation.Forward
                        }
                    }
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        setState { copy(isLoading = loading) }
    }
}