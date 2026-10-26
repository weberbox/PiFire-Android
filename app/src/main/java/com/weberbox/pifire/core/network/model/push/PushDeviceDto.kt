package com.weberbox.pifire.core.network.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A phone registered for push on a PiFire server (`GET /mobile/v2/push/devices`). */
@Serializable
data class PushDeviceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("device_name") val deviceName: String? = null,
    @SerialName("app_version") val appVersion: String? = null,
    @SerialName("registered") val registered: Long? = null,
)

/** Body of `POST /mobile/v2/push/devices`. */
@Serializable
data class PushDeviceRegisterDto(
    @SerialName("push_key") val pushKey: String,
    @SerialName("relay_url") val relayUrl: String,
    @SerialName("device_name") val deviceName: String,
    @SerialName("app_version") val appVersion: String,
)
