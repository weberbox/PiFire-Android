package com.weberbox.pifire.core.network.transport

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import kotlinx.coroutines.flow.Flow

/**
 * A connection to a PiFire server: Socket.IO ([com.weberbox.pifire.core.network.SocketManager])
 * or mobile API v2 over HTTP + SSE ([HttpSseTransport]). Both speak the Socket.IO actions and
 * return the Socket.IO response shape (`{data, result, message}` as JSON), so `*ApiImpl`s and
 * their parsers work the same over either (docs/V2_API.md).
 */
interface PiFireTransport {
    /** Connects to [server], replacing any current connection; false if it couldn't connect. */
    suspend fun connect(
        server: Server,
        disconnectOnCancellation: Boolean,
    ): Boolean

    fun disconnect()

    fun isConnected(): Boolean

    /** A read: `get_app_data(action, ...)`. */
    suspend fun get(vararg args: Any): Result<String, DataError>

    /** A write returning the server's response: `post_app_data(action, type, json)`. */
    suspend fun postResult(vararg args: Any): Result<String, DataError>

    /** A write that only needs to succeed. */
    suspend fun post(vararg args: Any): Result<Unit, DataError>

    /** Live updates for a Socket.IO event name (e.g. `socket_dash_data`); the payload is the first element. */
    fun listen(event: String): Flow<Result<Array<out Any?>, DataError>>
}
