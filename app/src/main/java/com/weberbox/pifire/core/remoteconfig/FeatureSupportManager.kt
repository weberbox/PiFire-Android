package com.weberbox.pifire.core.remoteconfig

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalView
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.core.model.SemanticVersion
import kotlinx.serialization.Serializable

enum class Feature {
    StartToHoldPrompt,
    NewSystemInfo,
    WLEDNotifications,
    PWMStartupDuty,
    ETACalculations,
    FanPidSettings,

    /** HTTP + SSE transport and relay push (docs/V2_API.md); checked against the version `/hello` reports. */
    MobileApiV2,
}

class FeatureSupport(
    private val currentVersion: String = "0.0.0",
    private val currentBuild: String = "0",
    private val featureMap: FeatureFlagsConfig = emptyMap(),
    private val appVersionCode: Int = BuildConfig.VERSION_CODE,
) {
    fun isSupported(feature: Feature): Boolean {
        val config = featureMap[feature.name]

        val enabled = config?.enabled ?: false
        if (!enabled) return false
        if (appVersionCode < config.minAppVersionCode) return false

        val version = SemanticVersion.fromString(currentVersion)
        val build = currentBuild.toIntOrNull() ?: 0

        val minVersion = SemanticVersion.fromString(config.minVersion)
        val maxVersion = (config.maxVersion)
            .takeIf { it.isNotBlank() }
            ?.let(SemanticVersion::fromString)

        val minBuild = (config.minBuild).toIntOrNull() ?: 0
        val maxBuild = (config.maxBuild).toIntOrNull() ?: Int.MAX_VALUE

        return version >= minVersion &&
            (maxVersion == null || version <= maxVersion) &&
            build in minBuild..maxBuild
    }
}

@Composable
fun FeatureGate(
    feature: Feature,
    enabled: Boolean = true,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val isPreview = LocalView.current.isInEditMode
    val featureSupport = LocalFeatureSupport.current

    if (isPreview || (featureSupport.isSupported(feature) && enabled)) {
        content()
    } else {
        fallback()
    }
}

val LocalFeatureSupport = compositionLocalOf<FeatureSupport> {
    error("FeatureSupport not provided. Make sure to wrap your app in CompositionLocalProvider.")
}

@Serializable
data class RemoteFeatureConfigData(
    val enabled: Boolean = false,
    val minVersion: String = "",
    val minBuild: String = "",
    val maxVersion: String = "",
    val maxBuild: String = "",
    /**
     * Turns the feature off in older app versions, e.g. ones whose side of it is broken. App
     * versions before 3.4.2 don't know this field and ignore it.
     */
    val minAppVersionCode: Int = 0,
)

typealias FeatureFlagsConfig = Map<String, RemoteFeatureConfigData>
