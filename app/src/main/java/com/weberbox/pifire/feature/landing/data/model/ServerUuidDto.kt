package com.weberbox.pifire.feature.landing.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerUuidDto(
    val uuid: String? = null,
)
