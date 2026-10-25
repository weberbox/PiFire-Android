package com.weberbox.pifire.feature.setup.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VersionsData(
    val version: String = "1.0.0",
    val build: String = "0"
)
