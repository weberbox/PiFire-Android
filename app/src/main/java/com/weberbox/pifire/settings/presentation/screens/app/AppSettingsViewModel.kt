package com.weberbox.pifire.settings.presentation.screens.app

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.Analytics
import com.weberbox.pifire.common.domain.AnalyticsEvent
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
    private val prefs: Prefs,
    private val analytics: Analytics,
) : BaseViewModel<AppContract.Event, AppContract.State, AppContract.Effect>() {

    init {
        collectPrefsFlow()
    }

    override fun setInitialState() = AppContract.State(
        appTheme = AppTheme.System,
        dynamicColorEnabled = false,
        keepScreenOn = false,
        showBottomBar = true,
        biometricsEnabled = false,
        eventsAmount = 20,
        incrementTemps = true,
        sentryEnabled = true,
        sentryDebugEnabled = false,
        userEmail = "",
        isInitialLoading = true,
        isDataError = false
    )

    override fun handleEvents(event: AppContract.Event) {
        when (event) {
            is AppContract.Event.UpdateAppTheme -> {
                prefs.set(Pref.appTheme, event.theme)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "appThemeChange",
                        AnalyticsEvent.Param.State.key to event.theme.name
                    )
                )
            }

            is AppContract.Event.DynamicColorEnabled -> {
                prefs.set(Pref.dynamicColor, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "dynamicColorChange",
                        AnalyticsEvent.Param.State.key to event.enabled
                    )
                )
            }

            is AppContract.Event.KeepScreenOn -> {
                prefs.set(Pref.keepScreenOn, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "keepScreenOnChange",
                        AnalyticsEvent.Param.State.key to event.enabled
                    )
                )
            }

            is AppContract.Event.ShowBottomBar -> {
                prefs.set(Pref.showBottomBar, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "showBottomBarChange",
                        AnalyticsEvent.Param.State.key to event.enabled
                    )
                )
            }

            is AppContract.Event.BiometricsEnabled -> {
                prefs.set(Pref.biometricSettingsPrompt, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "settingsBiometrics",
                        AnalyticsEvent.Param.State.key to event.enabled
                    )
                )
            }

            is AppContract.Event.SetEventsAmount -> {
                prefs.set(Pref.eventsAmount, event.amount)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "eventsAmount",
                        AnalyticsEvent.Param.State.key to event.amount
                    )
                )
            }

            is AppContract.Event.IncrementTemps -> {
                prefs.set(Pref.incrementTemps, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "incrementTemps",
                        AnalyticsEvent.Param.State.key to event.enabled
                    )
                )
            }

            is AppContract.Event.SentryEnabled -> {
                prefs.set(Pref.sentryEnabled, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "sentryEnabled",
                        AnalyticsEvent.Param.State.key to event.enabled
                    )
                )
            }

            is AppContract.Event.SentryDebugEnabled ->
                prefs.set(Pref.sentryDebugEnabled, event.enabled)

            is AppContract.Event.UpdateUserEmail ->
                prefs.set(Pref.sentryUserEmail, event.email)
        }
    }

    private fun collectPrefsFlow() {
        collectAndUpdateState(Pref.appTheme) { copy(appTheme = it, isInitialLoading = false) }
        collectAndUpdateState(Pref.dynamicColor) { copy(dynamicColorEnabled = it) }
        collectAndUpdateState(Pref.keepScreenOn) { copy(keepScreenOn = it) }
        collectAndUpdateState(Pref.showBottomBar) { copy(showBottomBar = it) }
        collectAndUpdateState(Pref.biometricSettingsPrompt) { copy(biometricsEnabled = it) }
        collectAndUpdateState(Pref.eventsAmount) { copy(eventsAmount = it) }
        collectAndUpdateState(Pref.incrementTemps) { copy(incrementTemps = it) }
        collectAndUpdateState(Pref.sentryEnabled) { copy(sentryEnabled = it) }
        collectAndUpdateState(Pref.sentryDebugEnabled) { copy(sentryDebugEnabled = it) }
        collectAndUpdateState(Pref.sentryUserEmail) { copy(userEmail = it) }
    }

    private fun <T> collectAndUpdateState(
        pref: Pref<T>,
        update: AppContract.State.(T) -> AppContract.State
    ) {
        viewModelScope.launch {
            prefs.collectPrefsFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }
}