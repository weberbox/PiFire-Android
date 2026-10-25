package com.weberbox.pifire.feature.setup.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class VersionsData(
    val version: String = "1.0.0",
    val build: String = "0"
)
