package com.weberbox.pifire.core.network.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The alert's `data` object, nested as a JSON string in the FCM message (docs/PUSH.md 3.9). */
@Serializable
data class AlertDataDto(
    @SerialName("probe_label") val probeLabel: String? = null,
    @SerialName("probe_name") val probeName: String? = null,
    @SerialName("target") val target: Double? = null,
    @SerialName("limit") val limit: Double? = null,
    @SerialName("direction") val direction: String? = null,
    @SerialName("units") val units: String? = null,
    @SerialName("level") val level: Double? = null,
    @SerialName("temp") val temp: Double? = null,
    @SerialName("max_temp") val maxTemp: Double? = null,
    @SerialName("startup_temp") val startupTemp: Double? = null,
    @SerialName("message") val message: String? = null,
)
