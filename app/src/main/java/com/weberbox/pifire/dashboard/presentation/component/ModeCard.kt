package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.modifier.doublePulseEffect
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.dashboard.presentation.model.RunningMode

@Composable
internal fun ModeCard(
    modifier: Modifier = Modifier,
    mode: String = "Stop",
    recipePaused: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val title by remember(mode) { mutableIntStateOf(RunningMode.from(mode).title) }
    val icon by remember(mode) { mutableStateOf(RunningMode.from(mode).icon) }

    Card(
        modifier = modifier
            .doublePulseEffect(
                enabled = recipePaused,
                targetScale = 1.2f,
                color = MaterialTheme.colorScheme.tertiary,
                shape = MaterialTheme.shapes.large
            )
            .height(100.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small),
        onClick = {
            if (onClick != null) onClick()
        }
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            this@Card.AnimatedVisibility(
                visible = recipePaused,
                enter = fadeEnterTransition(),
                exit = fadeExitTransition()
            ) {
                Icon(
                    modifier = Modifier.padding(top = 5.dp, end = 8.dp),
                    tint = MaterialTheme.colorScheme.tertiary,
                    imageVector = Icons.Filled.Pause,
                    contentDescription = stringResource(R.string.dash_recipe_paused)
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ModeCardPreview() {
    PiFireTheme {
        Surface {
            Column(
                modifier = Modifier.padding(
                    top = 10.dp,
                    bottom = 10.dp,
                    start = 10.dp,
                    end = 10.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val modes = RunningMode.entries.chunked(3)
                modes.forEach { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        chunk.forEach { mode ->
                            ModeCard(
                                mode = mode.name,
                                modifier = Modifier.weight(1f),
                                recipePaused = mode.name == "Recipe",
                                onClick = { }
                            )
                        }
                    }
                }
            }
        }
    }
}