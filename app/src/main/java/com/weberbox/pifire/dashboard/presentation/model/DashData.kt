package com.weberbox.pifire.dashboard.presentation.model

import androidx.compose.runtime.Immutable
import com.weberbox.pifire.common.domain.Uuid
import com.weberbox.pifire.settings.data.model.local.Setting
import kotlinx.serialization.Serializable

@Serializable
data class DashData(
    val dashMap: Map<Uuid, Dash> = emptyMap()
) {
    @Immutable
    @Serializable
    data class Dash(
        val uuid: String = "",
        val errors: List<String> = emptyList(),
        val warnings: List<String> = emptyList(),
        val status: String = "",
        val criticalError: Boolean = false,
        val grillName: String = "",
        val currentMode: String = RunningMode.Stop.name,
        val nextMode: String = "",
        val displayMode: String = RunningMode.Stop.name,
        val smokePlus: Boolean = false,
        val pwmControl: Boolean = false,
        val pMode: Int = 2,
        val hopperLevel: Int = 0,
        val startupTimestamp: Int = 0,
        val modeStartTime: Int = 0,
        val lidOpenDetectEnabled: Boolean = Setting.lidOpenDetectEnabled.value,
        val lidOpenDetected: Boolean = false,
        val lidOpenEndTime: Int = 0,
        val startDuration: Int = Setting.startupDuration.value,
        val shutdownDuration: Int = Setting.shutdownDuration.value,
        val primeDuration: Int = 0,
        val primeAmount: Int = 0,
        val timeRemaining: Int = 0,
        val tempUnits: String = Setting.tempUnits.value,
        val hasDcFan: Boolean = Setting.dcFan.value,
        val hasDistanceSensor: Boolean = Setting.hasDistanceSensor.value,
        val startupCheck: Boolean = Setting.safetyStartupCheck.value,
        val startToHoldPrompt: Boolean = Setting.startToHoldPrompt.value,
        val startupGotoTemp: Int = Setting.startupGotoTemp.value,
        val startupGotoMode: String = Setting.startupGotoMode.value,
        val allowManualOutputs: Boolean = Setting.safetyAllowManualChanges.value,
        val timer: Timer = Timer(),
        val outputs: Outputs = Outputs(),
        val recipeStatus: RecipeStatus = RecipeStatus(),
        val foodProbes: List<Probe> = listOf(
            Probe(title = "Probe 1", label = "probe1"),
            Probe(title = "Probe 2", label = "probe2"),
            Probe(title = "Probe 3", label = "probe3")
        ),
        val primaryProbe: Probe = Probe(title = "Grill", label = "grill", maxTemp = 600)
    ) {
        @Serializable
        data class Timer(
            val start: Int = 0,
            val paused: Int = 0,
            val end: Int = 0,
            val keepWarm: Boolean = false,
            val shutdown: Boolean = false,
            val timerActive: Boolean = false,
            val timerPaused: Boolean = false
        )

        @Serializable
        data class Outputs(
            val fan: Boolean = false,
            val auger: Boolean = false,
            val igniter: Boolean = false
        )

        @Serializable
        data class RecipeStatus(
            val recipeMode: Boolean = false,
            val filename: String = "",
            val mode: String = RunningMode.Startup.name,
            val paused: Boolean = false,
            val step: Int = 0,
        )

        @Serializable
        data class Probe(
            val probeType: ProbeType = ProbeType.Food,
            val title: String = "Probe",
            val label: String = "probe",
            val enabled: Boolean = true,
            val eta: Int = 0,
            val temp: Int = 0,
            val setTemp: Int = 0,
            val maxTemp: Int = 300,
            val target: Int = 0,
            val lowLimitTemp: Int = 0,
            val highLimitTemp: Int = 0,
            val lowLimitReq: Boolean = false,
            val highLimitReq: Boolean = false,
            val targetReq: Boolean = false,
            val highLimitShutdown: Boolean = false,
            val highLimitTriggered: Boolean = false,
            val lowLimitShutdown: Boolean = false,
            val lowLimitReignite: Boolean = false,
            val lowLimitTriggered: Boolean = false,
            val hasNotifications: Boolean = false,
            val targetShutdown: Boolean = false,
            val targetKeepWarm: Boolean = false,
            val status: Status = Status()
        ) {
            @Serializable
            data class Status(
                val batteryCharging: Boolean = false,
                val batteryPercentage: Int = 0,
                val batteryVoltage: Double = 0.0,
                val connected: Boolean = false,
                val error: Boolean = false,
                val wireless: Boolean = false
            )
        }
    }
}

