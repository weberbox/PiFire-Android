package com.weberbox.pifire.common.presentation.model

data class InputState(
    val input: FieldInput = FieldInput(),
    val error: ErrorStatus = ErrorStatus(isError = false)
)