package com.weberbox.pifire.info.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class GPIOInOutData(
    val name: String = String(),
    val pin: String = String()
)
