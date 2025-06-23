package com.weberbox.pifire.dashboard.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
internal fun SlideToStart(
    btnText: String,
    btnTextStyle: TextStyle,
    outerBtnBackgroundColor: Color,
    sliderBtnBackgroundColor: Color,
    sliderBtnIcon: ImageVector,
    onSliderChange: ((Float) -> Unit)? = null,
    onBtnSwipe: () -> Unit
) {
    val sliderButtonWidthDp = 70.dp
    val density = LocalDensity.current
    val sliderButtonWidthPx = with(density) { sliderButtonWidthDp.toPx() }
    var sliderPositionPx by remember { mutableFloatStateOf(0f) }
    var boxWidthPx by remember { mutableIntStateOf(0) }
    var showLoadingIndicator by remember { mutableStateOf(false) }
    val maxPositionSlide = boxWidthPx - sliderButtonWidthPx
    val dragProgress = remember(sliderPositionPx, boxWidthPx) {
        if (boxWidthPx > 0) {
            (sliderPositionPx / (boxWidthPx - sliderButtonWidthPx)).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    var sliderComplete by remember { mutableStateOf(false) }
    val animatedOffset by animateFloatAsState(
        targetValue = if (sliderComplete) maxPositionSlide else sliderPositionPx,
        label = "Animated Offset"
    )

    val textAlpha = 1f - dragProgress
    val trackScale by animateFloatAsState(
        targetValue = if (sliderComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "Track Scale"
    )
    val sliderAlpha by animateFloatAsState(
        targetValue = if (sliderComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "Slider Alpha"
    )

    LaunchedEffect(dragProgress) {
        if (dragProgress >= 1f && !sliderComplete) {
            sliderComplete = true
            showLoadingIndicator = true
            onBtnSwipe()
        }
    }

    var swipeVisible by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableIntStateOf(3) }
    LaunchedEffect(swipeVisible, remainingTime, sliderPositionPx) {
        if (sliderPositionPx <= 0f && swipeVisible) {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--
            }
            swipeVisible = false
        } else {
            remainingTime = 3
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .onSizeChanged { size ->
                boxWidthPx = size.width
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(scaleX = trackScale, scaleY = 1f)
                .background(
                    color = outerBtnBackgroundColor,
                    shape = MaterialTheme.shapes.large
                )
        ) {
            Text(
                text = btnText,
                style = btnTextStyle,
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(textAlpha)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(1.dp)
                .offset {
                    IntOffset(
                        x = animatedOffset.toInt(),
                        y = 0
                    )
                }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newPosition = sliderPositionPx + delta
                        val maxPosition = boxWidthPx - sliderButtonWidthPx
                        sliderPositionPx = newPosition.coerceIn(0f, maxPosition)
                        onSliderChange?.invoke(sliderPositionPx)
                    },
                    onDragStarted = { },
                    onDragStopped = {
                        if (sliderPositionPx < maxPositionSlide) {
                            sliderPositionPx = 0f
                        }
                        onSliderChange?.invoke(sliderPositionPx)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .alpha(sliderAlpha)
                    .graphicsLayer { alpha = sliderAlpha }
            ) {
                SliderButton(
                    sliderBtnWidth = sliderButtonWidthDp,
                    sliderBtnBackgroundColor = sliderBtnBackgroundColor,
                    sliderBtnIcon = sliderBtnIcon
                )
            }
        }

        if (showLoadingIndicator) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
private fun SliderButton(
    sliderBtnWidth: Dp,
    sliderBtnBackgroundColor: Color,
    sliderBtnIcon: ImageVector
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .width(sliderBtnWidth)
            .height(54.dp)
            .background(
                color = sliderBtnBackgroundColor,
                shape = MaterialTheme.shapes.large
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = sliderBtnIcon,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}