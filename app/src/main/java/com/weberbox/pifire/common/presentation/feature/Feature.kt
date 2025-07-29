package com.weberbox.pifire.common.presentation.feature

sealed class Feature(
    val minVersion: String,
    val minBuild: String,
    val maxVersion: String? = null,
    val maxBuild: String? = null
) {
    object StartToHoldPrompt : Feature("1.10.0", "33")
    object NewSystemInfo : Feature("1.10.0", "35")
}