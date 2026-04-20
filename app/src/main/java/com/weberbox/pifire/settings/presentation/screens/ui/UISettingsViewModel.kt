package com.weberbox.pifire.settings.presentation.screens.ui

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.UIContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UISettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : BaseViewModel<UIContract.Event, UIContract.State, UIContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = UIContract.State(
        grillName = "",
        etaCalculations = true,
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: UIContract.Event) {
        toggleLoading(true)
        when (event) {
            is UIContract.Event.SetGrillName -> setGrillName(event.name)
            is UIContract.Event.SetETACalculations -> setETACalculations(event.enabled)
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            settingsRepo.getCurrentServerFlow().collect { server ->
                setState {
                    copy(
                        grillName = server.settings.grillName,
                        isInitialLoading = false
                    )
                }
            }
        }
    }

    private fun setGrillName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setGrillName(name))
        }
    }

    private fun setETACalculations(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setETACalculations(enabled))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        UIContract.Effect.Notification(
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
                            grillName = result.data.settings.grillName,
                            etaCalculations = result.data.settings.etaCalculation,
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