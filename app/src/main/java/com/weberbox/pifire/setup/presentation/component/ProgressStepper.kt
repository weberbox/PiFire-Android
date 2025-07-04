package com.weberbox.pifire.setup.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.common.presentation.theme.PiFireTheme

@Composable
internal fun HorizontalDashedStepper(
    modifier: Modifier = Modifier,
    totalSteps: Int,
    currentStep: Number
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size = it }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {

        for (index in 0 until totalSteps) {
            val stepState =
                when {
                    index < currentStep.toInt() -> StepState.DONE
                    index == currentStep.toInt() -> StepState.CURRENT
                    else -> StepState.TODO
                }

            HorizontalDashedStep(
                stepState = stepState,
                totalSteps = totalSteps,
                size = size,
            )
        }
    }
}

@Composable
internal fun HorizontalDashedStep(
    modifier: Modifier = Modifier,
    stepState: StepState,
    totalSteps: Int,
    size: IntSize
) {

    val transition = updateTransition(targetState = stepState, label = "")

    val containerColor: Color by transition.animateColor(label = "itemColor") {
        when (it) {
            StepState.TODO -> MaterialTheme.colorScheme.surfaceContainerHighest
            StepState.CURRENT -> MaterialTheme.colorScheme.primaryContainer
            StepState.DONE -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    val progressState: Float by transition.animateFloat(label = "progress") {
        when (it) {
            StepState.TODO -> 0f
            StepState.CURRENT, StepState.DONE -> 1f
        }
    }

    val trackStrokeCap: StrokeCap = when (stepState) {
        StepState.TODO -> StrokeCap.Round
        StepState.CURRENT -> StrokeCap.Square
        StepState.DONE -> StrokeCap.Square
    }

    val progressStrokeCap: StrokeCap = when (stepState) {
        StepState.TODO -> StrokeCap.Round
        StepState.CURRENT -> StrokeCap.Square
        StepState.DONE -> StrokeCap.Square
    }

    LinearProgressIndicator(
        progress = { progressState },
        modifier = Modifier
            .height(6.dp)
            .then(
                with(LocalDensity.current) {
                    if (totalSteps > 1) {
                        Modifier
                            .widthIn(max = size.width.toDp() / totalSteps)
                            .padding(2.dp)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    }
                }
            )
            .then(modifier),
        color = containerColor,
        trackColor = containerColor,
        strokeCap = if (progressState == 0f) trackStrokeCap else progressStrokeCap,
    )
}

enum class StepState {
    TODO,
    CURRENT,
    DONE
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun HorizontalDashedStepperPreview() {
    PiFireTheme {
        Surface {
            HorizontalDashedStepper(
                totalSteps = 5,
                currentStep = 3
            )
        }
    }
}
