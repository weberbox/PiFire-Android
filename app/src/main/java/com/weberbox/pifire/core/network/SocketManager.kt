package com.weberbox.pifire.core.network

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.settings.data.util.HeadersManager
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException
import javax.inject.Inject
import kotlin.coroutines.resume

private const val CONNECT_TIMEOUT_MS = 20_000L
private const val ACK_TIMEOUT_MS = 10_000L
private const val LISTEN_BUFFER_SIZE = 64
private const val SOCKET_IO_PATH = "/socket.io"

class SocketManager @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val headersManager: HeadersManager,
    private val json: Json,
) {
    // A StateFlow so onFlow listeners move to the new socket after initSocket.
    private val socket = MutableStateFlow<Socket?>(null)

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
                on(Socket.EVENT_CONNECT) {
                    sessionStateHolder.tryEmitConnectedState(true)
                }

                on(Socket.EVENT_DISCONNECT) {
                    sessionStateHolder.tryEmitConnectedState(false)
                }

                on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Timber.d("Socket connect error: ${args.firstOrNull()}")
                    sessionStateHolder.tryEmitConnectedState(false)
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

    suspend fun initSocket(
        server: Server,
        disconnectOnCancellation: Boolean = false,
    ): Boolean {
        disconnect()
        val newSocket = createSocket(server) ?: return false
        socket.value = newSocket

        val connected = try {
            awaitConnection(newSocket)
        } catch (e: Exception) {
            if (disconnectOnCancellation) disconnect(target = newSocket)
            throw e
        }
        // Without this a failed socket keeps reconnecting and later flips the connected state.
        if (!connected) disconnect(target = newSocket)
        return connected
    }

    fun disconnect() {
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

    fun isConnected(): Boolean = socket.value?.connected() == true

    private suspend fun connected(target: Socket): Boolean {
        if (target.connected()) return true
        return awaitConnection(target)
    }

    private suspend fun awaitConnection(target: Socket): Boolean {
        return withTimeoutOrNull(CONNECT_TIMEOUT_MS) {
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
    }

    suspend fun emit(event: String, vararg args: Any): Result<String, DataError> {
        return internalEmit(event, *args)
    }

    suspend fun emitGet(vararg args: Any): Result<String, DataError> {
        return internalEmit(ServerConstants.GF_GET_APP_DATA, *args)
    }

    suspend fun emitPostResult(vararg args: Any): Result<String, DataError> {
        return internalEmit(ServerConstants.PF_POST_APP_DATA, *args)
    }

    suspend fun emitPost(vararg args: Any): Result<Unit, DataError> {
        return internalEmit(ServerConstants.PF_POST_APP_DATA, *args).let { result ->
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
    }

    private suspend fun internalEmit(
        event: String,
        vararg args: Any,
    ): Result<String, DataError> {
        val target = socket.value
        if (target == null || !connected(target)) {
            return Result.Error(DataError.Network.SOCKET_ERROR)
        }

        return withTimeoutOrNull(ACK_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                target.emit(
                    event, *args,
                    Ack { result ->
                        val data = result.firstOrNull()
                        // A resume after the timeout cancelled the continuation is ignored.
                        continuation.resume(
                            if (data != null) {
                                Result.Success(data.toString())
                            } else {
                                Result.Error(DataError.Network.SERVER_ERROR)
                            }
                        )
                    },
                )
            }
        } ?: Result.Error(DataError.Network.SOCKET_TIMEOUT)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onFlow(event: String): Flow<Result<Array<out Any?>, DataError>> =
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
