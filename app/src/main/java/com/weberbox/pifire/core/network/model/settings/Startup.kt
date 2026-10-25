package com.weberbox.pifire.core.network.model.settings

import com.weberbox.pifire.core.network.IntOrDoubleListSerializer
import com.weberbox.pifire.core.network.IntOrDoubleSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Startup(
    @SerialName("duration")
    val duration: Int? = null,

    @SerialName("prime_on_startup")
    val primeOnStartup: Int? = null,

    @SerialName("startup_exit_temp")
    @Serializable(with = IntOrDoubleSerializer::class)
    val startExitTemp: Int? = null,

    @SerialName("start_to_mode")
    val startToMode: StartToMode? = null,

    @SerialName("smartstart")
    val smartStart: SmartStart? = null,

    @SerialName("pwm_duty_cycle")
    val pwmStartupDuty: Int? = null,
) {

    @Serializable
    data class StartToMode(
        @SerialName("after_startup_mode")
        val afterStartUpMode: String? = null,

        @SerialName("primary_setpoint")
        @Serializable(with = IntOrDoubleSerializer::class)
        val primarySetPoint: Int? = null,

        @SerialName("start_to_hold_prompt")
        val startToHoldPrompt: Boolean? = null,
    )

    @Serializable
    data class SmartStart(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("exit_temp")
        @Serializable(with = IntOrDoubleSerializer::class)
        val exitTemp: Int? = null,

        @SerialName("profiles")
        val profiles: List<SSProfile>? = null,

        @SerialName("temp_range_list")
        @Serializable(with = IntOrDoubleListSerializer::class)
        val tempRangeList: List<Int>? = null,
    ) {

        @Serializable
        data class SSProfile(
            @SerialName("startuptime")
            val startUpTime: Int? = null,

            @SerialName("augerontime")
            val augerOnTime: Int? = null,

            @SerialName("p_mode")
            val pMode: Int? = null,
        )
    }
}
