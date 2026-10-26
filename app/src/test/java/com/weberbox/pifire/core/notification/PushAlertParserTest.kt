package com.weberbox.pifire.core.notification

import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushAlert
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PushAlertParserTest {

    // Same settings as the app's shared Json (JsonModule).
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }

    private val envelope = mapOf(
        "alert_id" to "alert-1",
        "type" to "probe_target",
        "server_uuid" to "server-1",
        "time" to "1761400000",
    )

    @Test
    fun `parses a full probe alert`() {
        val alert = parsePushAlert(
            data = envelope + mapOf(
                "data" to """{"probe_label":"Probe1","probe_name":"Brisket","target":203,"units":"F","added_later":1}""",
                "fallback_title" to "Brisket Target Achieved",
                "fallback_body" to "Brisket target of 203F achieved",
            ),
            json = json,
        )
        assertEquals(
            PushAlert(
                alertId = "alert-1",
                type = AlertType.PROBE_TARGET,
                serverUuid = "server-1",
                timeMillis = 1_761_400_000_000,
                probeName = "Brisket",
                target = 203.0,
                units = "F",
                fallbackTitle = "Brisket Target Achieved",
                fallbackBody = "Brisket target of 203F achieved",
            ),
            alert,
        )
    }

    @Test
    fun `falls back to the probe label and reads the limit direction`() {
        val high = parsePushAlert(
            data = envelope + ("data" to """{"probe_label":"Probe1","limit":107.5,"direction":"high"}"""),
            json = json,
        )
        val unknownDirection = parsePushAlert(
            data = envelope + ("data" to """{"probe_name":" ","probe_label":"Probe1","direction":"sideways"}"""),
            json = json,
        )
        assertEquals("Probe1", high?.probeName)
        assertEquals(107.5, high?.limit)
        assertEquals(true, high?.isHighLimit)
        assertEquals("Probe1", unknownDirection?.probeName)
        assertNull(unknownDirection?.isHighLimit)
    }

    @Test
    fun `keeps the alert when data is unreadable or a field has the wrong type`() {
        val broken = parsePushAlert(data = envelope + ("data" to "{not json"), json = json)
        val wrongType = parsePushAlert(data = envelope + ("data" to """{"target":"hot","units":"F"}"""), json = json)
        assertEquals("alert-1", broken?.alertId)
        assertNull(broken?.target)
        assertEquals("alert-1", wrongType?.alertId)
    }

    @Test
    fun `maps unknown types and bad times without failing`() {
        val alert = parsePushAlert(data = envelope + mapOf("type" to "smoke_signal", "time" to "soon"), json = json)
        assertEquals(AlertType.UNKNOWN, alert?.type)
        assertNull(alert?.timeMillis)
    }

    @Test
    fun `ignores messages that aren't relay alerts`() {
        assertNull(parsePushAlert(data = emptyMap(), json = json))
        assertNull(parsePushAlert(data = envelope - "alert_id", json = json))
    }

    @Test
    fun `every type round-trips through its wire name`() {
        AlertType.entries.filter { it != AlertType.UNKNOWN }.forEach { type ->
            assertEquals(type, AlertType.fromWireName(type.wireName))
        }
        assertEquals(AlertType.UNKNOWN, AlertType.fromWireName(""))
        assertEquals(AlertType.UNKNOWN, AlertType.fromWireName(null))
    }
}
