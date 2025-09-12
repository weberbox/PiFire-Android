package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotifyServices(
    @SerialName("apprise")
    val apprise: Apprise? = null,

    @SerialName("ifttt")
    val ifttt: Ifttt? = null,

    @SerialName("pushbullet")
    val pushbullet: PushBullet? = null,

    @SerialName("pushover")
    val pushover: Pushover? = null,

    @SerialName("onesignal")
    val onesignal: OneSignalPush? = null,

    @SerialName("influxdb")
    val influxDB: InfluxDB? = null,

    @SerialName("mqtt")
    val mqtt: Mqtt? = null,

    @SerialName("wled")
    val wled: WLed? = null
) {

    @Serializable
    data class Apprise(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("locations")
        val locations: List<String>? = null
    )

    @Serializable
    data class Ifttt(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("APIKey")
        val apiKey: String? = null
    )

    @Serializable
    data class PushBullet(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("APIKey")
        val apiKey: String? = null,

        @SerialName("PublicURL")
        val publicURL: String? = null
    )

    @Serializable
    data class Pushover(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("APIKey")
        val apiKey: String? = null,

        @SerialName("UserKeys")
        val userKeys: String? = null,

        @SerialName("PublicURL")
        val publicURL: String? = null
    )

    @Serializable
    data class OneSignalPush(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("uuid")
        val uuid: String? = null,

        @SerialName("app_id")
        val appID: String? = null,

        @SerialName("devices")
        val devices: Map<String, OneSignalDeviceInfo>? = null
    ) {

        @Serializable
        data class OneSignalDeviceInfo(
            @SerialName("device_name")
            val deviceName: String? = null,

            @SerialName("friendly_name")
            val friendlyName: String? = null,

            @SerialName("app_version")
            val appVersion: String? = null
        )
    }

    @Serializable
    data class InfluxDB(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("url")
        val url: String? = null,

        @SerialName("token")
        val token: String? = null,

        @SerialName("org")
        val org: String? = null,

        @SerialName("bucket")
        val bucket: String? = null
    )

    @Serializable
    data class Mqtt(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("broker")
        val broker: String? = null,

        @SerialName("homeassistant_autodiscovery_topic")
        val topic: String? = null,

        @SerialName("id")
        val id: String? = null,

        @SerialName("password")
        val password: String? = null,

        @SerialName("port")
        val port: Int? = null,

        @SerialName("update_sec")
        val updateSec: Int? = null,

        @SerialName("username")
        val username: String? = null
    )

    @Serializable
    data class WLed(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("device_address")
        val address: String? = null,

        @SerialName("notify_duration")
        val duration: Int? = null,

        @SerialName("mode_presets")
        val modePresets: ModePresets? = null,

        @SerialName("event_presets")
        val eventsPresets: EventsPresets? = null,

        @SerialName("use_profiles")
        val useProfiles: Boolean? = null,

        @SerialName("use_suggested_presets")
        val useSuggestedProfiles: Boolean? = null,

        @SerialName("suggested_config")
        val suggestedConfig: SuggestedConfig? = null,

        @SerialName("profile_numbers")
        val profileNumbers: ProfileNumbers? = null
    ) {

        @Serializable
        data class ModePresets(
            @SerialName("Stop")
            val stop: Int? = null,

            @SerialName("Startup")
            val startup: Int? = null,

            @SerialName("Reignite")
            val reignite: Int? = null,

            @SerialName("Smoke")
            val smoke: Int? = null,

            @SerialName("Hold")
            val hold: Int? = null,

            @SerialName("Shutdown")
            val shutdown: Int? = null,

            @SerialName("Prime")
            val prime: Int? = null
        )

        @Serializable
        data class EventsPresets(
            @SerialName("Temp_Achieved")
            val tempAchieved: Int? = null,

            @SerialName("Recipe_Next")
            val recipeNext: Int? = null,

            @SerialName("Grill_Error")
            val grillError: Int? = null,

            @SerialName("Pellet_Level_Low")
            val pelletLevel: Int? = null,

            @SerialName("Timer_Expired")
            val timerExpired: Int? = null
        )

        @Serializable
        data class SuggestedConfig(
            @SerialName("cooking_color")
            val cookingColor: String? = null,

            @SerialName("idle_brightness")
            val idleBrightness: Int? = null,

            @SerialName("led_count")
            val ledCount: Int? = null,

            @SerialName("night_mode")
            val nightMode: Boolean? = null,
        )

        @Serializable
        data class ProfileNumbers(
            @SerialName("booting")
            val booting: Int? = null,

            @SerialName("cooking")
            val cooking: Int? = null,

            @SerialName("cooldown")
            val cooldown: Int? = null,

            @SerialName("error_fault")
            val errorFault: Int? = null,

            @SerialName("idle")
            val idle: Int? = null,

            @SerialName("low_pellets")
            val lowPellets: Int? = null,

            @SerialName("night_mode")
            val nightMode: Int? = null,

            @SerialName("overshoot_alarm")
            val overshootAlarm: Int? = null,

            @SerialName("preheat")
            val preheat: Int? = null,

            @SerialName("probe_alarm")
            val probeAlarm: Int? = null,

            @SerialName("target_reached")
            val targetReached: Int? = null,

            @SerialName("timer_done")
            val timerDone: Int? = null
        )
    }
}
