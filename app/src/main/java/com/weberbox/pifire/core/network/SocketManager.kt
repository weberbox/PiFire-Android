package com.weberbox.pifire.core.network

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.headers.HeadersManager
import com.weberbox.pifire.core.network.transport.PiFireTransport
import com.weberbox.pifire.core.session.SessionStateHolder
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private const val CONNECT_TIMEOUT_MS = 20_000L
private const val ACK_TIMEOUT_MS = 10_000L
private const val LISTEN_BUFFER_SIZE = 64
private const val SOCKET_IO_PATH = "/socket.io"
private const val DIAGNOSE_PEEK_BYTES = 512L

// Short, so a server that already timed out doesn't keep the user waiting much longer.
private val DIAGNOSE_TIMEOUT = 5.seconds

class SocketManager @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val headersManager: HeadersManager,
    private val json: Json,
) : PiFireTransport {
    // A StateFlow so listen() flows move to the new socket after connect.
    private val socket = MutableStateFlow<Socket?>(null)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val diagnoseClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(DIAGNOSE_TIMEOUT.toJavaDuration())
            .readTimeout(DIAGNOSE_TIMEOUT.toJavaDuration())
            .build()
    }

    private suspend fun createSocket(server: Server): Socket? {
        Timber.i("Creating Socket connection")

        return try {
            val (address, enginePath) = splitSocketAddress(server.address)
            val options = IO.Options().apply {
                enginePath?.let { path = it }
                val headersMap = headersManager.buildServerHeadersList(server)
                if (headersMap.isNotEmpty()) {
                    extraHeaders = headersMap
                }
            }

            IO.socket(address, options).apply {
                // Socket events arrive on one thread. connect() diagnoses the first connect, so
                // only a dropped socket's reconnects are diagnosed here, once per outage.
                var hasConnected = false
                var outageDiagnosed = false

                on(Socket.EVENT_CONNECT) {
                    hasConnected = true
                    outageDiagnosed = false
                    sessionStateHolder.tryEmitConnectedState(true)
                }

                on(Socket.EVENT_DISCONNECT) {
                    sessionStateHolder.tryEmitConnectedState(false)
                }

                on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Timber.d("Socket connect error: ${args.firstOrNull()}")
                    sessionStateHolder.tryEmitConnectedState(false)
                    if (hasConnected && !outageDiagnosed) {
                        outageDiagnosed = true
                        reportBlockedReconnect(target = this, server = server)
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Invalid socket server address")
            null
        } catch (e: URISyntaxException) {
            Timber.e(e, "Malformed socket URI")
            null
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error creating socket")
            null
        }
    }

    /**
     * Socket.IO treats a URL path as a namespace, so a server behind a reverse proxy subpath
     * (`https://host/pifire`) has to connect to the host with the subpath as the engine path.
     */
    private fun splitSocketAddress(address: String): Pair<String, String?> {
        val uri = URI(address)
        val subPath = uri.rawPath.orEmpty().trimEnd('/')
        if (uri.scheme == null || uri.rawAuthority == null || subPath.isEmpty()) {
            return address to null
        }
        return "${uri.scheme}://${uri.rawAuthority}" to "$subPath$SOCKET_IO_PATH"
    }

    override suspend fun connect(
        server: Server,
        disconnectOnCancellation: Boolean,
    ): Result<Unit, DataError> {
        disconnect()
        val newSocket = createSocket(server) ?: return Result.Error(DataError.Network.UNKNOWN_HOST)
        socket.value = newSocket

        val connected = try {
            awaitConnection(newSocket)
        } catch (e: Exception) {
            if (disconnectOnCancellation) disconnect(target = newSocket)
            throw e
        }
        if (connected) return Result.Success(Unit)
        // Without this a failed socket keeps reconnecting and later flips the connected state.
        disconnect(target = newSocket)
        return Result.Error(diagnose(server))
    }

    private fun reportBlockedReconnect(
        target: Socket,
        server: Server,
    ) {
        scope.launch {
            val error = diagnose(server)
            if (socket.value === target && error.blocksConnection()) {
                sessionStateHolder.tryEmitConnectionBlocked(error)
            }
        }
    }

    /**
     * Why the socket can't connect. Socket.IO's own error doesn't say (a proxy's login page is just
     * a "poll error"), so this makes one plain request to the Engine.IO endpoint and maps the answer.
     */
    private suspend fun diagnose(server: Server): DataError = withContext(Dispatchers.IO) {
        try {
            val (address, enginePath) = splitSocketAddress(server.address)
            val request = Request.Builder()
                .url("${address.trimEnd('/')}${enginePath ?: SOCKET_IO_PATH}/?EIO=4&transport=polling")
                .apply {
                    headersManager.buildServerHeadersList(server).forEach { (name, values) ->
                        values.forEach { value -> addHeader(name, value) }
                    }
                }
                .build()
            diagnoseClient.newCall(request).execute().use { response ->
                when {
                    response.code == 401 -> DataError.Network.AUTHENTICATION

                    response.isSuccessful -> proxyResponseError(
                        url = response.request.url,
                        contentType = response.header("Content-Type"),
                        body = response.peekBody(DIAGNOSE_PEEK_BYTES).string(),
                    ) ?: DataError.Network.SOCKET_ERROR

                    else -> DataError.Network.SOCKET_ERROR
                }
            }
        } catch (e: IOException) {
            e.toNetworkError()
        } catch (e: IllegalArgumentException) {
            Timber.d(e, "Invalid socket server address")
            DataError.Network.UNKNOWN_HOST
        } catch (e: URISyntaxException) {
            Timber.d(e, "Malformed socket URI")
            DataError.Network.UNKNOWN_HOST
        }
    }

    override fun disconnect() {
        disconnect(target = socket.value)
        sessionStateHolder.tryEmitConnectedState(false)
    }

    private fun disconnect(target: Socket?) {
        if (target == null || !socket.compareAndSet(target, null)) return
        Timber.i("Socket Disconnect")
        target.off()
        target.disconnect()
        sessionStateHolder.tryEmitConnectedState(false)
    }

    override fun isConnected(): Boolean = socket.value?.connected() == true

    private suspend fun connected(target: Socket): Boolean {
        if (target.connected()) return true
        return awaitConnection(target)
    }

    private suspend fun awaitConnection(target: Socket): Boolean = withTimeoutOrNull(CONNECT_TIMEOUT_MS.milliseconds) {
        suspendCancellableCoroutine { continuation ->
            val onConnect = Emitter.Listener {
                if (continuation.isActive) continuation.resume(true)
            }
            val onError = Emitter.Listener {
                if (continuation.isActive) continuation.resume(false)
            }
            target.once(Socket.EVENT_CONNECT, onConnect)
            target.once(Socket.EVENT_CONNECT_ERROR, onError)
            continuation.invokeOnCancellation {
                target.off(Socket.EVENT_CONNECT, onConnect)
                target.off(Socket.EVENT_CONNECT_ERROR, onError)
            }
            target.connect()
        }
    } ?: false

    override suspend fun get(vararg args: Any): Result<String, DataError> = internalEmit(ServerConstants.GF_GET_APP_DATA, *args)

    override suspend fun postResult(vararg args: Any): Result<String, DataError> = internalEmit(ServerConstants.PF_POST_APP_DATA, *args)

    override suspend fun post(vararg args: Any): Result<Unit, DataError> = internalEmit(ServerConstants.PF_POST_APP_DATA, *args).let { result ->
        when (result) {
            is Result.Error -> Result.Error(result.error)

            is Result.Success -> {
                when (val data = parsePostResponse(result.data, json)) {
                    is Result.Error -> Result.Error(data.error)
                    is Result.Success -> Result.Success(Unit)
                }
            }
        }
    }

    private suspend fun internalEmit(
        event: String,
        vararg args: Any,
    ): Result<String, DataError> {
        val target = socket.value
        if (target == null || !connected(target)) {
            return Result.Error(DataError.Network.SOCKET_ERROR)
        }

        return withTimeoutOrNull(ACK_TIMEOUT_MS.milliseconds) {
            suspendCancellableCoroutine { continuation ->
                target.emit(
                    event,
                    *args,
                    Ack { result ->
                        val data = result.firstOrNull()
                        // A resume after the timeout canceled the continuation is ignored.
                        continuation.resume(
                            if (data != null) {
                                Result.Success(data.toString())
                            } else {
                                Result.Error(DataError.Network.SERVER_ERROR)
                            },
                        )
                    },
                )
            }
        } ?: Result.Error(DataError.Network.SOCKET_TIMEOUT)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun listen(event: String): Flow<Result<Array<out Any?>, DataError>> =
        socket.flatMapLatest { current ->
            if (current == null) {
                flowOf(Result.Error(DataError.Network.SOCKET_ERROR))
            } else {
                listen(target = current, event = event)
            }
        }

    private fun listen(
        target: Socket,
        event: String,
    ): Flow<Result<Array<out Any?>, DataError>> = callbackFlow<Result<Array<out Any?>, DataError>> {
        // trySend never blocks the socket's EventThread; the buffer drops the oldest on overflow.
        val listener = Emitter.Listener { args -> trySend(Result.Success(args)) }

        if (!target.connected()) {
            trySend(Result.Error(DataError.Network.SOCKET_ERROR))
        }

        target.on(event, listener)

        awaitClose {
            target.off(event, listener)
        }
    }.buffer(capacity = LISTEN_BUFFER_SIZE, onBufferOverflow = BufferOverflow.DROP_OLDEST)
}
