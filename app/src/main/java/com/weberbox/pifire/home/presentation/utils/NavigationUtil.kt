package com.weberbox.pifire.home.presentation.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.common.presentation.util.WindowSize
import com.weberbox.pifire.common.presentation.util.windowHeightSize
import com.weberbox.pifire.common.presentation.util.windowWidthSize

@Composable
fun navigationType(): NavigationType {
    val windowWidth = windowWidthSize()
    val windowHeight = windowHeightSize()

    return when {
        windowWidth == WindowSize.EXPANDED && windowHeight == WindowSize.COMPACT ->
            NavigationType.NavRail

        windowWidth == WindowSize.MEDIUM ->
            NavigationType.NavRail

        windowWidth == WindowSize.EXPANDED ->
            NavigationType.NavPermDrawer

        else -> NavigationType.NavBottomBar
    }
}

@Composable
fun Modifier.offsetDrawerWidth(): Modifier {
    return when (navigationType()) {
        NavigationType.NavBottomBar -> this.fillMaxWidth(fraction = 0.6f)
        else -> this.fillMaxWidth(fraction = 0.3f)
    }
}

@Composable
fun Modifier.permDrawerAdjustments(): Modifier {
    return if (navigationType() == NavigationType.NavPermDrawer) {
        this.windowInsetsPadding(WindowInsets.statusBars)
            .clip(RoundedCornerShape(topStart = 24.dp))
    } else this
}

@Composable
fun navContentPadding(contentPadding: PaddingValues): PaddingValues {
    val navigationType = navigationType()
    return if (navigationType == NavigationType.NavPermDrawer ||
        navigationType == NavigationType.NavRail
    )
        PaddingValues(
            start = 0.dp,
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding(),
            end = contentPadding.calculateRightPadding(LocalLayoutDirection.current)
        ) else contentPadding
}

@Composable
fun isBottomBarNavigation(): Boolean {
    return when (navigationType()) {
        NavigationType.NavBottomBar -> true
        else -> false
    }
}

@Composable
fun isRailNavigation(): Boolean {
    return when (navigationType()) {
        NavigationType.NavRail -> true
        else -> false
    }
}

@Composable
fun isPermDrawerNavigation(): Boolean {
    return when (navigationType()) {
        NavigationType.NavPermDrawer -> true
        else -> false
    }
}

sealed interface NavigationType {
    data object NavPermDrawer : NavigationType
    data object NavRail : NavigationType
    data object NavBottomBar : NavigationType
}