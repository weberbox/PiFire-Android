package com.weberbox.pifire.common.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val default: Dp = 0.dp,
    val extraExtraSmall: Dp = 2.dp,
    val extraSmall: Dp = 4.dp,
    val extraSmallOne: Dp = 6.dp,
    val small: Dp = 8.dp,
    val smallOne: Dp = 10.dp,
    val smallTwo: Dp = 12.dp,
    val smallThree: Dp = 16.dp,
    val medium: Dp = 18.dp,
    val mediumOne: Dp = 20.dp,
    val mediumTwo: Dp = 24.dp,
    val mediumThree: Dp = 28.dp,
    val mediumFour: Dp = 30.dp,
    val large: Dp = 32.dp,
    val largeOne: Dp = 36.dp,
    val largeTwo: Dp = 40.dp,
    val extraLarge: Dp = 64.dp,
    val extraLargeOne: Dp = 70.dp,
    val extraLargeTwo: Dp = 80.dp,
    val extraExtraLarge: Dp = 100.dp,
    val largeIcon: Dp = 120.dp,
    val extraLargeIcon: Dp = 150.dp,
    val pageSideSpace: Dp = 10.dp,
    val cardSideSpace: Dp = 12.dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

@Suppress("UnusedReceiverParameter")
val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current