package com.weberbox.pifire.core.repo

import android.os.Build
import androidx.datastore.core.DataStore
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.andThen
import com.weberbox.pifire.core.common.model.map
import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushDevice
import com.weberbox.pifire.core.model.push.PushRegistration
import com.weberbox.pifire.core.network.api.PushDevicesApi
import com.weberbox.pifire.core.network.api.RelayApi
import com.weberbox.pifire.core.network.model.push.PushDeviceRegisterDto
import com.weberbox.pifire.core.network.model.push.RelaySendDto
import com.weberbox.pifire.core.notification.FcmTokenSource
import com.weberbox.pifire.core.ui.util.EmptyUiArgs
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class PushRepoImpl @Inject constructor(
    private val relayApi: RelayApi,
    private val fcmTokenSource: FcmTokenSource,
    private val registrationStore: DataStore<PushRegistration>,
    private val settingsRepo: SettingsRepo,
    private val pushDevicesApi: PushDevicesApi,
) : PushRepo {

    // One registration at a time, so two callers never create two push keys.
    private val mutex = Mutex()

    override val isRelayConfigured: Boolean
        get() = relayApi.isConfigured

    override suspend fun getPushKey(): Result<String, DataError> {
        if (!isRelayConfigured) return Result.Error(NOT_CONFIGURED)
        return fcmTokenSource.getToken().andThen { token ->
            mutex.withLock { syncRegistration(token) }
        }.toUserError()
    }

    override suspend fun onNewFcmToken(token: String): Result<Unit, DataError> = mutex.withLock {
        // Without a push key there's nothing to update; the first getPushKey registers this token.
        if (!isRelayConfigured || registrationStore.data.first().pushKey.isEmpty()) {
            Result.Success(Unit)
        } else {
            syncRegistration(token).map { }.toUserError()
        }
    }

    override suspend fun sendTestPush(): Result<Unit, DataError> = getPushKey().andThen { pushKey ->
        val alert = RelaySendDto(
            pushKey = pushKey,
            alertId = UUID.randomUUID().toString(),
            type = AlertType.TEST.wireName,
            serverUuid = settingsRepo.getCurrentServer()?.uuid?.takeIf { it.isNotBlank() } ?: TEST_SERVER_UUID,
            time = System.currentTimeMillis() / 1000,
        )
        relayApi.send(alert).also { result ->
            // The relay deleted the key (FCM said the token is gone): register fresh next time.
            if (result is Result.Error && result.error.relayCode() in DEAD_KEY_CODES) {
                registrationStore.updateData { PushRegistration() }
            }
        }.toUserError()
    }

    override suspend fun registerWithServer(oneSignalPlayerId: String?): Result<Unit, DataError> = getPushKey().andThen { pushKey ->
        pushDevicesApi.register(
            PushDeviceRegisterDto(
                pushKey = pushKey,
                relayUrl = relayApi.baseUrl,
                deviceName = Build.MODEL.orEmpty(),
                appVersion = BuildConfig.VERSION_NAME,
            ),
        )
    }.andThen {
        val leftover = oneSignalPlayerId?.takeIf { playerId ->
            settingsRepo.getCurrentServer()?.settings?.onesignalDevices.orEmpty().containsKey(playerId)
        }
        if (leftover == null) Result.Success(Unit) else pushDevicesApi.removeOneSignalDevice(leftover)
    }

    override suspend fun getServerDevices(): Result<List<PushDevice>, DataError> = pushDevicesApi.list().map { devices ->
        val thisPhoneId = registrationStore.data.first().pushKey.takeIf { it.isNotEmpty() }?.let(::serverDeviceId)
        devices.mapNotNull { device ->
            device.id?.let { id ->
                PushDevice(
                    id = id,
                    deviceName = device.deviceName.orEmpty(),
                    appVersion = device.appVersion.orEmpty(),
                    registered = device.registered ?: 0,
                    isThisPhone = id == thisPhoneId,
                )
            }
        }
    }

    override suspend fun removeServerDevice(id: String): Result<Unit, DataError> = pushDevicesApi.remove(id)

    /** Callers hold [mutex]. Returns the push key, registering or updating the token as needed. */
    private suspend fun syncRegistration(fcmToken: String): Result<String, DataError> {
        val saved = registrationStore.data.first()
        if (saved.pushKey.isNotEmpty()) {
            if (saved.fcmToken == fcmToken) return Result.Success(saved.pushKey)
            when (val update = relayApi.updateToken(pushKey = saved.pushKey, fcmToken = fcmToken)) {
                is Result.Success -> {
                    registrationStore.updateData { saved.copy(fcmToken = fcmToken) }
                    return Result.Success(saved.pushKey)
                }

                is Result.Error -> {
                    if (update.error.relayCode() != RelayApi.ERROR_UNKNOWN_KEY) return Result.Error(update.error)
                    Timber.d("Relay no longer knows this push key, registering again")
                }
            }
        }
        return relayApi.register(fcmToken = fcmToken).andThen { pushKey ->
            registrationStore.updateData { PushRegistration(pushKey = pushKey, fcmToken = fcmToken) }
            Result.Success(pushKey)
        }
    }

    // PiFire's device id: the first 16 hex chars of sha256(push key) (docs/V2_API.md section 6).
    private fun serverDeviceId(pushKey: String): String = MessageDigest.getInstance("SHA-256")
        .digest(pushKey.toByteArray())
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
        .take(SERVER_DEVICE_ID_LENGTH)

    private fun DataError.relayCode(): String? = (this as? DataError.Server)?.data

    // The relay's codes are for us, not the user: wrap them in a sentence.
    private fun <T> Result<T, DataError>.toUserError(): Result<T, DataError> = when (this) {
        is Result.Success -> this

        is Result.Error -> Result.Error(
            error.relayCode()?.let { code ->
                DataError.UiText(resId = R.string.data_error_push_relay, args = uiTextArgsOf(code))
            } ?: error,
        )
    }

    private companion object {
        // Shown as the server when no PiFire is saved; any id matching the relay's pattern works.
        const val TEST_SERVER_UUID = "app-test"
        const val SERVER_DEVICE_ID_LENGTH = 16
        val DEAD_KEY_CODES = setOf(RelayApi.ERROR_UNKNOWN_KEY, RelayApi.ERROR_UNREGISTERED)
        val NOT_CONFIGURED = DataError.UiText(resId = R.string.data_error_push_not_configured, args = EmptyUiArgs)
    }
}
