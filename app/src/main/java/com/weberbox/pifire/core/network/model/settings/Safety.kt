package com.weberbox.pifire.core.network.model.settings

import com.weberbox.pifire.core.network.IntOrDoubleSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Safety(
    @SerialName("minstartuptemp")
    @Serializable(with = IntOrDoubleSerializer::class)
    val minStartupTemp: Int? = null,

    @SerialName("maxstartuptemp")
    @Serializable(with = IntOrDoubleSerializer::class)
    val maxStartupTemp: Int? = null,

    @SerialName("maxtemp")
    @Serializable(with = IntOrDoubleSerializer::class)
    val maxTemp: Int? = null,

    @SerialName("reigniteretries")
    val reigniteRetries: Int? = null,

    @SerialName("startup_check")
    val startupCheck: Boolean? = null,

    @SerialName("allow_manual_changes")
    val allowManualChanges: Boolean? = null,

    @SerialName("manual_override_time")
    val manualOverrideTime: Int? = null,
)
