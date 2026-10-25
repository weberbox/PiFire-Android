package com.weberbox.pifire.core.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class SmartStartProfile(
    val startUpTime: Int = 240,
    val augerOnTime: Int = 15,
    val pMode: Int = 2,
)
