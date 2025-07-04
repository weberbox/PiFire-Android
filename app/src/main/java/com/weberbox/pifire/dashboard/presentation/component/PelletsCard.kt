package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.component.AnimatedCounter
import com.weberbox.pifire.common.presentation.modifier.doublePulseEffect
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.core.constants.AppConfig

@Composable
internal fun PelletsCard(
    modifier: Modifier = Modifier,
    level: Int,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .doublePulseEffect(
                enabled = level < AppConfig.DANGER_PELLET_WARNING && level > 0,
                targetScale = 1.2f,
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.large
            )
            .height(150.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small),
        onClick = {
            if (onClick != null) onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.smallTwo,
                        end = MaterialTheme.spacing.smallOne
                    )
                    .weight(1f),
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.smallOne)
                        .fillMaxWidth(),

                    ) {
                    Text(
                        text = stringResource(R.string.dash_pellets_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = MaterialTheme.spacing.smallOne)
                    ) {
                        AnimatedCounter(
                            count = "$level%",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = stringResource(R.string.dash_remaining_title),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Box(
                        modifier = Modifier.padding(
                            bottom = MaterialTheme.spacing.smallOne,
                            top = MaterialTheme.spacing.smallOne
                        )
                    ) {
                        val progress = level.toFloat() / 100f
                        val color = when {
                            level <= AppConfig.LOW_PELLET_WARNING && progress > 0 ->
                                MaterialTheme.colorScheme.errorContainer
                            level > AppConfig.LOW_PELLET_WARNING &&
                                    level < AppConfig.MEDIUM_PELLET_WARNING ->
                                MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                        VerticalProgress(
                            color = color,
                            progress = {
                                if (progress > 0f) progress else 0f
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun HopperCardPreview() {
    PiFireTheme {
        Surface {
            Row(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PelletsCard(
                    level = 50,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .weight(1f)
                )
                PelletsCard(
                    level = 30,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f)
                )
            }
        }
    }
}