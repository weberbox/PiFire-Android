package com.weberbox.pifire.landing.presentation.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.landing.presentation.contract.SettingsContract
import com.weberbox.pifire.settings.data.model.local.Pref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: Prefs,
    val savedStateHandle: SavedStateHandle
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {

    init {
        collectPrefsFlow()
    }

    override fun setInitialState() = SettingsContract.State(
        autSelectEnabled = false,
        biometricsEnabled = false,
        isInitialLoading = true,
        isDataError = false
    )

    override fun handleEvents(event: SettingsContract.Event) {
        when (event) {
            is SettingsContract.Event.Back ->
                setEffect { SettingsContract.Effect.Navigation.Back }

            is SettingsContract.Event.AutoSelectEnabled ->
                prefs.set(Pref.landingAutoSelect, event.enabled)

            is SettingsContract.Event.BiometricsEnabled ->
                prefs.set(Pref.biometricServerPrompt, event.enabled)
        }
    }

    private fun collectPrefsFlow() {
        collectAndUpdateState(Pref.landingAutoSelect) {
            copy(
                autSelectEnabled = it,
                isInitialLoading = false
            )
        }
        collectAndUpdateState(Pref.biometricServerPrompt) { copy(biometricsEnabled = it) }
    }

    private fun <T> collectAndUpdateState(
        pref: Pref<T>,
        update: SettingsContract.State.(T) -> SettingsContract.State
    ) {
        viewModelScope.launch {
            prefs.collectPrefsFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }
}