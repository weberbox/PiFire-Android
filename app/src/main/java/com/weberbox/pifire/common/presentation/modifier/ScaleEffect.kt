package com.weberbox.pifire.common.presentation.modifier

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun Modifier.scaleEffect(
    enabled: Boolean = false,
    targetScale: Float = 0.8f,
    initialScale: Float = 1f,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(800)
): Modifier {
    val scaleTransition = rememberInfiniteTransition("ScaleTransition")
    val pulseScale by scaleTransition.animateFloat(
        initialValue = initialScale,
        targetValue = targetScale,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "PulseScale"
    )
    return this.scale(
        scale = if (enabled) pulseScale else initialScale
    )
}