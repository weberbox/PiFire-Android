package com.weberbox.pifire.common.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class FieldInput(
    val value: String = "",
    val hasInteracted: Boolean = false,
)