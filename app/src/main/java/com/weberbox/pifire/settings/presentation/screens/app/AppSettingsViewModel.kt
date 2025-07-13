package com.weberbox.pifire.settings.presentation.screens.app

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.presentation.contract.AppContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val prefs: Prefs
) : BaseViewModel<AppContract.Event, AppContract.State, AppContract.Effect>() {

    init {
        collectPrefsFlow()
    }

    override fun setInitialState() = AppContract.State(
        appTheme = AppTheme.System,
        userEmail = "",
        biometricsEnabled = false,
        isInitialLoading = true,
        isDataError = false
    )

    override fun handleEvents(event: AppContract.Event) {
        when (event) {
            is AppContract.Event.UpdateAppTheme ->
                prefs.set(Pref.appTheme, event.theme)

            is AppContract.Event.UpdateUserEmail ->
                prefs.set(Pref.sentryUserEmail, event.email)

            is AppContract.Event.BiometricsEnabled ->
                prefs.set(Pref.biometricSettingsPrompt, event.enabled)
        }
    }

    private fun collectPrefsFlow() {
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.appTheme).collect {
                setState {
                    copy(
                        appTheme = it,
                        isInitialLoading = false
                    )
                }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.sentryUserEmail).collect {
                setState {
                    copy(
                        userEmail = it,
                        isInitialLoading = false
                    )
                }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.biometricSettingsPrompt).collect {
                setState {
                    copy(
                        biometricsEnabled = it
                    )
                }
            }
        }
    }
}