package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Safety(
    @SerialName("minstartuptemp")
    val minStartupTemp: Int? = null,
    
    @SerialName("maxstartuptemp")
    val maxStartupTemp: Int? = null,

    @SerialName("maxtemp")
    val maxTemp: Int? = null,

    @SerialName("reigniteretries")
    val reigniteRetries: Int? = null,

    @SerialName("startup_check")
    val startupCheck: Boolean? = null,

    @SerialName("allow_manual_changes")
    val allowManualChanges: Boolean? = null,

    @SerialName("manual_override_time")
    val manualOverrideTime: Int? = null
)
