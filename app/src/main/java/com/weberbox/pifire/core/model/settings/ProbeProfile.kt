package com.weberbox.pifire.core.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class ProbeProfile(
    val id: String = "",
    val name: String = "",
)
