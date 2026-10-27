package com.weberbox.pifire.core.remoteconfig

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureSupportTest {
    private fun support(config: RemoteFeatureConfigData, appVersionCode: Int = 200) = FeatureSupport(
        currentVersion = "1.11.0",
        currentBuild = "10",
        featureMap = mapOf(Feature.ETACalculations.name to config),
        appVersionCode = appVersionCode,
    )

    private val enabled = RemoteFeatureConfigData(enabled = true, minVersion = "1.11.0", minBuild = "5")

    @Test
    fun `an enabled feature in range is supported`() {
        assertTrue(support(config = enabled).isSupported(Feature.ETACalculations))
    }

    @Test
    fun `a missing or disabled feature is not supported`() {
        assertFalse(support(config = enabled.copy(enabled = false)).isSupported(Feature.ETACalculations))
        assertFalse(support(config = enabled).isSupported(Feature.FanPidSettings))
    }

    @Test
    fun `an app below the minimum app version doesn't get the feature`() {
        val config = enabled.copy(minAppVersionCode = 200)
        assertTrue(support(config = config, appVersionCode = 200).isSupported(Feature.ETACalculations))
        assertFalse(support(config = config, appVersionCode = 199).isSupported(Feature.ETACalculations))
    }

    @Test
    fun `a server build below the minimum doesn't get the feature`() {
        assertFalse(support(config = enabled.copy(minBuild = "11")).isSupported(Feature.ETACalculations))
    }
}
