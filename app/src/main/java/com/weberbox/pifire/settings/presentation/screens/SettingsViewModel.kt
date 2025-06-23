package com.weberbox.pifire.settings.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.SettingsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {

    init {
        getSettingsData(false)
    }

    override fun setInitialState() = SettingsContract.State(
        supportsDcFan = false,
        isInitialLoading = true,
        isRefreshing = false,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: SettingsContract.Event) {
        when (event) {
            SettingsContract.Event.Refresh -> getSettingsData(true)
        }
    }

    private fun getSettingsData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val result = settingsRepo.getSettings()) {
                is Result.Error -> {
                    if (forced) {
                        setEffect {
                            SettingsContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true
                            )
                        }
                    } else {
                        getCachedData()
                    }
                }
                is Result.Success -> {
                    setState {
                        copy(
                            supportsDcFan = result.data.settings.dcFan,
                            isInitialLoading = false,
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun getCachedData() {
        when (val result = settingsRepo.getCachedData()) {
            is Result.Error -> {
                setState {
                    copy(
                        supportsDcFan = false,
                        isInitialLoading = false,
                        isDataError = true
                    )
                }
            }

            is Result.Success -> {
                setState {
                    copy(
                        supportsDcFan = result.data.settings.dcFan,
                        isInitialLoading = false,
                        isRefreshing = false
                    )
                }
                setEffect {
                    SettingsContract.Effect.Notification(
                        text = UiText(R.string.not_connected_cached_results),
                        error = true
                    )
                }
            }
        }
    }
}