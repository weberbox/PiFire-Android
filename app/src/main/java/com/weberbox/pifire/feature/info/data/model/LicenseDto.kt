package com.weberbox.pifire.feature.info.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LicenseDto(
    val project: String? = null,
    val license: String? = null
)