package com.weberbox.pifire.core.network.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request and response bodies of the push relay (docs/PUSH.md 3.2). */
@Serializable
data class RelayRegisterDto(
    @SerialName("fcm_token") val fcmToken: String,
    @SerialName("push_key") val pushKey: String? = null,
)

@Serializable
data class RelaySendDto(
    @SerialName("push_key") val pushKey: String,
    @SerialName("alert_id") val alertId: String,
    @SerialName("type") val type: String,
    @SerialName("server_uuid") val serverUuid: String,
    @SerialName("time") val time: Long,
)

@Serializable
data class RelayResponseDto(
    @SerialName("ok") val ok: Boolean = false,
    @SerialName("error") val error: String? = null,
    @SerialName("push_key") val pushKey: String? = null,
)
