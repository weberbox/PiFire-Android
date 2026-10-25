package com.weberbox.pifire.core.network.model.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Shutdown(
    @SerialName("shutdown_duration")
    val duration: Int? = null,
    
    @SerialName("auto_power_off")
    val autoPowerOff: Boolean? = null
)
