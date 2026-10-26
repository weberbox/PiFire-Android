package com.weberbox.pifire.feature.settings.presentation.screen.app

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.repo.PrefsRepo
import com.weberbox.pifire.core.repo.PushRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.model.AppTheme
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.settings.presentation.contract.AppContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val analytics: Analytics,
    private val pushRepo: PushRepo,
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
        isDataError = false,
    )

    override fun handleEvents(event: AppContract.Event) {
        when (event) {
            is AppContract.Event.UpdateAppTheme -> {
                prefsRepo.set(Pref.appTheme, event.theme)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "appThemeChange",
                        AnalyticsEvent.Param.State.key to event.theme.name,
                    ),
                )
            }

            is AppContract.Event.DynamicColorEnabled -> {
                prefsRepo.set(Pref.dynamicColor, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "dynamicColorChange",
                        AnalyticsEvent.Param.State.key to event.enabled,
                    ),
                )
            }

            is AppContract.Event.KeepScreenOn -> {
                prefsRepo.set(Pref.keepScreenOn, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "keepScreenOnChange",
                        AnalyticsEvent.Param.State.key to event.enabled,
                    ),
                )
            }

            is AppContract.Event.ShowBottomBar -> {
                prefsRepo.set(Pref.showBottomBar, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "showBottomBarChange",
                        AnalyticsEvent.Param.State.key to event.enabled,
                    ),
                )
            }

            is AppContract.Event.BiometricsEnabled -> {
                prefsRepo.set(Pref.biometricSettingsPrompt, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "settingsBiometrics",
                        AnalyticsEvent.Param.State.key to event.enabled,
                    ),
                )
            }

            is AppContract.Event.SetEventsAmount -> {
                prefsRepo.set(Pref.eventsAmount, event.amount)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "eventsAmount",
                        AnalyticsEvent.Param.State.key to event.amount,
                    ),
                )
            }

            is AppContract.Event.IncrementTemps -> {
                prefsRepo.set(Pref.incrementTemps, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "incrementTemps",
                        AnalyticsEvent.Param.State.key to event.enabled,
                    ),
                )
            }

            is AppContract.Event.SentryEnabled -> {
                prefsRepo.set(Pref.sentryEnabled, event.enabled)
                analytics.logEvent(
                    name = AnalyticsEvent.PrefsChange.name,
                    params = mapOf(
                        AnalyticsEvent.Param.PrefKey.key to "sentryEnabled",
                        AnalyticsEvent.Param.State.key to event.enabled,
                    ),
                )
            }

            is AppContract.Event.SentryDebugEnabled ->
                prefsRepo.set(Pref.sentryDebugEnabled, event.enabled)

            is AppContract.Event.UpdateUserEmail ->
                prefsRepo.set(Pref.sentryUserEmail, event.email)

            is AppContract.Event.SendTestPush -> sendTestPush()
        }
    }

    private fun sendTestPush() {
        viewModelScope.launch {
            val notification = when (val result = pushRepo.sendTestPush()) {
                is Result.Success -> AppContract.Effect.Notification(
                    text = UiText(resId = R.string.settings_dev_test_push_sent),
                    error = false,
                )

                is Result.Error -> AppContract.Effect.Notification(
                    text = result.error.asUiText(),
                    error = true,
                )
            }
            setEffect { notification }
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
        update: AppContract.State.(T) -> AppContract.State,
    ) {
        viewModelScope.launch {
            prefsRepo.prefFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }
}
