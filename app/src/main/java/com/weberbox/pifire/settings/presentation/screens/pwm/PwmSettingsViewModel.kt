package com.weberbox.pifire.settings.presentation.screens.pwm

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.settings.data.model.remote.Pwm.Profile
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.PwmContract
import com.weberbox.pifire.settings.presentation.model.PwmControl
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PwmSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
): BaseViewModel<PwmContract.Event, PwmContract.State, PwmContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = PwmContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
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
                event.controlItems
            )
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

    private fun setPWMControlDefault(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMControlDefault(enabled))
        }
    }

    private fun setPWMTempUpdateTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMTempUpdateTime(time))
        }
    }

    private fun setPWMFanFrequency(frequency: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMFanFrequency(frequency))
        }
    }

    private fun setPWMMinDutyCycle(dutyCycle: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMMinDutyCycle(dutyCycle))
        }
    }

    private fun setPWMMaxDutyCycle(dutyCycle: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMMaxDutyCycle(dutyCycle))
        }
    }

    private fun setPWMControlItem(
        index: Int,
        temp: Int,
        dutyCycle: Int,
        controlItems: List<PwmControl>
    ) {
        val tempsList = controlItems.map { item ->
            item.temp
        }.toMutableList()
        val profilesList = controlItems.map { item ->
            Profile(item.dutyCycle)
        }.toMutableList()

        if (index != -1) {
            tempsList[index] = temp
            tempsList.removeAt(tempsList.lastIndex)
            profilesList[index] = Profile(dutyCycle)
        } else {
            tempsList[tempsList.lastIndex] = temp
            profilesList.add(Profile(dutyCycle))
        }
        val temps = tempsList.sorted()
        val profiles = profilesList.sortedBy { it.dutyCycle }.toMutableList()
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMControl(temps, profiles))
        }
    }

    private fun deletePWMControlItem(controlItems: List<PwmControl>) {
        val temps = controlItems.map { item ->
            item.temp
        }
            .dropLast(2)
            .sorted()
        val profiles = controlItems.map { item ->
            Profile(item.dutyCycle)
        }
            .dropLast(1)
            .sortedBy { it.dutyCycle }

        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            handleResult(settingsRepo.setPWMControl(temps, profiles))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        PwmContract.Effect.Notification(
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