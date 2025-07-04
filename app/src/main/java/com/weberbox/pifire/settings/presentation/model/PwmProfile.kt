package com.weberbox.pifire.settings.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class PwmProfile(
    val dutyCycle: Int = 0
)
