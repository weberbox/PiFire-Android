package com.weberbox.pifire.feature.update.data.mapper

import com.weberbox.pifire.core.remoteconfig.AppUpdateChannelData
import com.weberbox.pifire.core.remoteconfig.AppUpdateConfigData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdatePolicyMapperTest {
    private fun channel(latest: Int, min: Int = 0, message: String = "") = AppUpdateChannelData(
        latestVersionCode = latest,
        minVersionCode = min,
        message = message,
    )

    @Test
    fun `nothing newer means no policy`() {
        val config = AppUpdateConfigData(stable = channel(latest = 100, min = 90))
        assertNull(config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false))
    }

    @Test
    fun `no channels means no policy`() {
        assertNull(AppUpdateConfigData().toUpdatePolicy(currentVersionCode = 100, includeAlpha = true))
    }

    @Test
    fun `the latest release is offered with its notes`() {
        val config = AppUpdateConfigData(stable = channel(latest = 120, min = 90, message = "New"))
        val policy = config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)
        assertEquals(120, policy?.latestVersionCode)
        assertEquals("New", policy?.message)
        assertFalse(policy?.isRequired == true)
    }

    @Test
    fun `a version below the minimum must update`() {
        val config = AppUpdateConfigData(stable = channel(latest = 120, min = 110))
        assertTrue(config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)?.isRequired == true)
    }

    @Test
    fun `the minimum version itself doesn't have to update`() {
        val config = AppUpdateConfigData(stable = channel(latest = 120, min = 100))
        assertFalse(config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)?.isRequired == true)
    }

    @Test
    fun `the alpha channel counts only for alpha builds`() {
        val config = AppUpdateConfigData(
            stable = channel(latest = 110),
            alpha = channel(latest = 120, min = 105, message = "Alpha"),
        )
        val stable = config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)
        assertEquals(110, stable?.latestVersionCode)
        assertFalse(stable?.isRequired == true)
        val alpha = config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = true)
        assertEquals(120, alpha?.latestVersionCode)
        assertEquals("Alpha", alpha?.message)
        assertTrue(alpha?.isRequired == true)
    }

    @Test
    fun `alpha builds get a stable release newer than the latest alpha`() {
        val config = AppUpdateConfigData(stable = channel(latest = 130), alpha = channel(latest = 120))
        assertEquals(130, config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = true)?.latestVersionCode)
    }

    @Test
    fun `blank notes become null`() {
        val config = AppUpdateConfigData(stable = channel(latest = 110, message = " "))
        assertNull(config.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)?.message)
    }
}
