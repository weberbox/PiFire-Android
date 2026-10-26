package com.weberbox.pifire.core.model.info

import kotlinx.serialization.Serializable

@Serializable
data class GPIODeviceData(
    val name: String = String(),
    val function: String = String(),
    val pin: String = String(),
)
