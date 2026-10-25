package com.weberbox.pifire.feature.settings.presentation.screen.work

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.feature.settings.presentation.contract.WorkContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
) : BaseViewModel<WorkContract.Event, WorkContract.State, WorkContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = WorkContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
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
            is WorkContract.Event.SetFanPidEnabled -> setFanPidEnabled(event.enabled)
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
                        isInitialLoading = false,
                    )
                }
            }
        }
    }

    private fun setAugerOnTime(time: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setAugerOnTime(time))
        }
    }

    private fun setAugerOffTime(time: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setAugerOffTime(time))
        }
    }

    private fun setPMode(mode: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setPMode(mode))
        }
    }

    private fun setSPlusEnabled(enabled: Boolean) {
        viewModelScope.launch {
            handleResult(settingsRepo.setSPlusEnabled(enabled))
        }
    }

    private fun setFanRampEnabled(enabled: Boolean) {
        viewModelScope.launch {
            handleResult(settingsRepo.setFanRampEnabled(enabled))
        }
    }

    private fun setFanRampDutyCycle(dutyCycle: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setFanRampDutyCycle(dutyCycle))
        }
    }

    private fun setFanOnTime(time: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setFanOnTime(time))
        }
    }

    private fun setFanOffTime(time: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setFanOffTime(time))
        }
    }

    private fun setMinTemp(temp: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setMinTemp(temp))
        }
    }

    private fun setMaxTemp(temp: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setMaxTemp(temp))
        }
    }

    private fun setLidOpenDetectEnabled(enabled: Boolean) {
        viewModelScope.launch {
            handleResult(settingsRepo.setLidOpenDetectEnabled(enabled))
        }
    }

    private fun setLidOpenThresh(thresh: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setLidOpenThresh(thresh))
        }
    }

    private fun setLidOpenPauseTime(time: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setLidOpenPauseTime(time))
        }
    }

    private fun setFanPidEnabled(enabled: Boolean) {
        viewModelScope.launch {
            handleResult(settingsRepo.setFanPidEnabled(enabled))
        }
    }

    private fun setKeepWarmEnabled(enabled: Boolean) {
        viewModelScope.launch {
            handleResult(settingsRepo.setKeepWarmEnabled(enabled))
        }
    }

    private fun setKeepWarmTemp(temp: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setKeepWarmTemp(temp))
        }
    }

    private fun setCntrlrSelected(selected: String) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrSelected(selected))
        }
    }

    private fun setCntrlrPidPb(pb: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidPb(pb))
        }
    }

    private fun setCntrlrPidTd(td: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidTd(td))
        }
    }

    private fun setCntrlrPidTi(ti: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidTi(ti))
        }
    }

    private fun setCntrlrPidCenter(center: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidCenter(center))
        }
    }

    private fun setCntrlrPidAcPb(pb: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidAcPb(pb))
        }
    }

    private fun setCntrlrPidAcTd(td: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidAcTd(td))
        }
    }

    private fun setCntrlrPidAcTi(ti: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidAcTi(ti))
        }
    }

    private fun setCntrlrPidAcStable(stable: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidAcStable(stable))
        }
    }

    private fun setCntrlrPidAcCenter(center: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidAcCenter(center))
        }
    }

    private fun setCntrlrPidSpPb(pb: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidSpPb(pb))
        }
    }

    private fun setCntrlrPidSpTd(td: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidSpTd(td))
        }
    }

    private fun setCntrlrPidSpTi(ti: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidSpTi(ti))
        }
    }

    private fun setCntrlrPidSpStable(stable: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidSpStable(stable))
        }
    }

    private fun setCntrlrPidSpCenter(center: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidSpCenter(center))
        }
    }

    private fun setCntrlrPidSpTau(tau: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidSpTau(tau))
        }
    }

    private fun setCntrlrPidSpTheta(theta: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrPidSpTheta(theta))
        }
    }

    private fun setCntrlrTime(time: Int) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlrTime(time))
        }
    }

    private fun setCntrlruMin(uMin: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlruMin(uMin))
        }
    }

    private fun setCntrlruMax(uMax: Double) {
        viewModelScope.launch {
            handleResult(settingsRepo.setCntrlruMax(uMax))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                setEffect {
                    WorkContract.Effect.Notification(
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
