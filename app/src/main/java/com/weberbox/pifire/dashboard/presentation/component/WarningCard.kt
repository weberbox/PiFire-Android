package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.modifier.doublePulseEffect
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.slideDownExpandEnterTransition
import com.weberbox.pifire.common.presentation.util.slideOutShrinkExitTransition

@Composable
internal fun WarningCard(
    warnings: List<String>,
    modifier: Modifier = Modifier,
    initialState: Boolean = false
) {
    warnings.forEach { warning ->
        var visible by rememberSaveable { mutableStateOf(true) }
        val visibleState = remember { MutableTransitionState(initialState) }

        LaunchedEffect(visible) {
            visibleState.targetState = visible
        }
        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideDownExpandEnterTransition(),
            exit = slideOutShrinkExitTransition()
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.smallOne,
                        end = MaterialTheme.spacing.smallOne
                    )
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = modifier
                        .doublePulseEffect(
                            enabled = visibleState.targetState,
                            targetScale = 1.1f,
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.large
                        )
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = MaterialTheme.elevation.small
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                            .padding(bottom = MaterialTheme.spacing.smallOne)
                            .wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.dash_warnings_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.errorContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.smallOne,
                                start = MaterialTheme.spacing.smallTwo
                            )
                        )
                        Text(
                            text = warning,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraSmall,
                                bottom = MaterialTheme.spacing.extraSmall,
                                start = MaterialTheme.spacing.smallTwo,
                                end = MaterialTheme.spacing.smallTwo
                            )
                        )
                    }
                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = (-3).dp),
                    onClick = { visible = false }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun WarningCardPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                WarningCard(
                    warnings = buildWarningData(),
                    initialState = true
                )
            }
        }
    }
}

private fun buildWarningData(): List<String> {
    return listOf(
        "PiFire is set to OFF. This doesn\'t prevent startup, but this means the " +
                "switch won\'t behave as normal."
    )
}