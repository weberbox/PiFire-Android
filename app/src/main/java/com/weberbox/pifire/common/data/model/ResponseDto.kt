package com.weberbox.pifire.common.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ResponseDto(
    val result: String? = null,
    val message: String? = null,
    val data: JsonObject? = null
)