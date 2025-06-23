package com.weberbox.pifire.common.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Elevation(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 10.dp,
    val medium: Dp = 18.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 64.dp
)

val LocalElevation = compositionLocalOf { Elevation() }

@Suppress("UnusedReceiverParameter")
val MaterialTheme.elevation: Elevation
    @Composable
    @ReadOnlyComposable
    get() = LocalElevation.current