package com.weberbox.pifire.core.singleton

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parsePostResponse
import com.weberbox.pifire.common.data.util.AckWithTimeout
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.settings.data.util.HeadersManager
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SocketManager @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val headersManager: HeadersManager,
    private val json: Json
) {
    var socket: Socket? = null
    private var address: String? = null

    private suspend fun startSocket(server: Server): Socket {
        Timber.i("Creating Socket connection")

        val options = IO.Options().apply {
            val headersMap = headersManager.buildServerHeadersList(server)

            if (headersMap.isNotEmpty()) {
                extraHeaders = headersMap
            }
        }

        return IO.socket(server.address, options).apply {
            on(Socket.EVENT_CONNECT) {
                sessionStateHolder.tryEmitConnectedState(true)
            }

            on(Socket.EVENT_DISCONNECT) {
                sessionStateHolder.tryEmitConnectedState(false)
            }

            connect()
        }
    }

    suspend fun initSocket(
        server: Server,
        disconnectOnCancellation: Boolean = false
    ): Boolean {
        disconnect()
        socket = startSocket(server)
        return suspendCancellableCoroutine { continuation ->
            socket?.once(Socket.EVENT_CONNECT) {
                if (continuation.isActive) continuation.resume(true)
            }
            socket?.once(Socket.EVENT_CONNECT_ERROR) {
                if (continuation.isActive) continuation.resume(false)
            }

            socket?.connect()

            if (disconnectOnCancellation) continuation.invokeOnCancellation { disconnect() }
        }
    }

    fun disconnect() {
        Timber.i("Socket Disconnect")
        socket?.off()
        socket?.disconnect()
        socket?.close()
        socket = null
        address = null
        sessionStateHolder.tryEmitConnectedState(false)
    }

    fun isConnected(): Boolean = socket?.connected() == true

    suspend fun connected(): Boolean {
        if (socket?.connected() == true) return true

        return suspendCancellableCoroutine { continuation ->
            socket?.once(Socket.EVENT_CONNECT) {
                if (continuation.isActive) continuation.resume(true)
            }
            socket?.once(Socket.EVENT_CONNECT_ERROR) {
                if (continuation.isActive) continuation.resume(false)
            }

            socket?.connect()
            continuation.invokeOnCancellation { socket?.disconnect() }
        }
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
        vararg args: Any
    ): Result<String, DataError> {
        return if (connected()) {
            suspendCoroutine { cont ->
                socket?.emit(
                    event, *args,
                    AckWithTimeout(
                        onSuccess = { result ->
                            if (result.isNotEmpty() && result[0] != null) {
                                cont.resume(Result.Success(result[0].toString()))
                            } else {
                                cont.resume(Result.Error(DataError.Network.SERVER_ERROR))
                            }
                        },
                        onTimeout = {
                            cont.resume(Result.Error(DataError.Network.SOCKET_TIMEOUT))
                        }
                    )
                )
            }
        } else {
            Result.Error(DataError.Network.SOCKET_ERROR)
        }
    }

    fun onFlow(event: String): Flow<Result<Array<out Any?>, DataError>> = callbackFlow {
        val listener = object : Emitter.Listener {
            override fun call(vararg args: Any?) {
                trySendBlocking(Result.Success(args)).onFailure {
                    socket?.off(event, this)
                }
            }
        }

        if (socket?.connected() == false) {
            trySendBlocking(Result.Error(DataError.Network.SOCKET_ERROR))
        }

        socket?.on(event, listener)

        awaitClose {
            socket?.off(event, listener)
        }
    }
}