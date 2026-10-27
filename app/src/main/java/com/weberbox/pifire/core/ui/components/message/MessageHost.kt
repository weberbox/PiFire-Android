package com.weberbox.pifire.core.ui.components.message

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.UiMessage
import com.weberbox.pifire.core.ui.util.asString
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

private val AlertDuration = 3.seconds
private val ErrorDuration = 4.seconds
private val DismissDragDistance = 48.dp
private const val DISMISS_FLING_VELOCITY = -1000f

/**
 * Shows [state]'s message as a [MessageCard] floating below the status bar. Place it last in a
 * full-screen `Box`, aligned to the top, so it draws over the content without moving it.
 */
@Composable
fun MessageHost(
    state: MessageHostState,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = state.current,
        modifier = modifier,
        transitionSpec = {
            val slideSpec = spring<IntOffset>(stiffness = Spring.StiffnessMediumLow)
            val fadeSpec = spring<Float>(stiffness = Spring.StiffnessMediumLow)
            (slideInVertically(animationSpec = slideSpec) { -it } + fadeIn(animationSpec = fadeSpec))
                .togetherWith(slideOutVertically(animationSpec = slideSpec) { -it } + fadeOut(animationSpec = fadeSpec))
                .using(SizeTransform(clip = false))
        },
        contentAlignment = Alignment.TopCenter,
        label = "message",
    ) { shown ->
        if (shown != null) {
            DismissibleMessage(shown = shown, onDismiss = { state.dismiss(shown = shown) })
        }
    }
}

/** Times out on its own; tap or swipe up to dismiss sooner. Holding it pauses the timer. */
@Composable
private fun DismissibleMessage(
    shown: ShownMessage,
    onDismiss: () -> Unit,
) {
    val isError = shown.message.style == UiMessage.Style.Error
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isDragging by remember { mutableStateOf(false) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val dismissDistance = with(LocalDensity.current) { DismissDragDistance.toPx() }

    LaunchedEffect(shown) {
        if (isError) haptics.performHapticFeedback(HapticFeedbackType.Reject)
    }
    // Starts over once the card is let go, so the user gets the full time to finish reading.
    LaunchedEffect(isPressed, isDragging) {
        if (isPressed || isDragging) return@LaunchedEffect
        delay(if (isError) ErrorDuration else AlertDuration)
        onDismiss()
    }

    MessageCard(
        text = shown.message.text.asString(),
        isError = isError,
        onClick = onDismiss,
        interactionSource = interactionSource,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                horizontal = MaterialTheme.spacing.smallThree,
                vertical = MaterialTheme.spacing.small,
            ).offset { IntOffset(x = 0, y = offsetY.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    offsetY = (offsetY + delta).coerceAtMost(0f)
                },
                onDragStarted = { isDragging = true },
                onDragStopped = { velocity ->
                    if (offsetY < -dismissDistance || velocity < DISMISS_FLING_VELOCITY) {
                        onDismiss()
                    } else {
                        animate(initialValue = offsetY, targetValue = 0f) { value, _ -> offsetY = value }
                        isDragging = false
                    }
                },
            ),
    )
}
