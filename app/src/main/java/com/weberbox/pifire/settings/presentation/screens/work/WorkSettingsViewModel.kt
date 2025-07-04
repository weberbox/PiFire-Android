package com.weberbox.pifire.settings.presentation.screens.work

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.WorkContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WorkSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
): BaseViewModel<WorkContract.Event, WorkContract.State, WorkContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = WorkContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: WorkContract.Event) {
        toggleLoading(true)
        when (event) {
            is WorkContract.Event.SetAugerOnTime -> setAugerOnTime(event.time)
            is WorkContract.Event.SetAugerOffTime -> setAugerOffTime(event.time)
            is WorkContract.Event.SetPMode -> setPMode(event.mode)
            is WorkContract.Event.SetSPlusEnabled -> setSPlusEnabled(event.enabled)
            is WorkContract.Event.SetFanRampEnabled -> setFanRampEnabled(event.enabled)
            is WorkContract.Event.SetFanRampDutyCycle -> setFanRampDutyCycle(event.dutyCycle)
            is WorkContract.Event.SetFanOnTime -> setFanOnTime(event.time)
            is WorkContract.Event.SetFanOffTime -> setFanOffTime(event.time)
            is WorkContract.Event.SetMinTemp -> setMinTemp(event.temp)
            is WorkContract.Event.SetMaxTemp -> setMaxTemp(event.temp)
            is WorkContract.Event.SetLidOpenDetectEnabled -> setLidOpenDetectEnabled(event.enabled)
            is WorkContract.Event.SetLidOpenThresh -> setLidOpenThresh(event.thresh)
            is WorkContract.Event.SetLidOpenPauseTime -> setLidOpenPauseTime(event.time)
            is WorkContract.Event.SetKeepWarmEnabled -> setKeepWarmEnabled(event.enabled)
            is WorkContract.Event.SetKeepWarmTemp -> setKeepWarmTemp(event.temp)
            is WorkContract.Event.SetCntrlrSelected -> setCntrlrSelected(event.selected)
            is WorkContract.Event.SetCntrlrPidPb -> setCntrlrPidPb(event.pb)
            is WorkContract.Event.SetCntrlrPidTd -> setCntrlrPidTd(event.td)
            is WorkContract.Event.SetCntrlrPidTi -> setCntrlrPidTi(event.ti)
            is WorkContract.Event.SetCntrlrPidCenter -> setCntrlrPidCenter(event.center)
            is WorkContract.Event.SetCntrlrPidAcCenter -> setCntrlrPidAcCenter(event.center)
            is WorkContract.Event.SetCntrlrPidAcPb -> setCntrlrPidAcPb(event.pb)
            is WorkContract.Event.SetCntrlrPidAcStable -> setCntrlrPidAcStable(event.stable)
            is WorkContract.Event.SetCntrlrPidAcTd -> setCntrlrPidAcTd(event.td)
            is WorkContract.Event.SetCntrlrPidAcTi -> setCntrlrPidAcTi(event.ti)
            is WorkContract.Event.SetCntrlrPidSpCenter -> setCntrlrPidSpCenter(event.center)
            is WorkContract.Event.SetCntrlrPidSpPb -> setCntrlrPidSpPb(event.pb)
            is WorkContract.Event.SetCntrlrPidSpStable -> setCntrlrPidSpStable(event.stable)
            is WorkContract.Event.SetCntrlrPidSpTau -> setCntrlrPidSpTau(event.tau)
            is WorkContract.Event.SetCntrlrPidSpTd -> setCntrlrPidSpTd(event.td)
            is WorkContract.Event.SetCntrlrPidSpTheta -> setCntrlrPidSpTheta(event.theta)
            is WorkContract.Event.SetCntrlrPidSpTi -> setCntrlrPidSpTi(event.ti)
            is WorkContract.Event.SetCntrlrTime -> setCntrlrTime(event.time)
            is WorkContract.Event.SetCntrlruMax -> setCntrlruMax(event.uMax)
            is WorkContract.Event.SetCntrlruMin -> setCntrlruMin(event.uMin)
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

    private fun setAugerOnTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setAugerOnTime(time))
        }
    }

    private fun setAugerOffTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setAugerOffTime(time))
        }
    }

    private fun setPMode(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPMode(mode))
        }
    }

    private fun setSPlusEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setSPlusEnabled(enabled))
        }
    }

    private fun setFanRampEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setFanRampEnabled(enabled))
        }
    }

    private fun setFanRampDutyCycle(dutyCycle: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setFanRampDutyCycle(dutyCycle))
        }
    }

    private fun setFanOnTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setFanOnTime(time))
        }
    }

    private fun setFanOffTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setFanOffTime(time))
        }
    }

    private fun setMinTemp(temp: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMinTemp(temp))
        }
    }

    private fun setMaxTemp(temp: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMaxTemp(temp))
        }
    }

    private fun setLidOpenDetectEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setLidOpenDetectEnabled(enabled))
        }
    }

    private fun setLidOpenThresh(thresh: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setLidOpenThresh(thresh))
        }
    }

    private fun setLidOpenPauseTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setLidOpenPauseTime(time))
        }
    }

    private fun setKeepWarmEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setKeepWarmEnabled(enabled))
        }
    }

    private fun setKeepWarmTemp(temp: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setKeepWarmTemp(temp))
        }
    }

    private fun setCntrlrSelected(selected: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrSelected(selected))
        }
    }

    private fun setCntrlrPidPb(pb: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidPb(pb))
        }
    }

    private fun setCntrlrPidTd(td: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidTd(td))
        }
    }

    private fun setCntrlrPidTi(ti: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidTi(ti))
        }
    }

    private fun setCntrlrPidCenter(center: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidCenter(center))
        }
    }

    private fun setCntrlrPidAcPb(pb: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidAcPb(pb))
        }
    }

    private fun setCntrlrPidAcTd(td: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidAcTd(td))
        }
    }

    private fun setCntrlrPidAcTi(ti: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidAcTi(ti))
        }
    }

    private fun setCntrlrPidAcStable(stable: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidAcStable(stable))
        }
    }

    private fun setCntrlrPidAcCenter(center: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidAcCenter(center))
        }
    }

    private fun setCntrlrPidSpPb(pb: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidSpPb(pb))
        }
    }

    private fun setCntrlrPidSpTd(td: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidSpTd(td))
        }
    }

    private fun setCntrlrPidSpTi(ti: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidSpTi(ti))
        }
    }

    private fun setCntrlrPidSpStable(stable: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidSpStable(stable))
        }
    }

    private fun setCntrlrPidSpCenter(center: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidSpCenter(center))
        }
    }

    private fun setCntrlrPidSpTau(tau: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidSpTau(tau))
        }
    }

    private fun setCntrlrPidSpTheta(theta: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrPidSpTheta(theta))
        }
    }

    private fun setCntrlrTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlrTime(time))
        }
    }

    private fun setCntrlruMin(uMin: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlruMin(uMin))
        }
    }

    private fun setCntrlruMax(uMax: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setCntrlruMax(uMax))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.IO) {
                    setEffect {
                        WorkContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true
                        )
                    }
                }
            }

            is Result.Success -> {
                settingsRepo.updateServerSettings(result.data)
                withContext(Dispatchers.IO) {
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