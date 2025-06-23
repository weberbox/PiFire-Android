package com.weberbox.pifire.settings.presentation.model

data class ManualData(
    val active: Boolean = false,
    val fan: Boolean = false,
    val auger: Boolean = false,
    val igniter: Boolean = false,
    val power: Boolean = false,
    val pwm: Int = 100,
    val dcFan: Boolean = false
)
