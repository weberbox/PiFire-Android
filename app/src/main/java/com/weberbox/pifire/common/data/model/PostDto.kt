package com.weberbox.pifire.common.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("onesignal_device")
    var oneSignalDevice: OneSignalDevice? = null,

    @SerialName("pellets_action")
    var pelletsDto: PelletsDto? = null,

    @SerialName("timer_action")
    var timerDto: TimerDto? = null,

    @SerialName("recipes_action")
    var recipeDto: RecipeDto? = null,

    @SerialName("control_action")
    var controlDto: ControlDto? = null,

    @SerialName("probes_action")
    var probesDto: ProbesDto? = null,

    @SerialName("notify_action")
    var notifyDto: NotifyDto? = null,
) {
    @Serializable
    data class RecipeDto(
        @SerialName("filename")
        var filename: String = String()
    )

    @Serializable
    data class TimerDto(
        @SerialName("hours_range")
        var hoursRange: Int? = null,

        @SerialName("minutes_range")
        var minutesRange: Int? = null,

        @SerialName("timer_shutdown")
        var timerShutdown: Boolean? = null,

        @SerialName("timer_keep_warm")
        var timerKeepWarm: Boolean? = null,
    )

    @Serializable
    data class PelletsDto (
        @SerialName("profile")
        var profile: String? = null,

        @SerialName("brand_name")
        var brandName: String? = null,

        @SerialName("wood_type")
        var woodType: String? = null,

        @SerialName("rating")
        var rating: Int? = null,

        @SerialName("comments")
        var comments: String? = null,

        @SerialName("add_and_load")
        var addAndLoad: Boolean? = null,

        @SerialName("delete_wood")
        var deleteWood: String? = null,

        @SerialName("new_wood")
        var newWood: String? = null,

        @SerialName("delete_brand")
        var deleteBrand: String? = null,

        @SerialName("new_brand")
        var newBrand: String? = null,

        @SerialName("log_item")
        var logDate: String? = null
    )

    @Serializable
    data class OneSignalDevice(
        @SerialName("onesignal_player_id")
        var oneSignalPlayerID: String? = null
    )

    @Serializable
    data class ControlDto(
        @SerialName("updated")
        var updated: Boolean? = null,

        @SerialName("mode")
        var currentMode: String? = null,

        @SerialName("recipe")
        var recipe: Recipe? = null,

        @SerialName("s_plus")
        var smokePlus: Boolean? = null,

        @SerialName("pwm_control")
        var pwmControl: Boolean? = null,

        @SerialName("settings_update")
        var settingsUpdate: Boolean? = null,

        @SerialName("lid_open_toggle")
        var lidOpenToggle: Boolean? = null,

        @SerialName("next_mode")
        var nextMode: String? = null,

        @SerialName("prime_amount")
        var primeAmount: Int? = null,

        @SerialName("primary_setpoint")
        var primarySetPoint: Int? = null
    ) {

        @Serializable
        data class Recipe(
            @SerialName("filename")
            var fileName: String? = null,

            @SerialName("start_step")
            var smartStep: Int? = null,

            @SerialName("step")
            var step: Int? = null,

            @SerialName("step_data")
            var stepData: StepData? = null
        )

        @Serializable
        data class StepData(
            @SerialName("hold_temp")
            var holdTemp: Int? = null,

            @SerialName("message")
            var message: String? = null,

            @SerialName("mode")
            var mode: String? = null,

            @SerialName("notify")
            var notify: Boolean? = null,

            @SerialName("pause")
            var pause: Boolean? = null,

            @SerialName("timer")
            var timer: Int? = null,

            @SerialName("triggered")
            var triggered: Boolean? = null,

            @SerialName("trigger_temps")
            var triggerTemps: Map<String, Double>? = null
        )
    }

    @Serializable
    data class ProbesDto(
        @SerialName("name")
        var name: String? = null,

        @SerialName("label")
        var label: String? = null,

        @SerialName("profile_id")
        var profileId: String? = null,

        @SerialName("enabled")
        var enabled: Boolean? = null
    )

    @Serializable
    data class NotifyDto(
        @SerialName("label")
        val label: String,

        @SerialName("target_temp")
        val targetTemp: Int? = null,

        @SerialName("low_limit_temp")
        val lowLimitTemp: Int? = null,

        @SerialName("high_limit_temp")
        val highLimitTemp: Int? = null,

        @SerialName("low_limit_req")
        val lowLimitReq: Boolean? = null,

        @SerialName("high_limit_req")
        val highLimitReq: Boolean? = null,

        @SerialName("target_req")
        val targetReq: Boolean? = null,

        @SerialName("high_limit_shutdown")
        val highLimitShutdown: Boolean? = null,

        @SerialName("low_limit_shutdown")
        val lowLimitShutdown: Boolean? = null,

        @SerialName("low_limit_reignite")
        val lowLimitReignite: Boolean? = null,

        @SerialName("target_shutdown")
        val targetShutdown: Boolean? = null,

        @SerialName("target_keep_warm")
        val targetKeepWarm: Boolean? = null
    )
}