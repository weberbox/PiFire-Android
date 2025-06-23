package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.outlined.CarDefrostRear
import com.weberbox.pifire.common.icons.outlined.FlagCheckered
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.component.AnimatedCounter
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.util.formatEta
import com.weberbox.pifire.dashboard.presentation.util.formatTemp
import com.weberbox.pifire.dashboard.presentation.util.formatTempWithUnits
import com.weberbox.pifire.dashboard.presentation.util.getBatteryIcon

@Composable
internal fun FoodProbe(
    probe: Probe,
    tempUnits: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
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
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(end = MaterialTheme.spacing.extraSmallOne)
                            .weight(1f),
                        text = if (probe.status.error)
                            stringResource(R.string.error) else probe.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                    this@Card.AnimatedVisibility(
                        visible = probe.hasNotifications,
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraExtraSmall,
                                end = MaterialTheme.spacing.extraSmall
                            )
                        )
                    }
                    this@Card.AnimatedVisibility(
                        visible = probe.targetKeepWarm,
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        Icon(
                            imageVector = Icon.Outlined.CarDefrostRear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraExtraSmall,
                                end = MaterialTheme.spacing.extraSmall
                            )
                        )
                    }
                    this@Card.AnimatedVisibility(
                        visible = probe.targetShutdown,
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        Icon(
                            imageVector = Icon.Outlined.FlagCheckered,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraExtraSmall
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formatEta(probe.eta),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = MaterialTheme.spacing.extraSmallOne)
                            )
                            if (probe.status.batteryCharging) {
                                Icon(
                                    imageVector = Icons.Outlined.BatteryChargingFull,
                                    contentDescription = null,
                                    modifier = Modifier.padding(
                                        top = MaterialTheme.spacing.extraSmallOne,
                                        end = MaterialTheme.spacing.extraSmall
                                    )
                                )
                            } else {
                                if (probe.status.batteryPercentage > 0) {
                                    Icon(
                                        imageVector = getBatteryIcon(probe.status.batteryPercentage),
                                        contentDescription = null,
                                        modifier = Modifier.padding(
                                            top = MaterialTheme.spacing.extraSmallOne,
                                            end = MaterialTheme.spacing.extraSmall
                                        )
                                    )
                                }
                            }
                            if (probe.status.wireless) {
                                Icon(
                                    imageVector = if (probe.status.connected)
                                        Icons.Outlined.Bluetooth else
                                        Icons.Outlined.BluetoothDisabled,
                                    contentDescription = null,
                                    modifier = Modifier.padding(
                                        top = MaterialTheme.spacing.extraSmallOne,
                                        end = MaterialTheme.spacing.smallOne
                                    )
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            AnimatedCounter(
                                count = formatTemp(probe.temp),
                                style = MaterialTheme.typography.displaySmall
                            )
                            Spacer(
                                modifier = Modifier.weight(1f)
                            )
                            Column(
                                modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = stringResource(R.string.dash_state_set_to),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatTempWithUnits(probe.target, tempUnits),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne),
                    ) {
                        val progress = if (probe.target > 0f) {
                            probe.temp.toFloat() / probe.target.toFloat()
                        } else {
                            probe.temp.toFloat() / probe.maxTemp.toFloat()
                        }
                        VerticalProgress(
                            progress = {
                                if (progress > 0f) progress else 0.0001f
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
private fun FoodProbePreview() {
    PiFireTheme {
        Surface {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FoodProbe(
                    probe = buildFoodProbe(),
                    tempUnits = "F",
                    modifier = Modifier
                        .padding(start = 10.dp, end = 5.dp)
                        .weight(1f)
                )
                FoodProbe(
                    probe = buildFoodProbe(),
                    tempUnits = "F",
                    modifier = Modifier
                        .padding(end = 10.dp, start = 5.dp)
                        .weight(1f)
                )
            }
        }
    }
}

private fun buildFoodProbe(): Probe {
    return Probe(
        title = "Probe 1",
        eta = 225,
        temp = 135,
        target = 200,
        maxTemp = 300,
        hasNotifications = true,
        targetKeepWarm = false,
        targetShutdown = true,
        status = Probe.Status(
            batteryCharging = false,
            batteryPercentage = 80,
            connected = true,
            wireless = true,
            error = false
        )
    )
}