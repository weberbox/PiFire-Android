package com.weberbox.pifire.core.network.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.network.model.push.RelaySendDto

/**
 * The push relay (docs/PUSH.md 3.2). A failure the relay explains comes back as
 * [DataError.Server] holding its error code (e.g. [ERROR_UNKNOWN_KEY]).
 */
interface RelayApi {
    /** False when this build has no relay URL (`PUSH_RELAY_URL` in local.properties). */
    val isConfigured: Boolean

    /** The relay's base URL, which PiFire servers send alerts to. */
    val baseUrl: String

    /** Returns the new push key. */
    suspend fun register(fcmToken: String): Result<String, DataError>
    suspend fun updateToken(pushKey: String, fcmToken: String): Result<Unit, DataError>
    suspend fun send(alert: RelaySendDto): Result<Unit, DataError>

    companion object {
        const val ERROR_UNKNOWN_KEY = "unknown_key"
        const val ERROR_UNREGISTERED = "unregistered"
    }
}
