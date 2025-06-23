package com.weberbox.pifire.common.presentation.util

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

private const val ANIMATION_TIME = 400

fun slidePopEnterTransition(
    animationTime: Int = ANIMATION_TIME,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(animationTime)
): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = animationSpec
    )
}

@Suppress("unused")
fun slidePopExitTransition(
    animationTime: Int = ANIMATION_TIME,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(animationTime)
): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = animationSpec
    )
}

fun slideEnterTransition(
    animationTime: Int = ANIMATION_TIME,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(animationTime)
): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = animationSpec
    )
}

@Suppress("unused")
fun slideExitTransition(
    animationTime: Int = ANIMATION_TIME,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(animationTime)
): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = animationSpec
    )
}

fun fadeEnterTransition(
    animationTime: Int = ANIMATION_TIME,
    animationSpec: FiniteAnimationSpec<Float> = tween(animationTime)
): EnterTransition {
    return fadeIn(
        animationSpec = animationSpec
    )
}

fun fadeExitTransition(
    animationTime: Int = ANIMATION_TIME,
    animationSpec: FiniteAnimationSpec<Float> = tween(animationTime)
): ExitTransition {
    return fadeOut(
        animationSpec = animationSpec
    )
}

fun scaleEnterTransition(
    animationSpec: FiniteAnimationSpec<Float> = SpringSpec(
        dampingRatio = 0.7f,
        stiffness = 400f
    )
): EnterTransition {
    return scaleIn(
        animationSpec = animationSpec
    )
}

fun slideDownFadeEnterTransition(
    animationTime: Int = ANIMATION_TIME,
    fadeAnimationSpec: FiniteAnimationSpec<Float> = tween(animationTime),
    slideAnimationSpec: FiniteAnimationSpec<IntOffset> = tween(animationTime)
): EnterTransition {
    return slideInVertically(
        animationSpec = slideAnimationSpec
    ) + fadeIn(
        animationSpec = fadeAnimationSpec
    )
}

fun slideUpFadeExitTransition(
    animationTime: Int = ANIMATION_TIME,
    fadeAnimationSpec: FiniteAnimationSpec<Float> = tween(animationTime),
    slideAnimationSpec: FiniteAnimationSpec<IntOffset> = tween(animationTime)
): ExitTransition {
    return slideOutVertically(
        animationSpec = slideAnimationSpec
    ) + fadeOut(
        animationSpec = fadeAnimationSpec
    )
}

@Composable
fun slideDownExpandEnterTransition(): EnterTransition {
    val density = LocalDensity.current
    return slideInVertically {
        with(density) { -40.dp.roundToPx() }
    } + expandVertically(
        expandFrom = Alignment.Top
    ) + fadeIn(
        initialAlpha = 0.3f
    )
}

@Composable
fun slideOutShrinkExitTransition(): ExitTransition {
    return slideOutVertically() + shrinkVertically() + fadeOut()
}

@Composable
fun slideUpExpandEnterTransition(): EnterTransition {
    val density = LocalDensity.current
    return slideInVertically {
        with(density) { 40.dp.roundToPx() }
    } + expandVertically(
        expandFrom = Alignment.Bottom
    ) + fadeIn(
        initialAlpha = 0.3f
    )
}

@Composable
fun slideDownShrinkExitTransition(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it / 2 }
    ) + shrinkVertically() + fadeOut()
}

@Composable
fun slideUpEnterTransition(): EnterTransition {
    val density = LocalDensity.current
    return slideInVertically {
        with(density) { it }
    }  + fadeIn(
        initialAlpha = 0.3f
    )
}

@Composable
fun slideDownExitTransition(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it / 2 }
    )  + fadeOut()
}