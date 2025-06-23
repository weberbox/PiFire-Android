package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeepWarm(
    @SerialName("temp")
    val temp: Int? = null,

    @SerialName("s_plus")
    val enabled: Boolean? = null
)
