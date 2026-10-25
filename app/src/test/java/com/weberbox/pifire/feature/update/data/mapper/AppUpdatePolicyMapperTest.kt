package com.weberbox.pifire.feature.update.data.mapper

import com.weberbox.pifire.core.remoteconfig.AppUpdateConfigData
import com.weberbox.pifire.core.remoteconfig.UpdateType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdatePolicyMapperTest {
    private fun release(
        versionCode: Int,
        type: UpdateType = UpdateType.FLEXIBLE,
        message: String = "",
        isAlpha: Boolean = false,
    ) = AppUpdateConfigData(
        appVersionCode = versionCode,
        type = type,
        message = message,
        isAlpha = isAlpha,
    )

    @Test
    fun `nothing newer means no policy`() {
        val releases = listOf(release(versionCode = 100), release(versionCode = 90, type = UpdateType.IMMEDIATE))
        assertNull(releases.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false))
    }

    @Test
    fun `the latest newer release is offered with its notes`() {
        val releases = listOf(release(versionCode = 110, message = "Old"), release(versionCode = 120, message = "New"))
        val policy = releases.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)
        assertEquals(120, policy?.latestVersionCode)
        assertEquals("New", policy?.message)
        assertFalse(policy?.isRequired == true)
    }

    @Test
    fun `an immediate release in between makes the update required`() {
        val releases = listOf(
            release(versionCode = 110, type = UpdateType.IMMEDIATE),
            release(versionCode = 120),
        )
        val policy = releases.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)
        assertEquals(120, policy?.latestVersionCode)
        assertTrue(policy?.isRequired == true)
    }

    @Test
    fun `an immediate release this version already has doesn't require anything`() {
        val releases = listOf(release(versionCode = 100, type = UpdateType.IMMEDIATE), release(versionCode = 120))
        assertFalse(releases.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)?.isRequired == true)
    }

    @Test
    fun `alpha releases count only for alpha builds`() {
        val releases = listOf(release(versionCode = 110), release(versionCode = 120, type = UpdateType.IMMEDIATE, isAlpha = true))
        val stable = releases.toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)
        assertEquals(110, stable?.latestVersionCode)
        assertFalse(stable?.isRequired == true)
        val alpha = releases.toUpdatePolicy(currentVersionCode = 100, includeAlpha = true)
        assertEquals(120, alpha?.latestVersionCode)
        assertTrue(alpha?.isRequired == true)
    }

    @Test
    fun `blank notes become null`() {
        val policy = listOf(release(versionCode = 110, message = " ")).toUpdatePolicy(currentVersionCode = 100, includeAlpha = false)
        assertNull(policy?.message)
    }
}
