package com.weberbox.pifire.core.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.push.PushRegistration
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.api.RelayApi
import com.weberbox.pifire.core.network.model.push.RelaySendDto
import com.weberbox.pifire.core.notification.FcmTokenSource
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PushRepoImplTest {

    private val relayApi = mockk<RelayApi> {
        every { isConfigured } returns true
    }
    private val fcmTokenSource = mockk<FcmTokenSource> {
        coEvery { getToken() } returns Result.Success(TOKEN)
    }
    private val settingsRepo = mockk<SettingsRepo> {
        coEvery { getCurrentServer() } returns Server(uuid = "server-1")
    }
    private val store = FakeDataStore(PushRegistration())

    private val repo = PushRepoImpl(
        relayApi = relayApi,
        fcmTokenSource = fcmTokenSource,
        registrationStore = store,
        settingsRepo = settingsRepo,
    )

    @Test
    fun `registers on first use and saves the key with its token`() = runTest {
        coEvery { relayApi.register(TOKEN) } returns Result.Success(KEY)

        assertEquals(Result.Success<String, DataError>(KEY), repo.getPushKey())
        assertEquals(PushRegistration(pushKey = KEY, fcmToken = TOKEN), store.value)
    }

    @Test
    fun `reuses the saved key without calling the relay when the token is unchanged`() = runTest {
        store.value = PushRegistration(pushKey = KEY, fcmToken = TOKEN)

        assertEquals(Result.Success<String, DataError>(KEY), repo.getPushKey())
        coVerify(exactly = 0) { relayApi.register(any()) }
        coVerify(exactly = 0) { relayApi.updateToken(any(), any()) }
    }

    @Test
    fun `updates the relay when the token changed`() = runTest {
        store.value = PushRegistration(pushKey = KEY, fcmToken = "old-token")
        coEvery { relayApi.updateToken(pushKey = KEY, fcmToken = TOKEN) } returns Result.Success(Unit)

        assertEquals(Result.Success<String, DataError>(KEY), repo.getPushKey())
        assertEquals(PushRegistration(pushKey = KEY, fcmToken = TOKEN), store.value)
    }

    @Test
    fun `registers again when the relay no longer knows the key`() = runTest {
        store.value = PushRegistration(pushKey = KEY, fcmToken = "old-token")
        coEvery { relayApi.updateToken(any(), any()) } returns Result.Error(DataError.Server(RelayApi.ERROR_UNKNOWN_KEY))
        coEvery { relayApi.register(TOKEN) } returns Result.Success("pfk_new")

        assertEquals(Result.Success<String, DataError>("pfk_new"), repo.getPushKey())
        assertEquals(PushRegistration(pushKey = "pfk_new", fcmToken = TOKEN), store.value)
    }

    @Test
    fun `wraps relay error codes in a sentence and keeps other errors`() = runTest {
        coEvery { relayApi.register(TOKEN) } returns Result.Error(DataError.Server("rate_limited"))
        assertEquals(
            Result.Error<String, DataError>(
                DataError.UiText(resId = R.string.data_error_push_relay, args = uiTextArgsOf("rate_limited")),
            ),
            repo.getPushKey(),
        )

        coEvery { relayApi.register(TOKEN) } returns Result.Error(DataError.Network.NO_CONNECTION)
        assertEquals(Result.Error<String, DataError>(DataError.Network.NO_CONNECTION), repo.getPushKey())
        assertEquals(PushRegistration(), store.value)
    }

    @Test
    fun `a new FCM token is ignored until this phone has registered`() = runTest {
        assertEquals(Result.Success<Unit, DataError>(Unit), repo.onNewFcmToken("rotated"))
        coVerify(exactly = 0) { relayApi.updateToken(any(), any()) }

        store.value = PushRegistration(pushKey = KEY, fcmToken = TOKEN)
        coEvery { relayApi.updateToken(pushKey = KEY, fcmToken = "rotated") } returns Result.Success(Unit)
        assertEquals(Result.Success<Unit, DataError>(Unit), repo.onNewFcmToken("rotated"))
        assertEquals("rotated", store.value.fcmToken)
    }

    @Test
    fun `sends a test alert for the current server`() = runTest {
        store.value = PushRegistration(pushKey = KEY, fcmToken = TOKEN)
        val sent = slot<RelaySendDto>()
        coEvery { relayApi.send(capture(sent)) } returns Result.Success(Unit)

        assertEquals(Result.Success<Unit, DataError>(Unit), repo.sendTestPush())
        assertEquals(KEY, sent.captured.pushKey)
        assertEquals("test", sent.captured.type)
        assertEquals("server-1", sent.captured.serverUuid)
    }

    @Test
    fun `forgets the key when the relay dropped it`() = runTest {
        store.value = PushRegistration(pushKey = KEY, fcmToken = TOKEN)
        coEvery { relayApi.send(any()) } returns Result.Error(DataError.Server(RelayApi.ERROR_UNREGISTERED))

        repo.sendTestPush()
        assertEquals(PushRegistration(), store.value)
    }

    @Test
    fun `reports a build without a relay`() = runTest {
        every { relayApi.isConfigured } returns false

        assertEquals(R.string.data_error_push_not_configured, (repo.getPushKey() as? Result.Error)?.error?.let { (it as DataError.UiText).resId })
        coVerify(exactly = 0) { fcmTokenSource.getToken() }
    }

    private class FakeDataStore<T>(initial: T) : DataStore<T> {
        private val state = MutableStateFlow(initial)
        var value: T
            get() = state.value
            set(value) {
                state.value = value
            }

        override val data: Flow<T> = state

        override suspend fun updateData(transform: suspend (t: T) -> T): T = transform(state.value).also { state.value = it }
    }

    private companion object {
        const val TOKEN = "fcm-token"
        const val KEY = "pfk_key"
    }
}
