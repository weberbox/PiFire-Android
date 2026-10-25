package com.weberbox.pifire.core.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class PwmProfile(
    val dutyCycle: Int = 0
)
