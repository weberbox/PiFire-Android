package com.weberbox.pifire.core.network.model.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerInfo(
    @SerialName("uuid")
    val uuid: String? = null,
)
