/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Custom Offset Drawer Implementation derived from Google's ModelNavigationDrawer
package com.weberbox.pifire.home.presentation.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxOfOrNull
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class DrawerValue {
    Closed,
    Open,
}

@Suppress("unused")
class OffsetDrawerState(
    initialValue: DrawerValue
) {
    internal var anchoredDraggableMotionSpec: FiniteAnimationSpec<Float> =
        TweenSpec(durationMillis = 256)
    internal val anchoredDraggableState =
        AnchoredDraggableState(
            initialValue = initialValue
        )

    val isOpen: Boolean
        get() = currentValue == DrawerValue.Open

    val isClosed: Boolean
        get() = currentValue == DrawerValue.Closed

    val currentValue: DrawerValue
        get() = anchoredDraggableState.currentValue

    val isAnimationRunning: Boolean
        get() = anchoredDraggableState.isAnimationRunning

    suspend fun open() =
        animateTo(targetValue = DrawerValue.Open, animationSpec = openDrawerMotionSpec)

    suspend fun close() =
        animateTo(targetValue = DrawerValue.Closed, animationSpec = closeDrawerMotionSpec)

    val progress: Float
        get() = anchoredDraggableState.progress(DrawerValue.Closed, DrawerValue.Open)

    val targetValue: DrawerValue
        get() = anchoredDraggableState.targetValue

    val offset: State<Float> =
        object : State<Float> {
            override val value: Float
                get() = anchoredDraggableState.offset
        }

    val currentOffset: Float
        get() = anchoredDraggableState.offset

    val defaultDrawerWidth: Dp
        get() = 280.dp

    internal var density: Density? by mutableStateOf(null)
    internal var openDrawerMotionSpec: FiniteAnimationSpec<Float> = snap()
    internal var closeDrawerMotionSpec: FiniteAnimationSpec<Float> = snap()
    internal fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    private suspend fun animateTo(
        targetValue: DrawerValue,
        animationSpec: AnimationSpec<Float>,
        velocity: Float = anchoredDraggableState.lastVelocity,
    ) {
        anchoredDraggableState.anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
            val targetOffset = anchors.positionOf(latestTarget)
            if (!targetOffset.isNaN()) {
                var prev = if (currentOffset.isNaN()) 0f else currentOffset
                animate(prev, targetOffset, velocity, animationSpec) { value, velocity ->
                    dragTo(value, velocity)
                    prev = value
                }
            }
        }
    }

    companion object {
        fun Saver() = Saver<OffsetDrawerState, DrawerValue>(
            save = { it.currentValue },
            restore = { OffsetDrawerState(it) },
        )
    }
}

@Composable
fun rememberOffsetDrawerState(
    initialValue: DrawerValue = DrawerValue.Closed,
): OffsetDrawerState {
    return rememberSaveable(saver = OffsetDrawerState.Saver()) {
        OffsetDrawerState(initialValue)
    }
}

@Composable
fun OffsetNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    offsetDrawerState: OffsetDrawerState = rememberOffsetDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    scrimColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.32f),
    overscrollEffect: OverscrollEffect? = null,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var anchorsInitialized by remember { mutableStateOf(false) }
    var maxValue by remember(density) { mutableFloatStateOf(0f) }
    val minValue = 0f
    val anchoredDraggableMotion: FiniteAnimationSpec<Float> = spring(
        dampingRatio = 0.8f,
        stiffness = 380.0f
    )
    val openMotion: FiniteAnimationSpec<Float> = spring(
        dampingRatio = 0.9f,
        stiffness = 700.0f
    )
    val closeMotion: FiniteAnimationSpec<Float> = spring(
        dampingRatio = 1.0f,
        stiffness = 3800.0f
    )
    val animatedScale by remember {
        derivedStateOf { calculateScale(0.85f, 1f, offsetDrawerState.progress) }
    }
    val animatedCorners by remember {
        derivedStateOf { calculateCorners(0, 24, offsetDrawerState.progress) }
    }
    SideEffect {
        offsetDrawerState.density = density
        offsetDrawerState.openDrawerMotionSpec = openMotion
        offsetDrawerState.closeDrawerMotionSpec = closeMotion
        offsetDrawerState.anchoredDraggableMotionSpec = anchoredDraggableMotion
    }

    Box(
        modifier
            .background(containerColor)
            .background(containerColor.copy(alpha = 0.05f))
            .fillMaxSize()
            .anchoredDraggable(
                state = offsetDrawerState.anchoredDraggableState,
                orientation = Orientation.Horizontal,
                enabled = gesturesEnabled,
                overscrollEffect = overscrollEffect
            )

    ) {
        Layout(
            modifier = Modifier.clipToBounds(),
            content = drawerContent,
        ) { measurables, constraints ->
            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
            val placeables = measurables.fastMap { it.measure(looseConstraints) }
            val width = placeables.fastMaxOfOrNull { it.width } ?: 0
            val height = placeables.fastMaxOfOrNull { it.height } ?: 0
            layout(width, height) {
                val currentOpenAnchor =
                    offsetDrawerState.anchoredDraggableState.anchors.positionOf(DrawerValue.Open)
                val calculatedOpenAnchor = width.toFloat()
                if (!anchorsInitialized || currentOpenAnchor != calculatedOpenAnchor) {
                    if (!anchorsInitialized) {
                        anchorsInitialized = true
                    }
                    maxValue = calculatedOpenAnchor
                    offsetDrawerState.anchoredDraggableState.updateAnchors(
                        DraggableAnchors {
                            DrawerValue.Closed at minValue
                            DrawerValue.Open at maxValue
                        }
                    )
                }
                placeables.fastForEach { it.place(0, 0) }
            }
        }
        Box(
            modifier = Modifier
                .overscroll(overscrollEffect)
                .offset {
                    offsetDrawerState.currentOffset.let { offset ->
                        val offsetX =
                            when {
                                !offset.isNaN() -> offset.roundToInt()
                                offsetDrawerState.isOpen ->
                                    offsetDrawerState.defaultDrawerWidth.roundToPx()

                                else -> 0
                            }
                        IntOffset(offsetX, 0)
                    }
                }
                .scale(scale = animatedScale)
                .clip(RoundedCornerShape(animatedCorners))
                .then(
                    if (offsetDrawerState.isOpen) {
                        Modifier.pointerInteropFilter {
                            return@pointerInteropFilter false
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
            Scrim(
                open = offsetDrawerState.isOpen,
                onClose = {
                    if (gesturesEnabled) {
                        scope.launch { offsetDrawerState.close() }
                    }
                },
                fraction = {
                    calculateAlpha(minValue, maxValue, offsetDrawerState.requireOffset())
                },
                color = scrimColor
            )
        }
    }
}

@Suppress("SameParameterValue")
private fun calculateAlpha(minValue: Float, maxValue: Float, progress: Float) =
    ((progress - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)

@Suppress("SameParameterValue")
private fun calculateScale(minScale: Float, maxScale: Float, progress: Float) =
    (maxScale - (maxScale - minScale) * progress).coerceIn(minScale, maxScale)

@Suppress("SameParameterValue")
private fun calculateCorners(minDp: Int, maxDp: Int, progress: Float) =
    (maxDp * progress).dp.coerceIn(minDp.dp, maxDp.dp)

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color
) {
    val dismissDrawer =
        if (open) {
            Modifier.pointerInput(onClose) { detectTapGestures { onClose() } }
        } else {
            Modifier
        }
    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(
            color = color,
            alpha = fraction()
        )
    }
}