package com.weberbox.pifire.core.notification

import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushAlert
import com.weberbox.pifire.core.network.model.push.AlertDataDto
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Reads the FCM data map the relay sends (docs/PUSH.md 3.6). Returns null for a message that isn't
 * a relay alert. A `data` object that fails to decode only drops the details: the alert still shows,
 * with PiFire's fallback text or generic wording.
 */
fun parsePushAlert(
    data: Map<String, String>,
    json: Json,
): PushAlert? {
    val alertId = data["alert_id"]?.takeIf { it.isNotBlank() } ?: return null
    val serverUuid = data["server_uuid"].orEmpty()
    val details = data["data"]?.let { raw ->
        try {
            json.decodeFromString(
                deserializer = AlertDataDto.serializer(),
                string = raw,
            )
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "Unreadable push alert data")
            null
        }
    } ?: AlertDataDto()

    return PushAlert(
        alertId = alertId,
        type = AlertType.fromWireName(data["type"]),
        serverUuid = serverUuid,
        timeMillis = data["time"]?.toLongOrNull()?.times(1000),
        probeName = details.probeName?.takeIf { it.isNotBlank() } ?: details.probeLabel?.takeIf { it.isNotBlank() },
        target = details.target,
        limit = details.limit,
        isHighLimit = when (details.direction) {
            "high" -> true
            "low" -> false
            else -> null
        },
        units = details.units?.takeIf { it.isNotBlank() },
        level = details.level,
        temp = details.temp,
        maxTemp = details.maxTemp,
        startupTemp = details.startupTemp,
        message = details.message?.takeIf { it.isNotBlank() },
        fallbackTitle = data["fallback_title"]?.takeIf { it.isNotBlank() },
        fallbackBody = data["fallback_body"]?.takeIf { it.isNotBlank() },
    )
}
