package com.weberbox.pifire.dashboard.presentation.sheets

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.model.PostDto.NotifyDto
import com.weberbox.pifire.common.presentation.base.pickerSelectedTextStyle
import com.weberbox.pifire.common.presentation.base.pickerUnselectedTextStyle
import com.weberbox.pifire.common.presentation.component.PickerMark
import com.weberbox.pifire.common.presentation.component.PickerWheel
import com.weberbox.pifire.common.presentation.model.PickerWheelTextStyle
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.findLongestString
import com.weberbox.pifire.common.presentation.util.slideDownShrinkExitTransition
import com.weberbox.pifire.common.presentation.util.slideUpExpandEnterTransition
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.model.NotifType
import com.weberbox.pifire.dashboard.presentation.model.ProbeType
import com.weberbox.pifire.dashboard.presentation.util.findClosestTemp
import com.weberbox.pifire.dashboard.presentation.util.getDefaultProbeTemp
import com.weberbox.pifire.dashboard.presentation.util.getProbeTempRange

@Composable
internal fun ProbePickerSheet(
    probeData: Probe,
    units: String,
    increment: Boolean = false,
    selectedTextStyle: PickerWheelTextStyle = pickerSelectedTextStyle(fontSize = 80.sp),
    unselectedTextStyle: PickerWheelTextStyle = pickerUnselectedTextStyle(fontSize = 40.sp),
    isLooping: Boolean = true,
    extraRows: Int = 1,
    itemSpace: Dp = MaterialTheme.spacing.mediumOne,
    onConfirm: (NotifyDto) -> Unit
) {
    val temps by remember {
        mutableStateOf(
            getProbeTempRange(probeData.probeType, units, probeData.maxTemp, increment)
        )
    }
    var targetTemp by remember {
        mutableIntStateOf(
            if (probeData.target == 0) {
                getDefaultProbeTemp(probeData.probeType, units)
            } else {
                probeData.target
            }
        )
    }
    var highLimitTemp by remember {
        mutableIntStateOf(
            if (probeData.highLimitTemp == 0) {
                getDefaultProbeTemp(probeData.probeType, units)
            } else {
                probeData.highLimitTemp
            }
        )
    }
    var lowLimitTemp by remember {
        mutableIntStateOf(
            if (probeData.lowLimitTemp == 0) {
                getDefaultProbeTemp(probeData.probeType, units)
            } else {
                probeData.lowLimitTemp
            }
        )
    }
    var currentSelection by remember { mutableStateOf(NotifType.Target) }
    var targetReq by remember { mutableStateOf(probeData.targetReq) }
    var highLimitReq by remember { mutableStateOf(probeData.highLimitReq) }
    var lowLimitReq by remember { mutableStateOf(probeData.lowLimitReq) }
    var targetVisible by remember { mutableStateOf(NotifType.Target.defaultVisibility) }
    var highTempVisible by remember { mutableStateOf(NotifType.HighTemp.defaultVisibility) }
    var lowTempVisible by remember { mutableStateOf(NotifType.LowTemp.defaultVisibility) }
    var targetOptions by remember { mutableStateOf(false) }
    var highTempOptions by remember { mutableStateOf(false) }
    var lowTempOptions by remember { mutableStateOf(false) }
    var targetShutdown by remember { mutableStateOf(probeData.targetShutdown) }
    var targetKeepWarm by remember { mutableStateOf(probeData.targetKeepWarm) }
    var highLimitShutdown by remember { mutableStateOf(probeData.highLimitShutdown) }
    var lowLimitShutdown by remember { mutableStateOf(probeData.lowLimitShutdown) }
    var lowLimitReignite by remember { mutableStateOf(probeData.lowLimitReignite) }

    Column(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.mediumOne,
                end = MaterialTheme.spacing.mediumOne,
                bottom = MaterialTheme.spacing.smallThree
            )
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NotifType.entries.forEach { item ->
            AnimatedVisibility(
                visible = when (item) {
                    NotifType.Target -> targetVisible
                    NotifType.HighTemp -> highTempVisible
                    NotifType.LowTemp -> lowTempVisible
                },
                enter = slideUpExpandEnterTransition(),
                exit = slideDownShrinkExitTransition()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.extraSmall),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        PickerMark(
                            modifier = Modifier.align(Alignment.CenterStart),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        PickerWheel(
                            items = temps,
                            selectedItem = when (item) {
                                NotifType.Target -> {
                                    temps.indexOfFirst {
                                        it == findClosestTemp(
                                            list = temps,
                                            target = targetTemp
                                        )
                                    }.takeIf { it >= 0 } ?: 0
                                }

                                NotifType.HighTemp -> {
                                    temps.indexOfFirst {
                                        it == findClosestTemp(
                                            list = temps,
                                            target = highLimitTemp
                                        )
                                    }.takeIf { it >= 0 } ?: 0
                                }

                                NotifType.LowTemp -> {
                                    temps.indexOfFirst {
                                        it == findClosestTemp(
                                            list = temps,
                                            target = lowLimitTemp
                                        )
                                    }.takeIf { it >= 0 } ?: 0
                                }
                            },
                            unitsString = units,
                            onItemSelected = {
                                val temp = temps[it]
                                when (item) {
                                    NotifType.Target -> {
                                        if (temp != targetTemp) targetReq = true
                                        targetTemp = temp
                                    }

                                    NotifType.HighTemp -> {
                                        if (temp != highLimitTemp) highLimitReq = true
                                        highLimitTemp = temp
                                    }

                                    NotifType.LowTemp -> {
                                        if (temp != lowLimitTemp) lowLimitReq = true
                                        lowLimitTemp = temp
                                    }
                                }
                            },
                            space = itemSpace,
                            selectedTextStyle = selectedTextStyle,
                            unselectedTextStyle = unselectedTextStyle,
                            extraRow = extraRows,
                            isLooping = isLooping,
                            itemToString = { it.toString() },
                            longestText = findLongestString(temps)
                        )
                        PickerMark(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                    AnimatedVisibility(
                        visible = when (item) {
                            NotifType.Target -> targetOptions
                            NotifType.HighTemp -> highTempOptions
                            NotifType.LowTemp -> lowTempOptions
                        },
                        enter = slideUpExpandEnterTransition(),
                        exit = slideDownShrinkExitTransition()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.spacing.extraSmall)
                                .offset(y = 2.5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when (item) {
                                NotifType.Target -> {
                                    when (probeData.probeType) {
                                        ProbeType.Food -> {
                                            OptionSwitch(
                                                title = stringResource(R.string.dash_shutdown_temp),
                                                enabled = !targetKeepWarm,
                                                checked = targetShutdown,
                                                onCheckedChange = { targetShutdown = it }
                                            )
                                            OptionSwitch(
                                                title = stringResource(R.string.dash_keep_warm_temp),
                                                enabled = !targetShutdown,
                                                checked = targetKeepWarm,
                                                onCheckedChange = { targetKeepWarm = it }
                                            )
                                        }

                                        else -> {}
                                    }
                                }

                                NotifType.HighTemp -> {
                                    when (probeData.probeType) {
                                        ProbeType.Primary -> {
                                            OptionSwitch(
                                                title = stringResource(R.string.dash_shutdown_temp),
                                                enabled = true,
                                                checked = highLimitShutdown,
                                                onCheckedChange = { highLimitShutdown = it }
                                            )
                                        }

                                        else -> {}
                                    }
                                }

                                NotifType.LowTemp ->
                                    when (probeData.probeType) {
                                        ProbeType.Primary -> {
                                            OptionSwitch(
                                                title = stringResource(R.string.dash_shutdown_temp),
                                                enabled = !lowLimitReignite,
                                                checked = lowLimitShutdown,
                                                onCheckedChange = { lowLimitShutdown = it }
                                            )
                                            OptionSwitch(
                                                title = stringResource(R.string.dash_reignite_temp),
                                                enabled = !lowLimitShutdown,
                                                checked = lowLimitReignite,
                                                onCheckedChange = { lowLimitReignite = it }
                                            )
                                        }

                                        else -> {}
                                    }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.extraSmall)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(
                        onClick = {
                            currentSelection = item
                            when (item) {
                                NotifType.Target -> {
                                    targetVisible = true
                                    lowTempVisible = false
                                    lowTempOptions = false
                                    highTempVisible = false
                                    highTempOptions = false
                                }

                                NotifType.HighTemp -> {
                                    targetVisible = false
                                    targetOptions = false
                                    lowTempVisible = false
                                    lowTempOptions = false
                                    highTempVisible = true
                                }

                                NotifType.LowTemp -> {
                                    targetVisible = false
                                    targetOptions = false
                                    lowTempVisible = true
                                    highTempVisible = false
                                    highTempOptions = false
                                }
                            }
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement
                    .spacedBy(
                        space = MaterialTheme.spacing.smallOne,
                        alignment = Alignment.Start
                    )
            ) {
                Switch(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.smallOne),
                    checked = when (item) {
                        NotifType.Target -> targetReq
                        NotifType.HighTemp -> highLimitReq
                        NotifType.LowTemp -> lowLimitReq
                    },
                    onCheckedChange = {
                        when (item) {
                            NotifType.Target -> targetReq = !targetReq
                            NotifType.HighTemp -> highLimitReq = !highLimitReq
                            NotifType.LowTemp -> lowLimitReq = !lowLimitReq
                        }
                    }
                )
                Icon(
                    imageVector = item.icon,
                    contentDescription = null
                )
                Text(
                    text = stringResource(item.title),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                enabled = when (currentSelection) {
                    NotifType.Target -> {
                        when (probeData.probeType) {
                            ProbeType.Food -> true
                            else -> false
                        }
                    }

                    else -> {
                        when (probeData.probeType) {
                            ProbeType.Primary -> true
                            else -> false
                        }
                    }
                },
                onClick = {
                    when (currentSelection) {
                        NotifType.Target -> targetOptions = !targetOptions
                        NotifType.HighTemp -> highTempOptions = !highTempOptions
                        NotifType.LowTemp -> lowTempOptions = !lowTempOptions
                    }
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.options),
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedButton(
                onClick = {
                    onConfirm(
                        NotifyDto(
                            label = probeData.label
                        )
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.clear_all),
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = {
                    onConfirm(
                        NotifyDto(
                            label = probeData.label,
                            targetTemp = if (targetReq) targetTemp else null,
                            lowLimitTemp = if (lowLimitReq) lowLimitTemp else null,
                            highLimitTemp = if (highLimitReq) highLimitTemp else null,
                            lowLimitReq = lowLimitReq,
                            highLimitReq = highLimitReq,
                            targetReq = targetReq,
                            highLimitShutdown = highLimitShutdown,
                            lowLimitShutdown = lowLimitShutdown,
                            lowLimitReignite = lowLimitReignite,
                            targetShutdown = targetShutdown,
                            targetKeepWarm = targetKeepWarm
                        )
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.confirm),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OptionSwitch(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.weight(1f))
        Switch(
            enabled = enabled,
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ProbePickerSheetPreview() {
    val probeData by remember { mutableStateOf(Probe()) }
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                ProbePickerSheet(
                    probeData = probeData,
                    units = "F",
                    onConfirm = {},
                )
            }
        }
    }
}