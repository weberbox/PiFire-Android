package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.weberbox.pifire.common.presentation.theme.SettingsTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.util.formatEta
import com.weberbox.pifire.dashboard.presentation.util.formatSetTemp
import com.weberbox.pifire.dashboard.presentation.util.formatTemp
import com.weberbox.pifire.dashboard.presentation.util.getBatteryIcon

@Composable
internal fun GrillProbe(
    probe: Probe,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.smallTwo)
            .fillMaxWidth()
    ) {
        Card(
            modifier = modifier
                .combinedClickable(
                    onClick = { if (onClick != null) onClick() },
                    onLongClick = { if (onLongClick != null) onLongClick() }
                )
                .height(MaterialTheme.size.extraLargeIcon)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(
                defaultElevation = MaterialTheme.elevation.small
            )
        ) {
            Row(
                modifier = Modifier
                    .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(
                                top = MaterialTheme.spacing.smallOne,
                                start = MaterialTheme.spacing.smallTwo
                            )
                            .fillMaxWidth(),

                        ) {
                        Text(
                            text = probe.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
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
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = formatEta(probe.eta),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    top = MaterialTheme.spacing.extraSmallOne,
                                    start = MaterialTheme.spacing.smallTwo
                                )
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
                                    top = MaterialTheme.spacing.extraSmallOne
                                )
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Column (
                        modifier = Modifier
                            .padding(
                                bottom = MaterialTheme.spacing.smallOne,
                                start = MaterialTheme.spacing.smallTwo
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = stringResource(R.string.dash_state_set_to),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 1.dp)
                            )
                            Text(
                                text = formatSetTemp(probe.setTemp),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(
                                    start = MaterialTheme.spacing.extraSmallOne
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.padding(
                                top = MaterialTheme.spacing.extraSmallOne
                            ),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = stringResource(R.string.dash_state_target),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(bottom = 1.dp)

                            )
                            Text(
                                text = formatSetTemp(probe.target),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(
                                    start = MaterialTheme.spacing.extraSmallOne
                                )
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedCounter(
                        count = formatTemp(probe.temp),
                        style = MaterialTheme.typography.displaySmall
                    )
                    val progress = if (probe.setTemp > 0f) {
                        probe.temp.toFloat() / probe.setTemp.toFloat()
                    } else {
                        probe.temp.toFloat() / probe.maxTemp.toFloat()
                    }
                    CircularProgressIndicator(
                        modifier = Modifier.size(MaterialTheme.size.extraExtraLarge),
                        gapSize = (-10).dp,
                        progress = {
                            if (progress > 0f) progress else 0f
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun GrillProbePreview() {
    SettingsTheme {
        Surface {
            GrillProbe(
                probe = buildGrillProbe(),
                modifier = Modifier.padding(
                    top = 5.dp,
                    bottom = 5.dp
                )
            )
        }
    }
}

private fun buildGrillProbe(): Probe {
    return Probe(
        title = "Grill",
        eta = 1530,
        temp = 135,
        target = 125,
        setTemp = 225,
        maxTemp = 600,
        hasNotifications = true,
        targetKeepWarm = false,
        targetShutdown = true,
        status = Probe.Status(
            batteryCharging = false,
            batteryPercentage = 80,
            connected = true,
            wireless = true
        )
    )
}