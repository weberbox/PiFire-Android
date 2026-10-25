package com.weberbox.pifire.feature.settings.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class SmartStart(
    val temp: Int = 0,
    val startUp: Int = 0,
    val augerOn: Int = 0,
    val pMode: Int = 0
)