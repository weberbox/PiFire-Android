package com.weberbox.pifire.core.network.model.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Modules(
    @SerialName("grillplat")
    val grillplat: String? = null,

    @SerialName("display")
    val display: String? = null,

    @SerialName("dist")
    val dist: String? = null
)
