package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Versions(
    @SerialName("server")
    val server: String? = null,

    @SerialName("cookfile")
    val cookFile: String? = null,

    @SerialName("recipe")
    val recipe: String? = null,

    @SerialName("build")
    val build: String? = null,
)
