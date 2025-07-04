package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.outlined.FlagCheckered
import com.weberbox.pifire.common.icons.outlined.CarDefrostRear
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.component.AnimatedCounter
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition

@Composable
internal fun TimerCard(
    time: String,
    keepWarm: Boolean,
    shutdown: Boolean,
    paused: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .height(MaterialTheme.size.extraLargeIcon)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small),
        onClick = {
            if (onClick != null) onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                .fillMaxSize()
                .padding(start = MaterialTheme.spacing.smallTwo),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.smallOne)
            ) {
                Text(
                    text = stringResource(R.string.dash_timer_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne)
                ) {
                    this@Card.AnimatedVisibility(
                        visible = keepWarm,
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        Icon(
                            imageVector = Icon.Outlined.CarDefrostRear,
                            contentDescription = stringResource(R.string.dash_keep_warm_timer),
                            tint = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraExtraSmall,
                                end = MaterialTheme.spacing.extraSmall
                            )
                        )
                    }
                    this@Card.AnimatedVisibility(
                        visible = shutdown,
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        Icon(
                            imageVector = Icon.Outlined.FlagCheckered,
                            contentDescription = stringResource(R.string.dash_shutdown_timer),
                            tint = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraExtraSmall,
                                end = MaterialTheme.spacing.extraSmall
                            )
                        )
                    }
                    this@Card.AnimatedVisibility(
                        visible = paused,
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.errorContainer,
                            imageVector = Icons.Filled.Pause,
                            contentDescription = stringResource(R.string.dash_recipe_paused),
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraExtraSmall
                            )
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = MaterialTheme.spacing.smallOne),
                verticalArrangement = Arrangement.Bottom
            ) {
                AnimatedCounter(
                    count = time,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmallOne)
                )
                Text(
                    text = stringResource(R.string.dash_remaining_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun TimerCardPreview() {
    PiFireTheme {
        Surface {
            Row(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimerCard(
                    time = "00:00",
                    keepWarm = false,
                    shutdown = true,
                    paused = true,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .weight(1f)
                )
                TimerCard(
                    time = "00:00",
                    keepWarm = true,
                    shutdown = false,
                    paused = false,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f)
                )
            }
        }
    }
}