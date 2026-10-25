package com.weberbox.pifire.core.model.info

import kotlinx.serialization.Serializable

@Serializable
data class GPIOInOutData(
    val name: String = String(),
    val pin: String = String()
)
