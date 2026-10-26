package com.weberbox.pifire.core.network.transport

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.SocketManager
import com.weberbox.pifire.core.remoteconfig.RemoteConfigRepo
import com.weberbox.pifire.core.remoteconfig.RemoteFeatureConfigData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PiFireConnectionTest {

    private val server = Server(uuid = "server-1", address = "http://192.168.1.x")
    private val socketManager = mockk<SocketManager>(relaxed = true) {
        coEvery { connect(any(), any()) } returns true
    }
    private val httpSseTransport = mockk<HttpSseTransport>(relaxed = true) {
        coEvery { connect(any(), any()) } returns true
    }
    private val remoteConfigRepo = mockk<RemoteConfigRepo>()
    private val prefs = mockk<Prefs> {
        every { get(Pref.forceMobileApiV2) } returns false
    }
    private val connection = PiFireConnection(
        socketManager = socketManager,
        httpSseTransport = httpSseTransport,
        remoteConfigRepo = remoteConfigRepo,
        prefs = prefs,
    )

    private fun remoteConfig(enabled: Boolean) {
        coEvery { remoteConfigRepo.fetchFeatureSupportConfig() } returns mapOf(
            "MobileApiV2" to RemoteFeatureConfigData(enabled = enabled, minVersion = "1.11.0", minBuild = "19"),
        )
    }

    private fun probe(result: HttpSseTransport.ProbeResult) {
        coEvery { httpSseTransport.probe(any()) } returns result
    }

    @Test
    fun `switched off in Remote Config uses Socket IO without probing`() = runTest {
        remoteConfig(enabled = false)

        connection.connect(server = server, disconnectOnCancellation = false)

        assertFalse(connection.isApiV2)
        coVerify(exactly = 0) { httpSseTransport.probe(any()) }
        coVerify { socketManager.connect(server, false) }
    }

    @Test
    fun `a server in the Remote Config range uses v2`() = runTest {
        remoteConfig(enabled = true)
        probe(HttpSseTransport.ProbeResult.Available(serverVersion = "1.11.0", serverBuild = "19"))

        connection.connect(server = server, disconnectOnCancellation = false)

        assertTrue(connection.isApiV2)
        coVerify { httpSseTransport.connect(server, false) }
    }

    @Test
    fun `a server below the range stays on Socket IO`() = runTest {
        remoteConfig(enabled = true)
        probe(HttpSseTransport.ProbeResult.Available(serverVersion = "1.11.0", serverBuild = "18"))

        connection.connect(server = server, disconnectOnCancellation = false)

        assertFalse(connection.isApiV2)
    }

    @Test
    fun `no v2 or a failed probe falls back to Socket IO`() = runTest {
        remoteConfig(enabled = true)
        probe(HttpSseTransport.ProbeResult.NotAvailable)
        connection.connect(server = server, disconnectOnCancellation = false)
        assertFalse(connection.isApiV2)

        probe(HttpSseTransport.ProbeResult.Failed(DataError.Network.AUTHENTICATION))
        connection.connect(server = server, disconnectOnCancellation = false)
        assertFalse(connection.isApiV2)
    }

    @Test
    fun `the debug switch forces v2 on any server that answers hello`() = runTest {
        every { prefs.get(Pref.forceMobileApiV2) } returns true
        probe(HttpSseTransport.ProbeResult.Available(serverVersion = "1.10.0", serverBuild = "1"))

        connection.connect(server = server, disconnectOnCancellation = false)

        assertTrue(connection.isApiV2)
        coVerify(exactly = 0) { remoteConfigRepo.fetchFeatureSupportConfig() }
    }
}
