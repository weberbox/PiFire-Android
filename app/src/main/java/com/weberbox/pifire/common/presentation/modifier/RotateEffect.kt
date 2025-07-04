package com.weberbox.pifire.common.presentation.modifier

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun Modifier.rotateEffect(
    enabled: Boolean = false,
    targetDegrees: Float = 360f,
    initialDegrees: Float = 0f,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(1200, easing = LinearEasing)
): Modifier {
    val rotateTransition = rememberInfiniteTransition("rotateTransition")
    val rotation by rotateTransition.animateFloat(
        initialValue = initialDegrees,
        targetValue = targetDegrees,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "Rotation"
    )

    return this.rotate(
        degrees = if (enabled) rotation else initialDegrees
    )
}