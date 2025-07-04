package com.weberbox.pifire.common.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

@Composable
fun windowWidthSize(): WindowSize {
    val containerSize = LocalWindowInfo.current.containerSize
    val width = with(LocalDensity.current) { containerSize.width.toDp() }.value.toInt()

    return when {
        width < WindowWidthSize.MEDIUM.dp ->
            WindowSize.COMPACT

        width < WindowWidthSize.EXPANDED.dp ->
            WindowSize.MEDIUM

        else -> WindowSize.EXPANDED
    }
}

@Composable
fun windowHeightSize(): WindowSize {
    val containerSize = LocalWindowInfo.current.containerSize
    val height = with(LocalDensity.current) { containerSize.height.toDp() }.value.toInt()

    return when {
        height < WindowHeightSize.MEDIUM.dp ->
            WindowSize.COMPACT

        height < WindowHeightSize.EXPANDED.dp ->
            WindowSize.MEDIUM

        else -> WindowSize.EXPANDED
    }
}

@Suppress("unused")
@Composable
fun rememberWindowInfo(): WindowInfo {
    val containerSize = LocalWindowInfo.current.containerSize
    val width = with(LocalDensity.current) { containerSize.width.toDp() }
    val height = with(LocalDensity.current) { containerSize.height.toDp() }

    return WindowInfo(
        screenWidthInfo = when {
            width.value.toInt() < WindowWidthSize.MEDIUM.dp -> WindowInfo.WindowType.Compact
            width.value.toInt() < WindowWidthSize.EXPANDED.dp -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenHeightInfo = when {
            height.value.toInt() < WindowHeightSize.MEDIUM.dp -> WindowInfo.WindowType.Compact
            height.value.toInt() < WindowHeightSize.EXPANDED.dp -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenWidth = width,
        screenHeight = height
    )
}

data class WindowInfo(
    val screenWidthInfo: WindowType,
    val screenHeightInfo: WindowType,
    val screenWidth: Dp,
    val screenHeight: Dp
) {
    sealed class WindowType {
        data object Compact: WindowType()
        data object Medium: WindowType()
        data object Expanded: WindowType()
    }
}

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED
}

enum class WindowWidthSize(val dp: Int) {
    MEDIUM(600),
    EXPANDED(840)
}

enum class WindowHeightSize(val dp: Int) {
    MEDIUM(480),
    EXPANDED(900)
}