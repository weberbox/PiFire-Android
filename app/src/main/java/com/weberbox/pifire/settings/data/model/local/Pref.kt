package com.weberbox.pifire.settings.data.model.local

import com.weberbox.pifire.common.presentation.model.AppTheme

data class Pref<T>(
    val key: String,
    val defaultValue: T,
    val isEncrypted: Boolean = false
) {
    companion object {
        val storedAppVersion = Pref("pref_stored_app_version", 0)
        val serverSetupSession = Pref("pref_server_setup_session", "")
        val headersSetupSession = Pref("pref_headers_setup_session", "", true)
        val landingAutoSelect = Pref("pref_landing_auto_select", false)
        val updatePostponeTime = Pref("pref_update_postpone_time", 0L)
        val keepScreenOn = Pref("pref_keep_screen_on", false)
        val eventsAmount = Pref("pref_event_amount", 20)
        val sentryEnabled = Pref("pref_sentry_enable", true)
        val sentryUserEmail = Pref("pref_sentry_user_email", "", true)
        val sentryDebugEnabled = Pref("pref_sentry_dev_enable", false)
        val incrementTemps = Pref("pref_increment_temps", true)
        val showBottomBar = Pref("pref_show_bottom_bar", true)
        val appTheme = Pref("pref_app_theme", AppTheme.System)
        val dynamicColor = Pref("pref_dynamic_color", false)
        val holdTempToolTip = Pref("pref_hold_temp_tooltip", true)
        val biometricSettingsPrompt = Pref("pref_settings_biometrics", false)
        val biometricServerPrompt = Pref("pref_server_biometrics", false)
    }
}
