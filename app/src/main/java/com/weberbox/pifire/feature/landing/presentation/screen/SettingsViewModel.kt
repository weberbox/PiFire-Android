package com.weberbox.pifire.feature.landing.presentation.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.repo.PrefsRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.core.ui.util.getFormattedDate
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import com.weberbox.pifire.feature.landing.presentation.contract.SettingsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val analytics: Analytics,
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

            is SettingsContract.Event.AppInfoDialog ->
                handleAboutDialog()

            is SettingsContract.Event.AutoSelectEnabled ->
                handleAutoSelectEnabled(event.enabled)

            is SettingsContract.Event.BiometricsEnabled ->
                handleBiometricsEnabled(event.enabled)
        }
    }

    private fun handleAutoSelectEnabled(enabled: Boolean) {
        prefsRepo.set(Pref.landingAutoSelect, enabled)
        analytics.logEvent(
            name = AnalyticsEvent.PrefsChange.name,
            params = mapOf(
                AnalyticsEvent.Param.PrefKey.key to "autoSelectServer",
                AnalyticsEvent.Param.State.key to enabled
            )
        )
    }

    private fun handleBiometricsEnabled(enabled: Boolean) {
        prefsRepo.set(Pref.biometricServerPrompt, enabled)
        analytics.logEvent(
            name = AnalyticsEvent.PrefsChange.name,
            params = mapOf(
                AnalyticsEvent.Param.PrefKey.key to "serverBiometrics",
                AnalyticsEvent.Param.State.key to enabled
            )
        )
    }

    private fun handleAboutDialog() {
        viewModelScope.launch {
            DialogController.sendEvent(
                DialogEvent(
                    title = UiText(
                        R.string.dialog_app_info_title,
                    ),
                    message = UiText(
                        R.string.dialog_app_info_message,
                        uiTextArgsOf(
                            BuildConfig.VERSION_NAME,
                            String.format(Locale.US, BuildConfig.VERSION_CODE.toString()),
                            BuildConfig.BUILD_TYPE,
                            BuildConfig.FLAVOR,
                            getFormattedDate(BuildConfig.BUILD_TIME, "MM-dd-yy HH:mm")
                        )
                    ),
                    positiveAction = DialogAction(
                        buttonText = UiText(R.string.close),
                        action = { }
                    )
                )
            )
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
            prefsRepo.prefFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }
}