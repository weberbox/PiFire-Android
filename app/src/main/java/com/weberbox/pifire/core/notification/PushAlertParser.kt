package com.weberbox.pifire.core.notification

import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushAlert
import com.weberbox.pifire.core.network.model.push.AlertDataDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
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

/**
 * Reads an alert from the v2 live stream: the relay's send body (docs/PUSH.md 3.6) without
 * `push_key`. Converted to the FCM data map so both paths share one parser and give the same
 * [PushAlert] (and `alert_id`, which drops whichever copy arrives second).
 */
fun parseStreamAlert(
    raw: String,
    json: Json,
): PushAlert? {
    val alert = try {
        json.parseToJsonElement(raw) as? JsonObject
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "Unreadable stream alert")
        null
    } ?: return null
    val fallback = alert["fallback"] as? JsonObject
    val data = buildMap {
        alert.text("alert_id")?.let { put("alert_id", it) }
        alert.text("type")?.let { put("type", it) }
        alert.text("server_uuid")?.let { put("server_uuid", it) }
        alert.text("time")?.let { put("time", it) }
        (alert["data"] as? JsonObject)?.let { put("data", it.toString()) }
        fallback?.text("title")?.let { put("fallback_title", it) }
        fallback?.text("body")?.let { put("fallback_body", it) }
    }
    return parsePushAlert(data = data, json = json)
}

private fun JsonObject.text(key: String): String? = (get(key) as? JsonPrimitive)?.contentOrNull
