package com.weberbox.pifire.core.ui.model

data class InputState(
    val input: FieldInput = FieldInput(),
    val error: ErrorStatus = ErrorStatus(isError = false)
)