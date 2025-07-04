package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pwm(
    @SerialName("pwm_control")
    val pwmControl: Boolean? = null,

    @SerialName("update_time")
    val updateTime: Int? = null,

    @SerialName("frequency")
    val frequency: Int? = null,

    @SerialName("min_duty_cycle")
    val minDutyCycle: Int? = null,

    @SerialName("max_duty_cycle")
    val maxDutyCycle: Int? = null,

    @SerialName("temp_range_list")
    val tempRangeList: List<Int>? = null,

    @SerialName("profiles")
    val profiles: List<Profile>? = null
) {

    @Serializable
    data class Profile(
        @SerialName("duty_cycle")
        val dutyCycle: Int? = null
    )
}
