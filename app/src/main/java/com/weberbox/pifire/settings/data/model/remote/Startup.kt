package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Startup(
    @SerialName("duration")
    val duration: Int? = null,

    @SerialName("prime_on_startup")
    val primeOnStartup: Int? = null,

    @SerialName("startup_exit_temp")
    val startExitTemp: Int? = null,

    @SerialName("start_to_mode")
    val startToMode: StartToMode? = null,

    @SerialName("smartstart")
    val smartStart: SmartStart? = null
) {

    @Serializable
    data class StartToMode(
        @SerialName("after_startup_mode")
        val afterStartUpMode: String? = null,

        @SerialName("primary_setpoint")
        val primarySetPoint: Int? = null
    )

    @Serializable
    data class SmartStart(
        @SerialName("enabled")
        val enabled: Boolean? = null,

        @SerialName("exit_temp")
        val exitTemp: Int? = null,

        @SerialName("profiles")
        val profiles: List<SSProfile>? = null,

        @SerialName("temp_range_list")
        val tempRangeList: List<Int>? = null
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
