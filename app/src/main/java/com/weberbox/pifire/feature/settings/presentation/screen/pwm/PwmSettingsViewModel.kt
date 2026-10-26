package com.weberbox.pifire.feature.settings.presentation.screen.pwm

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.PwmControl
import com.weberbox.pifire.core.model.settings.PwmProfile
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.feature.settings.presentation.contract.PwmContract
import com.weberbox.pifire.feature.settings.presentation.util.sortRangesByTemp
import com.weberbox.pifire.feature.settings.presentation.util.updateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PwmSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
) : BaseViewModel<PwmContract.Event, PwmContract.State, PwmContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = PwmContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
    )

    override fun handleEvents(event: PwmContract.Event) {
        toggleLoading(true)
        when (event) {
            is PwmContract.Event.DeletePWMControlItem -> deletePWMControlItem(event.controlItems)

            is PwmContract.Event.SetPWMControlDefault -> setPWMControlDefault(event.enabled)

            is PwmContract.Event.SetPWMFanFrequency -> setPWMFanFrequency(event.frequency)

            is PwmContract.Event.SetPWMMaxDutyCycle -> setPWMMaxDutyCycle(event.dutyCycle)

            is PwmContract.Event.SetPWMMinDutyCycle -> setPWMMinDutyCycle(event.dutyCycle)

            is PwmContract.Event.SetPWMTempUpdateTime -> setPWMTempUpdateTime(event.time)

            is PwmContract.Event.SetPWMControlItem -> setPWMControlItem(
                event.index,
                event.temp,
                event.dutyCycle,
                event.controlItems,
            )
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

    private fun setPWMControlDefault(enabled: Boolean) {
        viewModelScope.launch {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMControlDefault(enabled))
        }
    }

    private fun setPWMTempUpdateTime(time: Int) {
        viewModelScope.launch {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMTempUpdateTime(time))
        }
    }

    private fun setPWMFanFrequency(frequency: Int) {
        viewModelScope.launch {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMFanFrequency(frequency))
        }
    }

    private fun setPWMMinDutyCycle(dutyCycle: Int) {
        viewModelScope.launch {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMMinDutyCycle(dutyCycle))
        }
    }

    private fun setPWMMaxDutyCycle(dutyCycle: Int) {
        viewModelScope.launch {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMMaxDutyCycle(dutyCycle))
        }
    }

    private fun setPWMControlItem(
        index: Int,
        temp: Int,
        dutyCycle: Int,
        controlItems: List<PwmControl>,
    ) {
        val (temps, profiles) = updateRange(
            index = index,
            temp = temp,
            profile = PwmProfile(dutyCycle = dutyCycle),
            rowTemps = controlItems.map { item -> item.temp },
            rowProfiles = controlItems.map { item -> PwmProfile(dutyCycle = item.dutyCycle) },
        )
        viewModelScope.launch {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMControl(temps, profiles))
        }
    }

    private fun deletePWMControlItem(controlItems: List<PwmControl>) {
        val (temps, profiles) = sortRangesByTemp(
            temps = controlItems.map { item -> item.temp }.dropLast(2),
            profiles = controlItems.map { item -> PwmProfile(dutyCycle = item.dutyCycle) }.dropLast(1),
        )

        viewModelScope.launch {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMControl(temps, profiles))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                setEffect {
                    PwmContract.Effect.Notification(
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
