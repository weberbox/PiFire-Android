package com.weberbox.pifire.setup.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VersionsDto(
    val version: String? = null,
    val build: String? = null
)
