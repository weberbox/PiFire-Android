package com.weberbox.pifire.feature.settings.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class PwmControl(
    var temp: Int = 7,
    var dutyCycle: Int = 100
)