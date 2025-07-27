package com.weberbox.pifire.dashboard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DashDto(
    val uuid: String? = null,
    val errors: List<String>? = null,
    val warnings: List<String>? = null,
    val status: String? = null,
    val criticalError: Boolean? = null,
    val grillName: String? = null,
    val currentMode: String? = null,
    val nextMode: String? = null,
    val displayMode: String? = null,
    val smokePlus: Boolean? = null,
    val pwmControl: Boolean? = null,
    val pMode: Int? = null,
    val hopperLevel: Int? = null,
    val startupTimestamp: Int? = null,
    val modeStartTime: Int? = null,
    val lidOpenDetectEnabled: Boolean? = null,
    val lidOpenDetected: Boolean? = null,
    val lidOpenEndTime: Int? = null,
    val startDuration: Int? = null,
    val shutdownDuration: Int? = null,
    val primeDuration: Int? = null,
    val primeAmount: Int? = null,
    val tempUnits: String? = null,
    val hasDcFan: Boolean? = null,
    val hasDistanceSensor: Boolean? = null,
    val startupCheck: Boolean? = null,
    val allowManualOutputs: Boolean? = null,
    val timer: Timer? = null,
    val outputs: Outputs? = null,
    val recipeStatus: RecipeStatus? = null,
    val foodProbes: List<Probe>? = null,
    val primaryProbe: Probe? = null
) {
    @Serializable
    data class Timer(
        val start: Int? = null,
        val paused: Int? = null,
        val end: Int? = null,
        val keepWarm: Boolean? = null,
        val shutdown: Boolean? = null,
    )

    @Serializable
    data class Outputs(
        val fan: Boolean? = null,
        val auger: Boolean? = null,
        val igniter: Boolean? = null
    )

    @Serializable
    data class RecipeStatus(
        val recipeMode: Boolean? = null,
        val filename: String? = null,
        val mode: String? = null,
        val paused: Boolean? = null,
        val step: Int? = null,
    )

    @Serializable
    data class Probe(
        val title: String? = null,
        val label: String? = null,
        val eta: Int? = null,
        val temp: Int? = null,
        val setTemp: Int? = null,
        val maxTemp: Int? = null,
        val target: Int? = null,
        val lowLimitTemp: Int? = null,
        val highLimitTemp: Int? = null,
        val targetReq: Boolean? = null,
        val lowLimitReq: Boolean? = null,
        val highLimitReq: Boolean? = null,
        val highLimitShutdown: Boolean? = null,
        val highLimitTriggered: Boolean? = null,
        val lowLimitShutdown: Boolean? = null,
        val lowLimitReignite: Boolean? = null,
        val lowLimitTriggered: Boolean? = null,
        val hasNotifications: Boolean? = null,
        val targetShutdown: Boolean? = null,
        val targetKeepWarm: Boolean? = null,
        val status: Status? = null,
    ) {
        @Serializable
        data class Status(
            val batteryCharging: Boolean? = null,
            val batteryPercentage: Double? = null,
            val batteryVoltage: Double? = null,
            val connected: Boolean? = null,
            val error: Boolean? = null
        )
    }
}
