package com.weberbox.pifire.core.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class FieldInput(
    val value: String = "",
    val hasInteracted: Boolean = false,
)