package com.weberbox.pifire.settings.presentation.screens.name

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.NameContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NameSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
): BaseViewModel<NameContract.Event, NameContract.State, NameContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = NameContract.State(
        grillName = "",
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: NameContract.Event) {
        toggleLoading(true)
        when (event) {
            is NameContract.Event.SetGrillName -> setGrillName(event.name)
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

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        NameContract.Effect.Notification(
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