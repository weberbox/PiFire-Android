package com.weberbox.pifire.feature.settings.data.model.remote

import com.weberbox.pifire.core.network.IntOrDoubleSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeepWarm(
    @SerialName("temp")
    @Serializable(with = IntOrDoubleSerializer::class)
    val temp: Int? = null,

    @SerialName("s_plus")
    val enabled: Boolean? = null
)
