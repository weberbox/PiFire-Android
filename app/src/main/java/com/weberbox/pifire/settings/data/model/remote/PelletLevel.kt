package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PelletLevel(
    @SerialName("warning_enabled")
    val warningEnabled: Boolean? = null,

    @SerialName("warning_level")
    val warningLevel: Int? = null,

    @SerialName("warning_time")
    val warningTime: Int? = null,

    @SerialName("empty")
    val empty: Int? = null,

    @SerialName("full")
    val full: Int? = null
)
