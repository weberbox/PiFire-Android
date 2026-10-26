package com.weberbox.pifire.core.notification

import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushAlert
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.transport.HttpSseTransport
import com.weberbox.pifire.core.session.SessionStateHolder
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class StreamAlertNotifierTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }
    private val alertNotifier = mockk<AlertNotifier>(relaxed = true)
    private val deduper = AlertDeduper()
    private val session = SessionStateHolder()
    private var consent = true
    private val oneSignalManager = mockk<OneSignalManager> {
        every { getUserConsent() } answers { consent }
    }
    private val notifier = StreamAlertNotifier(
        httpSseTransport = mockk<HttpSseTransport>(relaxed = true),
        alertNotifier = alertNotifier,
        alertDeduper = deduper,
        sessionStateHolder = session,
        oneSignalManager = { oneSignalManager },
        json = json,
    )

    private fun connectedTo(uuid: String = "server-1", pushEnabled: Boolean = true) {
        session.tryEmitSettingsData(
            Server(uuid = uuid, settings = Server.Settings(mobilePushEnabled = pushEnabled)),
        )
    }

    private val streamAlert = """
        {"alert_id":"alert-1","type":"probe_target","server_uuid":"server-1","time":1761400000,
         "data":{"probe_label":"Probe1","probe_name":"Brisket","target":203,"units":"F"},
         "fallback":{"title":"Brisket Target Achieved","body":"Brisket target of 203F achieved"}}
    """.trimIndent()

    @Test
    fun `shows a stream alert, parsed the same as the relay copy`() = runTest {
        connectedTo()
        val shown = slot<PushAlert>()

        notifier.handle(streamAlert)

        coVerify { alertNotifier.show(capture(shown)) }
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
            shown.captured,
        )
    }

    @Test
    fun `the relay copy is dropped after the stream showed it`() = runTest {
        connectedTo()
        notifier.handle(streamAlert)
        notifier.handle(streamAlert)

        coVerify(exactly = 1) { alertNotifier.show(any()) }
        // PushMessagingService asks the same deduper, so FCM's copy is dropped too.
        assertFalse(deduper.isNew("alert-1"))
    }

    @Test
    fun `follows PiFire, not the app's possibly stale copy of the App Push switch`() = runTest {
        // PiFire only streams alerts with the switch on; a stale "off" here must not drop them.
        connectedTo(pushEnabled = false)
        notifier.handle(streamAlert)
        coVerify(exactly = 1) { alertNotifier.show(any()) }
    }

    @Test
    fun `nothing shows without push consent`() = runTest {
        connectedTo()
        consent = false
        notifier.handle(streamAlert)
        coVerify(exactly = 0) { alertNotifier.show(any()) }
    }

    @Test
    fun `ignores alerts for another server and when not connected`() = runTest {
        notifier.handle(streamAlert)
        connectedTo(uuid = "server-2")
        notifier.handle(streamAlert)
        coVerify(exactly = 0) { alertNotifier.show(any()) }
    }

    @Test
    fun `ignores unreadable stream data`() = runTest {
        connectedTo()
        notifier.handle("{not json")
        notifier.handle("""["alert"]""")
        coVerify(exactly = 0) { alertNotifier.show(any()) }
    }
}
