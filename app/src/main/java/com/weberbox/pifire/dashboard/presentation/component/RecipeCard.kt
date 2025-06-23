package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.slideDownExpandEnterTransition
import com.weberbox.pifire.common.presentation.util.slideOutShrinkExitTransition
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.RecipeStatus

private typealias filename = String
private typealias step = Int

@Composable
internal fun RecipeCard(
    recipeStatus: RecipeStatus,
    modifier: Modifier = Modifier,
    onClick: (filename, step) -> Unit,
) {
    val visible by remember(recipeStatus) { mutableStateOf(recipeStatus.recipeMode) }

    AnimatedVisibility(
        visible = visible,
        enter = slideDownExpandEnterTransition(),
        exit = slideOutShrinkExitTransition()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.smallOne)
                .fillMaxWidth()
        ) {
            Card(
                modifier = modifier
                    .height(MaterialTheme.size.extraLargeTwo)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = MaterialTheme.elevation.small
                ),
                onClick = {
                    onClick(recipeStatus.filename, recipeStatus.step)
                }
            ) {
                Column(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.dash_recipe_info),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.smallOne,
                                start = MaterialTheme.spacing.smallTwo
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(
                                start = MaterialTheme.spacing.smallOne,
                                end = 15.dp
                            )
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = stringResource(R.string.dash_recipe_mode),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne)
                            )
                            Text(
                                text = recipeStatus.mode,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.dash_recipe_step),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne)
                            )
                            Text(
                                text = recipeStatus.step.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = stringResource(R.string.dash_recipe_status),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne)
                            )
                            Text(
                                text = if (recipeStatus.paused)
                                    stringResource(R.string.dash_recipe_paused) else
                                    stringResource(R.string.dash_recipe_running),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeCardPreview() {
    PiFireTheme {
        Surface {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RecipeCard(
                    recipeStatus = RecipeStatus(
                        mode = "Shutdown",
                        paused = false,
                        recipeMode = true
                    ),
                    onClick = {_,_ ->}
                )
            }
        }
    }
}