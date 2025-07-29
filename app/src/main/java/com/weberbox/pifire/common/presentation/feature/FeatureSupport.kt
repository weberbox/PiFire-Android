package com.weberbox.pifire.common.presentation.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalView
import com.weberbox.pifire.common.data.model.SemanticVersion

class FeatureSupport(
    private val currentVersion: String = "0.0.0",
    private val currentBuild: String = "0"
) {
    fun isSupported(feature: Feature): Boolean {
        val version = SemanticVersion.fromString(currentVersion)
        val build = currentBuild.toIntOrNull() ?: 0

        val minVersion = SemanticVersion.fromString(feature.minVersion)
        val maxVersion = feature.maxVersion?.let(SemanticVersion::fromString)

        val minBuild = feature.minBuild.toIntOrNull() ?: 0
        val maxBuild = feature.maxBuild?.toIntOrNull() ?: Int.MAX_VALUE

        // Version checks
        if (version < minVersion) return false
        if (maxVersion != null && version > maxVersion) return false

        // Build checks
        if (build < minBuild) return false
        if (build > maxBuild) return false

        return true
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