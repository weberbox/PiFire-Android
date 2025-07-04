package com.weberbox.pifire.recipes.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Step
import com.weberbox.pifire.recipes.presentation.screens.buildRecipe
import com.weberbox.pifire.recipes.presentation.util.toBulletedList
import kotlin.math.roundToInt

@Composable
internal fun RecipeSteps(
    modifier: Modifier = Modifier,
    steps: List<Step>,
    units: String,
    currentStep: Int,
    onStepPosition: (position: Int) -> Unit
) {
    HeaderCard(
        title = stringResource(R.string.recipes_steps),
        headerIcon = Icons.Filled.FormatListNumbered,
        headerBottomPadding = 0.dp
    ) {
        Column(
            modifier = modifier.padding(end = MaterialTheme.spacing.smallOne)
        ) {
            steps.forEachIndexed { index, step ->
                RecipeStep(
                    step = step,
                    index = index,
                    units = units,
                    currentStep = currentStep,
                    onStepPosition = onStepPosition
                )
            }
        }
    }
}

@Composable
private fun RecipeStep(
    modifier: Modifier = Modifier,
    step: Step,
    index: Int,
    units: String,
    currentStep: Int,
    onStepPosition: (position: Int) -> Unit
) {
    if (index != 0) {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        )
    }
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.2f)
                .then(
                    if (currentStep != -1) {
                        if (currentStep == index) Modifier
                            .background(
                                MaterialTheme.colorScheme.primary
                            )
                            .onGloballyPositioned { coordinates ->
                                onStepPosition(coordinates.positionInRoot().y.roundToInt())
                            }
                        else Modifier.background(
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    } else Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.recipes_step_step, index),
                style = MaterialTheme.typography.bodyLarge,
                color = if (currentStep != -1) {
                    if (currentStep == index)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface
                } else MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
        Column(
            modifier = Modifier
                .weight(0.8f)
                .padding(
                    start = MaterialTheme.spacing.smallOne,
                    top = MaterialTheme.spacing.extraSmall,
                    bottom = MaterialTheme.spacing.extraSmall
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(R.string.recipes_step_mode),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (step.mode.equals(RunningMode.Hold.name, ignoreCase = true))
                        stringResource(
                            R.string.recipes_step_mode_hold, step.mode, step.holdTemp, units
                        ) else step.mode,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (!step.mode.equals(RunningMode.Shutdown.name, ignoreCase = true)) {
                if (step.mode.equals(RunningMode.Startup.name, ignoreCase = true)) {
                    Text(
                        text = stringResource(R.string.recipes_step_startup_header),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.recipes_step_options_header),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.mediumOne),
                    text = formatOptions(step, units),
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (step.message.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.recipes_step_notif_header),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        modifier = Modifier.padding(
                            start = MaterialTheme.spacing.mediumOne,
                            bottom = MaterialTheme.spacing.extraSmall
                        ),
                        text = stringResource(
                            R.string.recipes_step_notif,
                            step.message.trim()),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                Spacer(Modifier.height(MaterialTheme.size.mediumFour))
            }
        }
    }
}

@Composable
private fun formatOptions(step: Step, units: String): String {
    val options = ArrayList<String>()
    if (step.mode.equals(RunningMode.Startup.name, ignoreCase = true)) {
        options.add(stringResource(R.string.recipes_step_options_startup))
    } else {
        if (step.triggerTemps.primary > 0) {
            options.add(
                stringResource(
                    R.string.recipes_step_options_primary,
                    step.triggerTemps.primary, units
                )
            )
        }
        var count = 1
        for (temp in step.triggerTemps.food) {
            if (temp > 0) {
                options.add(stringResource(R.string.recipes_step_options_food, count, temp, units))
                count++
            }
        }
        if (step.timer > 0) {
            options.add(stringResource(R.string.recipes_step_options_timer, step.timer))
        }
        if (step.pause) {
            options.add(stringResource(R.string.recipes_step_options_user))
        }
    }
    return options.toBulletedList()
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeStepsPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                RecipeSteps(
                    steps = buildRecipe()[0].steps,
                    units = "F",
                    currentStep = 0,
                    onStepPosition = {}
                )
            }
        }
    }
}