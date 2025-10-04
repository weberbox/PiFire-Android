package com.weberbox.pifire.common.domain

sealed class AnalyticsEvent(val name: String) {
    object ScreenView : AnalyticsEvent("screen_view")
    object PrefsChange : AnalyticsEvent("prefs_change")
    object ButtonClick : AnalyticsEvent("button_click")
    object SignOut : AnalyticsEvent("sign_out")
    object UpdateAvailable : AnalyticsEvent("update_available")
    object PostponeUpdate : AnalyticsEvent("postpone_update")
    object UnsupportedServerMin : AnalyticsEvent("unsupported_server_min")
    object UnsupportedServerMax : AnalyticsEvent("unsupported_server_max")
    object UntestedServer : AnalyticsEvent("untested_server")
    object VoiceSearch : AnalyticsEvent("voice_search")
    object Language : AnalyticsEvent("app_language")
    object AppFlavor : AnalyticsEvent("app_flavor")

    sealed class Param(val key: String) {
        object ScreenName : Param("screen_name")
        object PrefKey : Param("pref_key")
        object ButtonAction : Param("button_action")
        object State : Param("state")
        object ServerVersion : Param("server_version")
        object ServerBuild : Param("server_build")
        object AppVersion : Param("app_version")
        object UpdateType : Param("update_type")
    }
}