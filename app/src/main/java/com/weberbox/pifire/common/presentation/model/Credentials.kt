package com.weberbox.pifire.common.presentation.model

data class Credentials(
    val username: InputState = InputState(),
    val password: InputState = InputState()
)
