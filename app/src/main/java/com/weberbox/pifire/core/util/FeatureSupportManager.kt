package com.weberbox.pifire.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalView
import com.weberbox.pifire.common.data.model.SemanticVersion
import kotlinx.serialization.Serializable

enum class Feature {
    StartToHoldPrompt,
    NewSystemInfo
}

class FeatureSupport(
    private val currentVersion: String = "0.0.0",
    private val currentBuild: String = "0",
    private val featureMap: FeatureFlagsConfig = emptyMap()
) {
    fun isSupported(feature: Feature): Boolean {
        val config = featureMap[feature.name]

        val enabled = config?.enabled ?: false
        if (!enabled) return false

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
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val isPreview = LocalView.current.isInEditMode
    val featureSupport = LocalFeatureSupport.current

    if (isPreview || featureSupport.isSupported(feature)) {
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
    val maxBuild: String = ""
)

typealias FeatureFlagsConfig = Map<String, RemoteFeatureConfigData>