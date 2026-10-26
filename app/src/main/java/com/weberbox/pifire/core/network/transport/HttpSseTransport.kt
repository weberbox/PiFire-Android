package com.weberbox.pifire.core.network.transport

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.headers.HeadersManager
import com.weberbox.pifire.core.network.parsePostResponse
import com.weberbox.pifire.core.network.toNetworkError
import com.weberbox.pifire.core.session.SessionStateHolder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val CONNECT_TIMEOUT = 20.seconds
private val REQUEST_TIMEOUT = 15.seconds

// PiFire pings every 15 s, so a silent stream for this long is dead.
private val STREAM_READ_TIMEOUT = 45.seconds
private val RECONNECT_DELAY_MIN = 2.seconds
private val RECONNECT_DELAY_MAX = 30.seconds
private const val EVENT_BUFFER_SIZE = 64
private const val API_PATH = "/mobile/v2"

/** Mobile API v2 (docs/V2_API.md): reads and writes over HTTP, live updates over Server-Sent Events. */
@Singleton
class HttpSseTransport @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val headersManager: HeadersManager,
    private val json: Json,
) : PiFireTransport {

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT.toJavaDuration())
        .readTimeout(REQUEST_TIMEOUT.toJavaDuration())
        .build()
    private val streamClient = client.newBuilder()
        .readTimeout(STREAM_READ_TIMEOUT.toJavaDuration())
        .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private class Connection(
        val baseUrl: String,
        val headers: Headers,
    )

    // Replaced on every connect; callbacks from an older stream compare against it and stop.
    @Volatile
    private var connection: Connection? = null
    private var eventSource: EventSource? = null
    private var reconnectJob: Job? = null
    private var reconnectDelay = RECONNECT_DELAY_MIN
    private val connected = MutableStateFlow(false)
    private val events = MutableSharedFlow<Pair<String, String>>(
        extraBufferCapacity = EVENT_BUFFER_SIZE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** Whether [server] offers API v2: `GET /mobile/v2/hello`, run before choosing a transport. */
    suspend fun probe(server: Server): ProbeResult {
        val target = Connection(baseUrl = baseUrl(server.address), headers = headers(server))
        val request = Request.Builder()
            .url("${target.baseUrl}/hello")
            .headers(target.headers)
            .build()
        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).await().use { response ->
                    when {
                        response.isSuccessful -> response.body.string().toAvailable()
                        response.code == 404 -> ProbeResult.NotAvailable
                        response.code == 401 -> ProbeResult.Failed(DataError.Network.AUTHENTICATION)
                        else -> ProbeResult.Failed(DataError.Network.SERVER_ERROR)
                    }
                }
            } catch (e: IOException) {
                currentCoroutineContext().ensureActive()
                ProbeResult.Failed(e.toNetworkError())
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Invalid server address")
                ProbeResult.Failed(DataError.Network.UNKNOWN_HOST)
            }
        }
    }

    override suspend fun connect(
        server: Server,
        disconnectOnCancellation: Boolean,
    ): Boolean {
        disconnect()
        val target = try {
            Connection(baseUrl = baseUrl(server.address), headers = headers(server))
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Invalid server address")
            return false
        }
        connection = target
        reconnectDelay = RECONNECT_DELAY_MIN
        val opened = CompletableDeferred<Boolean>()
        openStream(target = target, opened = opened)
        val isOpen = try {
            withTimeoutOrNull(CONNECT_TIMEOUT) { opened.await() } ?: false
        } catch (e: Exception) {
            if (disconnectOnCancellation) disconnect()
            throw e
        }
        if (!isOpen) disconnect()
        return isOpen
    }

    override fun disconnect() {
        connection = null
        reconnectJob?.cancel()
        reconnectJob = null
        eventSource?.cancel()
        eventSource = null
        setConnected(false)
    }

    override fun isConnected(): Boolean = connected.value

    override suspend fun get(vararg args: Any): Result<String, DataError> {
        val name = DATA_NAMES[args.firstOrNull()] ?: return Result.Error(DataError.Network.SERVER_ERROR)
        return call(method = "GET", path = "/data/$name")
    }

    override suspend fun postResult(vararg args: Any): Result<String, DataError> {
        val action = (args.getOrNull(0) as? String)?.removeSuffix("_action")
        val type = args.getOrNull(1) as? String
        if (action == null || type == null) return Result.Error(DataError.Network.SERVER_ERROR)
        return call(method = "POST", path = "/action/$action/$type", body = args.getOrNull(2) as? String ?: "{}")
    }

    override suspend fun post(vararg args: Any): Result<Unit, DataError> = when (val result = postResult(*args)) {
        is Result.Error -> Result.Error(result.error)
        is Result.Success -> parsePostResponse(result.data, json)
    }

    override fun listen(event: String): Flow<Result<Array<out Any?>, DataError>> = flow {
        val name = EVENT_NAMES[event]
        if (name == null || !connected.value) emit(Result.Error(DataError.Network.SOCKET_ERROR))
        if (name == null) return@flow
        emitAll(
            events
                .filter { (type, _) -> type == name }
                .map { (_, data) -> Result.Success(arrayOf(data)) },
        )
    }

    /** Alerts from the stream (docs/V2_API.md section 5): the `alert` event's JSON, while connected over v2. */
    val alerts: Flow<String> = events
        .filter { (type, _) -> type == ALERT_EVENT }
        .map { (_, data) -> data }

    /**
     * A v2-only call (e.g. push devices), returning the envelope's `data`. PiFire's error message
     * comes back as [DataError.Server], like the Socket.IO actions.
     */
    suspend fun request(
        method: String,
        path: String,
        body: String? = null,
    ): Result<JsonElement?, DataError> {
        val target = connection ?: return Result.Error(DataError.Network.SOCKET_ERROR)
        return execute(target = target, method = method, path = path, body = body) { envelope ->
            if (envelope.ok) {
                Result.Success(envelope.data)
            } else {
                Result.Error(DataError.Server(envelope.message ?: envelope.code ?: ""))
            }
        }
    }

    private suspend fun call(
        method: String,
        path: String,
        body: String? = null,
    ): Result<String, DataError> {
        val target = connection ?: return Result.Error(DataError.Network.SOCKET_ERROR)
        return execute(target = target, method = method, path = path, body = body) { envelope ->
            Result.Success(envelope.toLegacyResponse())
        }
    }

    private suspend fun <T> execute(
        target: Connection,
        method: String,
        path: String,
        body: String?,
        onEnvelope: (V2Envelope) -> Result<T, DataError>,
    ): Result<T, DataError> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${target.baseUrl}$path")
            .headers(target.headers)
            .method(method, body?.toRequestBody(JSON_MEDIA_TYPE))
            .build()
        try {
            client.newCall(request).await().use { response ->
                if (response.code == 401) return@use Result.Error(DataError.Network.AUTHENTICATION)
                // PiFire's own failures (4xx/422) carry an envelope too; anything else is the proxy or a crash.
                val envelope = response.body.string().let { text ->
                    try {
                        json.decodeFromString(V2Envelope.serializer(), text)
                    } catch (_: IllegalArgumentException) {
                        null
                    }
                }
                envelope?.let(onEnvelope) ?: Result.Error(DataError.Network.SERVER_ERROR)
            }
        } catch (e: IOException) {
            currentCoroutineContext().ensureActive()
            Result.Error(e.toNetworkError())
        }
    }

    private fun openStream(
        target: Connection,
        opened: CompletableDeferred<Boolean>?,
    ) {
        val request = Request.Builder()
            .url("${target.baseUrl}/stream")
            .headers(target.headers)
            .header("Accept", "text/event-stream")
            .build()
        eventSource = EventSources.createFactory(streamClient).newEventSource(
            request = request,
            listener = StreamListener(target = target, opened = opened),
        )
    }

    private inner class StreamListener(
        private val target: Connection,
        private val opened: CompletableDeferred<Boolean>?,
    ) : EventSourceListener() {
        private var wasOpen = false

        override fun onOpen(eventSource: EventSource, response: Response) {
            if (connection !== target) return
            wasOpen = true
            this@HttpSseTransport.reconnectDelay = RECONNECT_DELAY_MIN
            setConnected(true)
            opened?.complete(true)
        }

        override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
            if (connection !== target || type == null) return
            events.tryEmit(type to data)
        }

        override fun onClosed(eventSource: EventSource) = streamEnded(response = null, error = null)

        override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) = streamEnded(response = response, error = t)

        private fun streamEnded(response: Response?, error: Throwable?) {
            if (connection !== target) return
            Timber.d("Stream ended: %s %s", response?.code, error?.javaClass?.simpleName)
            setConnected(false)
            opened?.complete(false)
            // A first connect that fails reports false to the caller; a dropped stream comes back on its own.
            if (wasOpen || opened == null) scheduleReconnect(target)
        }
    }

    /** One retry per ended stream, backing off up to [RECONNECT_DELAY_MAX] until a stream opens. */
    private fun scheduleReconnect(target: Connection) {
        val wait = reconnectDelay
        reconnectDelay = (reconnectDelay * 2).coerceAtMost(RECONNECT_DELAY_MAX)
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            delay(wait)
            if (connection === target) openStream(target = target, opened = null)
        }
    }

    private fun setConnected(isConnected: Boolean) {
        connected.value = isConnected
        sessionStateHolder.tryEmitConnectedState(isConnected)
    }

    private suspend fun headers(server: Server): Headers = Headers.Builder().apply {
        headersManager.buildServerHeadersList(server).forEach { (name, values) ->
            values.forEach { value -> add(name, value) }
        }
    }.build()

    private fun baseUrl(address: String): String {
        val trimmed = address.trimEnd('/')
        require(trimmed.startsWith("http://") || trimmed.startsWith("https://")) { "Address needs a scheme" }
        return "$trimmed$API_PATH"
    }

    private fun V2Envelope.toLegacyResponse(): String = buildJsonObject {
        put("data", data ?: JsonNull)
        put("result", if (ok) "OK" else "Error")
        put("message", message ?: code)
    }.toString()

    private fun String.toAvailable(): ProbeResult {
        val hello = try {
            json.decodeFromString(V2Envelope.serializer(), this).data?.let { data ->
                json.decodeFromJsonElement(HelloDto.serializer(), data)
            }
        } catch (_: IllegalArgumentException) {
            null
        }
        return ProbeResult.Available(
            serverVersion = hello?.serverVersion?.contentOrNull.orEmpty(),
            serverBuild = hello?.serverBuild?.contentOrNull.orEmpty(),
        )
    }

    sealed interface ProbeResult {
        data class Available(
            val serverVersion: String,
            val serverBuild: String,
        ) : ProbeResult

        data object NotAvailable : ProbeResult
        data class Failed(val error: DataError) : ProbeResult
    }

    @Serializable
    private data class HelloDto(
        // PiFire sends the build as a number; read either.
        @SerialName("server_version") val serverVersion: JsonPrimitive? = null,
        @SerialName("server_build") val serverBuild: JsonPrimitive? = null,
    )

    @Serializable
    private data class V2Envelope(
        @SerialName("ok") val ok: Boolean = false,
        @SerialName("code") val code: String? = null,
        @SerialName("message") val message: String? = null,
        @SerialName("data") val data: JsonElement? = null,
    )

    private companion object {
        val JSON_MEDIA_TYPE = "application/json".toMediaType()
        const val ALERT_EVENT = "alert"

        // Socket.IO action -> /data/<name>
        val DATA_NAMES = mapOf(
            ServerConstants.GA_SETTINGS_DATA to "settings",
            ServerConstants.GA_DASH_DATA to "dash",
            ServerConstants.GA_PELLETS_DATA to "pellets",
            ServerConstants.GA_EVENTS_DATA to "events",
            ServerConstants.GA_INFO_DATA to "info",
            ServerConstants.GA_MANUAL_DATA to "manual",
            ServerConstants.GA_RECIPE_DATA to "recipes",
        )

        // Socket.IO event -> stream event
        val EVENT_NAMES = mapOf(
            ServerConstants.LISTEN_DASH_DATA to "dash",
            ServerConstants.LISTEN_PELLET_DATA to "pellets",
            ServerConstants.LISTEN_EVENTS_DATA to "events",
            ServerConstants.LISTEN_SETTINGS_DATA to "settings",
        )
    }
}

/** Runs the call on OkHttp's dispatcher; cancelling the coroutine cancels the call. */
private suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation { cancel() }
    enqueue(
        object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response) { _, resource, _ -> resource.close() }
            }

            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        },
    )
}
