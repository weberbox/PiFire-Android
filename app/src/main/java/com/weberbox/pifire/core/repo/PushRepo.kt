package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.push.PushDevice

/** This phone's registration with the push relay (docs/PUSH.md 4.1). */
interface PushRepo {
    /** False when this build has no relay URL, which turns relay push off. */
    val isRelayConfigured: Boolean

    /** Registers with the relay on first use and returns this phone's push key. */
    suspend fun getPushKey(): Result<String, DataError>

    /** FCM rotated the token: update the relay, if this phone has registered. */
    suspend fun onNewFcmToken(token: String): Result<Unit, DataError>

    /** Sends a test alert to this phone through the relay (debug builds). */
    suspend fun sendTestPush(): Result<Unit, DataError>

    /**
     * Registers this phone for push with the connected API v2 server, then removes the OneSignal
     * entry [oneSignalPlayerId] left there from Socket.IO days, so alerts don't arrive twice.
     */
    suspend fun registerWithServer(oneSignalPlayerId: String?): Result<Unit, DataError>

    /** Phones registered for push on the connected API v2 server. */
    suspend fun getServerDevices(): Result<List<PushDevice>, DataError>

    suspend fun removeServerDevice(id: String): Result<Unit, DataError>
}
