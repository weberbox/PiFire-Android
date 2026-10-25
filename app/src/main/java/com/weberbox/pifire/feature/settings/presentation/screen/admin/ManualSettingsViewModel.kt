package com.weberbox.pifire.feature.settings.presentation.screen.admin

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.feature.settings.data.model.ManualData
import com.weberbox.pifire.feature.settings.data.repo.ManualRepoImpl
import com.weberbox.pifire.feature.settings.data.repo.SettingsRepo
import com.weberbox.pifire.feature.settings.presentation.contract.ManualContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ManualSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val manualRepo: ManualRepoImpl
): BaseViewModel<ManualContract.Event, ManualContract.State, ManualContract.Effect>() {

    init {
        getManualData()
    }

    override fun setInitialState() = ManualContract.State(
        manualData = ManualData(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: ManualContract.Event) {
        toggleLoading(true)
        when (event) {
            is ManualContract.Event.SetAugerEnabled -> setManualAugerOutput(event.enabled)
            is ManualContract.Event.SetDutyCycle -> setManualPWMOutput(event.dutyCycle)
            is ManualContract.Event.SetFanEnabled -> setManualFanOutput(event.enabled)
            is ManualContract.Event.SetIgniterEnabled -> setManualIgniterOutput(event.enabled)
            is ManualContract.Event.SetManualMode -> setManualMode(event.enabled)
            is ManualContract.Event.SetPowerEnabled -> setManualPowerOutput(event.enabled)
        }
    }

    private fun getManualData() {
        viewModelScope.launch(Dispatchers.IO) {
            toggleLoading(true)
            val result = manualRepo.getManualData()
            withContext(Dispatchers.Main) {
                toggleLoading(false)
                when (result) {
                    is Result.Error<ManualData, DataError> -> {
                        setEffect {
                            ManualContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true
                            )
                        }
                    }

                    is Result.Success<ManualData, DataError> -> {
                        setState {
                            copy(
                                manualData = result.data,
                                isInitialLoading = false,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setManualMode(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setManualMode(enabled))
        }
    }

    private fun setManualFanOutput(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setManualFanOutput(enabled))
        }
    }

    private fun setManualAugerOutput(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setManualAugerOutput(enabled))
        }
    }

    private fun setManualIgniterOutput(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setManualIgniterOutput(enabled))
        }
    }

    private fun setManualPowerOutput(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setManualPowerOutput(enabled))
        }
    }

    private fun setManualPWMOutput(pwm: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setManualPWMOutput(pwm))
        }
    }


    private suspend fun handleResult(result: Result<Server, DataError>) {
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    toggleLoading(false)
                    setEffect {
                        ManualContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true
                        )
                    }
                }
            }

            is Result.Success -> {
                delay(1000.milliseconds)
                getManualData()
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