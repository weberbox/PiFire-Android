package com.weberbox.pifire.core.network.model.settings

import com.weberbox.pifire.core.network.IntOrDoubleSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmokePlus(
    @SerialName("enabled")
    val enabled: Boolean? = null,

    @SerialName("min_temp")
    @Serializable(with = IntOrDoubleSerializer::class)
    val minTemp: Int? = null,

    @SerialName("max_temp")
    @Serializable(with = IntOrDoubleSerializer::class)
    val maxTemp: Int? = null,

    @SerialName("on_time")
    val onTime: Int? = null,

    @SerialName("off_time")
    val offTime: Int? = null,

    @SerialName("duty_cycle")
    val dutyCycle: Int? = null,

    @SerialName("fan_ramp")
    val fanRamp: Boolean? = null
)
