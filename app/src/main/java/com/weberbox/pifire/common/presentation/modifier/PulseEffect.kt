package com.weberbox.pifire.common.presentation.modifier

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.scale

private val DefaultPulseColor = Color.Black.copy(0.32f)

@Composable
fun Modifier.doublePulseEffect(
    enabled: Boolean = false,
    targetScale: Float = 1.5f,
    initialScale: Float = 1f,
    color: Color = DefaultPulseColor,
    shape: Shape = CircleShape,
    duration: Int = 1200,
): Modifier = doublePulseEffect(
    enabled, targetScale, initialScale, SolidColor(color), shape, duration)


@Composable
fun Modifier.doublePulseEffect(
    enabled: Boolean = false,
    targetScale: Float = 1.5f,
    initialScale: Float = 1f,
    brush: Brush = SolidColor(DefaultPulseColor),
    shape: Shape = CircleShape,
    duration: Int = 1200,
): Modifier {
    return this
        .pulseEffect(
            enabled, targetScale, initialScale, brush, shape,
            animationSpec = tween(duration, easing = FastOutSlowInEasing)
        )
        .pulseEffect(
            enabled, targetScale, initialScale, brush, shape,
            animationSpec = tween(
                durationMillis = (duration * 0.7f).toInt(),
                delayMillis = (duration * 0.3f).toInt(),
                easing = LinearEasing
            )
        )
}

@Suppress("unused")
@Composable
fun Modifier.pulseEffect(
    enabled: Boolean = false,
    targetScale: Float = 1.5f,
    initialScale: Float = 1f,
    color: Color = DefaultPulseColor,
    shape: Shape = CircleShape,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(1200)
): Modifier = pulseEffect(
    enabled, targetScale, initialScale, SolidColor(color), shape, animationSpec)


@Composable
fun Modifier.pulseEffect(
    enabled: Boolean = false,
    targetScale: Float = 1.5f,
    initialScale: Float = 1f,
    brush: Brush = SolidColor(DefaultPulseColor),
    shape: Shape = CircleShape,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(1200)
): Modifier {
    val pulseTransition = rememberInfiniteTransition("PulseTransition")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = initialScale,
        targetValue = targetScale,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "PulseScale"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "PulseAlpha"
    )
    return this.drawBehind {
        val outline = shape.createOutline(size, layoutDirection, this)
        if (enabled) {
            scale(pulseScale) {
                drawOutline(outline, brush, pulseAlpha)
            }
        }
    }
}