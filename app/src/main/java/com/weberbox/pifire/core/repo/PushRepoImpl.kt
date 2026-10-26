package com.weberbox.pifire.core.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.andThen
import com.weberbox.pifire.core.common.model.map
import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushRegistration
import com.weberbox.pifire.core.network.api.RelayApi
import com.weberbox.pifire.core.network.model.push.RelaySendDto
import com.weberbox.pifire.core.notification.FcmTokenSource
import com.weberbox.pifire.core.ui.util.EmptyUiArgs
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class PushRepoImpl @Inject constructor(
    private val relayApi: RelayApi,
    private val fcmTokenSource: FcmTokenSource,
    private val registrationStore: DataStore<PushRegistration>,
    private val settingsRepo: SettingsRepo,
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
        val DEAD_KEY_CODES = setOf(RelayApi.ERROR_UNKNOWN_KEY, RelayApi.ERROR_UNREGISTERED)
        val NOT_CONFIGURED = DataError.UiText(resId = R.string.data_error_push_not_configured, args = EmptyUiArgs)
    }
}
