package com.weberbox.pifire.feature.settings.presentation.screen.probe

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.ProbeInfoUpdate
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.feature.settings.presentation.contract.ProbeContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProbeSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
) : BaseViewModel<ProbeContract.Event, ProbeContract.State, ProbeContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = ProbeContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
    )

    override fun handleEvents(event: ProbeContract.Event) {
        toggleLoading(true)
        when (event) {
            is ProbeContract.Event.SetTempUnits -> setTempUnits(event.units)
            is ProbeContract.Event.UpdateProbe -> updateProbe(event.probeInfo)
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            settingsRepo.getCurrentServerFlow().collect { server ->
                setState {
                    copy(
                        serverData = server,
                        isInitialLoading = false,
                    )
                }
            }
        }
    }

    private fun updateProbe(probeInfo: ProbeInfoUpdate) {
        viewModelScope.launch {
            handleResult(settingsRepo.setUpdateProbeInfo(probeInfo))
        }
    }

    private fun setTempUnits(units: String) {
        viewModelScope.launch {
            handleResult(settingsRepo.setTempUnits(units))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                setEffect {
                    ProbeContract.Effect.Notification(
                        text = result.error.asUiText(),
                        error = true,
                    )
                }
            }

            is Result.Success -> {
                settingsRepo.updateServerSettings(result.data)
                setState {
                    copy(
                        serverData = result.data,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        setState {
            copy(
                isLoading = loading,
            )
        }
    }
}
