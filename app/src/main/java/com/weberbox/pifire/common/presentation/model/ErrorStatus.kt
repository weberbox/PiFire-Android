package com.weberbox.pifire.common.presentation.model

import androidx.compose.runtime.Immutable
import com.weberbox.pifire.common.presentation.util.UiText

@Immutable
data class ErrorStatus(
    val isError: Boolean,
    val errorMsg: UiText? = null,
)