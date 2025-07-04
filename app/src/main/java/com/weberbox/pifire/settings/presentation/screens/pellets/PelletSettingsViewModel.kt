package com.weberbox.pifire.settings.presentation.screens.pellets

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.PelletsContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PelletSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : BaseViewModel<PelletsContract.Event, PelletsContract.State, PelletsContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = PelletsContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: PelletsContract.Event) {
        toggleLoading(true)
        when (event) {
            is PelletsContract.Event.SetAugerRate -> setPelletsAugerRate(event.rate)
            is PelletsContract.Event.SetEmptyLevel -> setPelletsEmpty(event.level)
            is PelletsContract.Event.SetFullLevel -> setPelletsFull(event.level)
            is PelletsContract.Event.SetPrimeIgnition -> setPelletPrimeIgnition(event.enabled)
            is PelletsContract.Event.SetWarningEnabled -> setPelletWarningEnabled(event.enabled)
            is PelletsContract.Event.SetWarningLevel -> setPelletWarningLevel(event.level)
            is PelletsContract.Event.SetWarningTime -> setPelletWarningTime(event.time)
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

    private fun setPelletWarningEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPelletWarningEnabled(enabled))
        }
    }

    private fun setPelletPrimeIgnition(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPelletPrimeIgnition(enabled))
        }
    }

    private fun setPelletWarningTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPelletWarningTime(time))
        }
    }

    private fun setPelletWarningLevel(level: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPelletWarningLevel(level))
        }
    }

    private fun setPelletsEmpty(level: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPelletsEmpty(level))
        }
    }

    private fun setPelletsFull(level: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPelletsFull(level))
        }
    }

    private fun setPelletsAugerRate(rate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPelletsAugerRate(rate))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        PelletsContract.Effect.Notification(
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