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
    val startupCheck: Boolean? = null
)
