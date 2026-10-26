package com.weberbox.pifire.core.ui.model

import androidx.compose.runtime.Immutable
import com.weberbox.pifire.core.ui.util.UiText

@Immutable
data class ErrorStatus(
    val isError: Boolean,
    val errorMsg: UiText? = null,
)
