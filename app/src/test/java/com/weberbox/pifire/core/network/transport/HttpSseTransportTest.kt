package com.weberbox.pifire.core.network.transport

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.headers.HeadersManager
import com.weberbox.pifire.core.session.SessionStateHolder
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class HttpSseTransportTest {

    private val server = MockWebServer()
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }
    private val headersManager = mockk<HeadersManager> {
        coEvery { buildServerHeadersList(any()) } returns mapOf("X-Extra" to listOf("1"))
    }
    private val transport = HttpSseTransport(
        sessionStateHolder = SessionStateHolder(),
        headersManager = headersManager,
        json = json,
    )
    private lateinit var pifire: Server

    @Before
    fun setUp() {
        server.start()
        pifire = Server(uuid = "server-1", address = server.url("/").toString())
    }

    @After
    fun tearDown() {
        transport.disconnect()
        server.close()
    }

    private fun respond(code: Int, body: String) {
        server.enqueue(MockResponse.Builder().code(code).body(body).build())
    }

    // The stream: one dash event, held open briefly so connect() sees it open.
    private fun stream() {
        server.enqueue(
            MockResponse.Builder()
                .addHeader("Content-Type", "text/event-stream")
                .body("retry: 5000\n\nevent: dash\ndata: {\"uuid\":\"server-1\"}\n\n")
                .build(),
        )
    }

    private fun connect() = runBlocking {
        stream()
        assertTrue(transport.connect(server = pifire, disconnectOnCancellation = false))
        server.takeRequest()
    }

    private fun JsonObject.text(key: String) = get(key)?.jsonPrimitive?.content

    @Test
    fun `probe reads hello, 404 and failures`() = runBlocking {
        respond(code = 200, body = """{"ok":true,"data":{"api_version":1,"server_version":"1.11.0","server_build":19}}""")
        respond(code = 404, body = "<html>Not Found</html>")
        respond(code = 401, body = "")

        assertEquals(HttpSseTransport.ProbeResult.Available(serverVersion = "1.11.0", serverBuild = "19"), transport.probe(pifire))
        assertEquals("/mobile/v2/hello", server.takeRequest().url.encodedPath)
        assertEquals(HttpSseTransport.ProbeResult.NotAvailable, transport.probe(pifire))
        assertEquals(HttpSseTransport.ProbeResult.Failed(DataError.Network.AUTHENTICATION), transport.probe(pifire))
    }

    @Test
    fun `connect opens the stream with the server's headers`() {
        val request = connect()
        assertEquals("/mobile/v2/stream", request.url.encodedPath)
        assertEquals("text/event-stream", request.headers["Accept"])
        assertEquals("1", request.headers["X-Extra"])
    }

    @Test
    fun `reads map to data names and come back in the Socket IO shape`() = runBlocking {
        connect()
        respond(code = 200, body = """{"ok":true,"data":{"uuid":"server-1","grillName":"Smoker"}}""")

        val result = transport.get(ServerConstants.GA_DASH_DATA)

        assertEquals("/mobile/v2/data/dash", server.takeRequest().url.encodedPath)
        val response = json.parseToJsonElement((result as Result.Success).data).jsonObject
        assertEquals("OK", response.text("result"))
        assertEquals("Smoker", response["data"]?.jsonObject?.text("grillName"))
    }

    @Test
    fun `recipes read maps to its own name`() = runBlocking {
        connect()
        respond(code = 200, body = """{"ok":true,"data":{}}""")
        transport.get(ServerConstants.GA_RECIPE_DATA, ServerConstants.GA_RECIPE_DETAILS)
        assertEquals("/mobile/v2/data/recipes", server.takeRequest().url.encodedPath)
    }

    @Test
    fun `writes post the JSON body to the action path`() = runBlocking {
        connect()
        respond(code = 200, body = """{"ok":true,"data":null}""")

        val result = transport.post(ServerConstants.PA_TIMER_ACTION, ServerConstants.PT_TIMER_START, """{"timer_action":{}}""")

        val request = server.takeRequest()
        assertEquals(Result.Success<Unit, DataError>(Unit), result)
        assertEquals("POST", request.method)
        assertEquals("/mobile/v2/action/timer/start_timer", request.url.encodedPath)
        assertEquals("""{"timer_action":{}}""", request.body?.utf8())
        assertTrue(request.headers["Content-Type"].orEmpty().startsWith("application/json"))
    }

    @Test
    fun `a write without a body sends an empty object`() = runBlocking {
        connect()
        respond(code = 200, body = """{"ok":true,"data":null}""")
        transport.post(ServerConstants.PA_ADMIN_ACTION, ServerConstants.PT_RESTART_CONTROL)
        assertEquals("{}", server.takeRequest().body?.utf8())
    }

    @Test
    fun `PiFire's error message comes through as before`() = runBlocking {
        connect()
        respond(code = 422, body = """{"ok":false,"code":"action_failed","message":"Error: Cannot delete current profile"}""")

        val result = transport.post(ServerConstants.PA_PELLETS_ACTION, ServerConstants.PT_DELETE_PROFILE, "{}")

        assertEquals(Result.Error<Unit, DataError>(DataError.Server("Error: Cannot delete current profile")), result)
    }

    @Test
    fun `401 is an authentication error and a non-JSON reply a server error`() = runBlocking {
        connect()
        respond(code = 401, body = "")
        respond(code = 502, body = "<html>Bad Gateway</html>")

        assertEquals(Result.Error<String, DataError>(DataError.Network.AUTHENTICATION), transport.get(ServerConstants.GA_DASH_DATA))
        assertEquals(Result.Error<String, DataError>(DataError.Network.SERVER_ERROR), transport.get(ServerConstants.GA_DASH_DATA))
    }

    @Test
    fun `stream events reach listeners under their Socket IO names`() = runBlocking {
        val event = async {
            withTimeout(10.seconds) {
                transport.listen(ServerConstants.LISTEN_DASH_DATA).filterIsInstance<Result.Success<Array<out Any?>, DataError>>().first()
            }
        }
        // Lets the listener subscribe before the stream's event arrives.
        delay(200)
        stream()
        transport.connect(server = pifire, disconnectOnCancellation = false)

        assertEquals("""{"uuid":"server-1"}""", event.await().data.first())
    }

    @Test
    fun `alert events reach the alerts flow`() = runBlocking {
        val alert = async { withTimeout(10.seconds) { transport.alerts.first() } }
        delay(200)
        server.enqueue(
            MockResponse.Builder()
                .addHeader("Content-Type", "text/event-stream")
                .body("event: alert\ndata: {\"alert_id\":\"a1\",\"type\":\"test\"}\n\n")
                .build(),
        )
        transport.connect(server = pifire, disconnectOnCancellation = false)

        assertEquals("""{"alert_id":"a1","type":"test"}""", alert.await())
    }

    @Test
    fun `v2-only requests return the envelope data`() = runBlocking {
        connect()
        respond(code = 200, body = """{"ok":true,"data":[{"id":"abc"}]}""")
        respond(code = 409, body = """{"ok":false,"code":"too_many_devices","message":"At most 20 devices can be registered"}""")

        val list = transport.request(method = "GET", path = "/push/devices")
        val full = transport.request(method = "POST", path = "/push/devices", body = "{}")

        assertEquals("abc", (list as Result.Success).data.toString().substringAfter("\"id\":\"").substringBefore("\""))
        assertEquals(Result.Error<Any?, DataError>(DataError.Server("At most 20 devices can be registered")), full)
    }
}
