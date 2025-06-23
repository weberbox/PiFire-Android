package com.weberbox.pifire.settings.presentation.screens.safety

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.SafetyContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SafetySettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
): BaseViewModel<SafetyContract.Event, SafetyContract.State, SafetyContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = SafetyContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: SafetyContract.Event) {
        toggleLoading(true)
        when (event) {
            is SafetyContract.Event.SetMaxGrillTemp -> setMaxGrillTemp(event.temp)
            is SafetyContract.Event.SetMaxStartTemp -> setMaxStartTemp(event.temp)
            is SafetyContract.Event.SetMinStartTemp -> setMinStartTemp(event.temp)
            is SafetyContract.Event.SetReigniteRetries -> setReigniteRetries(event.retries)
            is SafetyContract.Event.SetSafetyStartupCheck -> setSafetyStartupCheck(event.enabled)
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            settingsRepo.getCurrentServerFlow().collect { server ->
                setState {
                    copy(
                        serverData = server,
                        isInitialLoading = false
                    )
                }
            }
        }
    }

    private fun setSafetyStartupCheck(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setSafetyStartupCheck(enabled))
        }
    }

    private fun setMinStartTemp(temp: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMinStartTemp(temp))
        }
    }

    private fun setMaxStartTemp(temp: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMaxStartTemp(temp))
        }
    }

    private fun setMaxGrillTemp(temp: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMaxGrillTemp(temp))
        }
    }

    private fun setReigniteRetries(retries: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setReigniteRetries(retries))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        SafetyContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true
                        )
                    }
                }
            }

            is Result.Success -> {
                settingsRepo.updateServerSettings(result.data)
                withContext(Dispatchers.Main) {
                    setState {
                        copy(
                            serverData = result.data,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        setState {
            copy(
                isLoading = loading
            )
        }
    }
}