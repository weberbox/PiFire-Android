package com.weberbox.pifire.settings.presentation.screens.probe

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto.ProbesDto
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.ProbeContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProbeSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : BaseViewModel<ProbeContract.Event, ProbeContract.State, ProbeContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = ProbeContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: ProbeContract.Event) {
        toggleLoading(true)
        when (event) {
            is ProbeContract.Event.SetTempUnits -> setTempUnits(event.units)
            is ProbeContract.Event.UpdateProbe -> updateProbe(event.probeDto)
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

    private fun updateProbe(probesDto: ProbesDto) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setUpdateProbeInfo(probesDto))
        }
    }

    private fun setTempUnits(units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setTempUnits(units))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        ProbeContract.Effect.Notification(
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