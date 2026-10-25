package com.weberbox.pifire.feature.settings.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class ProbeProfile(
    val id: String = "",
    val name: String = ""
)