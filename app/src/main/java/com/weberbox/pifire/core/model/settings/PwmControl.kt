package com.weberbox.pifire.core.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class PwmControl(
    var temp: Int = 7,
    var dutyCycle: Int = 100,
)
