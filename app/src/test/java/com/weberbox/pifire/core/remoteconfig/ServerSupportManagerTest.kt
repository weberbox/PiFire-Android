package com.weberbox.pifire.core.remoteconfig

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerSupportManagerTest {
    private val remoteConfigRepo = mockk<RemoteConfigRepo>()
    private val manager = ServerSupportManager(remoteConfigRepo = remoteConfigRepo)

    private fun rule(
        minApp: Int,
        maxApp: Int? = null,
        min: ServerInfo = ServerInfo(version = "1.10.0", build = "29"),
        max: ServerInfo = ServerInfo(),
    ) = ServerSupportRule(
        minAppVersionCode = minApp,
        maxAppVersionCode = maxApp,
        minServerInfo = min,
        maxServerInfo = max,
    )

    @Test
    fun `an open-ended rule covers every later app version`() {
        val open = rule(minApp = 100)
        val config = ServerSupportConfigData(rules = listOf(rule(minApp = 0, maxApp = 99), open))
        assertEquals(open, config.ruleFor(appVersionCode = 100))
        assertEquals(open, config.ruleFor(appVersionCode = 500))
    }

    @Test
    fun `a closed rule covers its range only`() {
        val old = rule(minApp = 50, maxApp = 99)
        val config = ServerSupportConfigData(rules = listOf(old))
        assertEquals(old, config.ruleFor(appVersionCode = 50))
        assertEquals(old, config.ruleFor(appVersionCode = 99))
        assertNull(config.ruleFor(appVersionCode = 49))
        assertNull(config.ruleFor(appVersionCode = 100))
    }

    @Test
    fun `where rules overlap the one starting latest wins`() {
        val newer = rule(minApp = 200)
        val config = ServerSupportConfigData(rules = listOf(newer, rule(minApp = 100)))
        assertEquals(newer, config.ruleFor(appVersionCode = 250))
    }

    @Test
    fun `no rule for this app version is untested`() = runTest {
        coEvery { remoteConfigRepo.fetchServerSupportRule() } returns null
        val result = manager.isServerVersionSupported(server(version = "1.11.0", build = "5"))
        assertEquals(ServerSupportResult.Untested, result)
    }

    @Test
    fun `a server inside the range is supported`() = runTest {
        coEvery { remoteConfigRepo.fetchServerSupportRule() } returns rule(minApp = 0)
        val result = manager.isServerVersionSupported(server(version = "1.11.0", build = "5"))
        assertEquals(ServerSupportResult.Supported, result)
    }

    @Test
    fun `a build below the minimum on the same version is too old`() = runTest {
        coEvery { remoteConfigRepo.fetchServerSupportRule() } returns rule(minApp = 0)
        val result = manager.isServerVersionSupported(server(version = "1.10.0", build = "28"))
        assertTrue(result is ServerSupportResult.UnsupportedMin)
    }

    @Test
    fun `a server past the maximum is too new`() = runTest {
        coEvery { remoteConfigRepo.fetchServerSupportRule() } returns
            rule(minApp = 0, max = ServerInfo(version = "1.10.0", build = "36"))
        val newerBuild = manager.isServerVersionSupported(server(version = "1.10.0", build = "37"))
        val newerVersion = manager.isServerVersionSupported(server(version = "1.11.0", build = "1"))
        assertTrue(newerBuild is ServerSupportResult.UnsupportedMax)
        assertTrue(newerVersion is ServerSupportResult.UnsupportedMax)
    }

    private fun server(version: String, build: String) = ServerInfo(version = version, build = build)
}
