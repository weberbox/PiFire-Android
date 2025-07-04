package com.weberbox.pifire.dashboard.presentation.sheets

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.sp
import com.weberbox.pifire.R
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
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent
import java.util.Locale

@Composable
internal fun TimerPickerSheet(
    shutdown: Boolean,
    keepWarm: Boolean,
    selectedTextStyle: PickerWheelTextStyle = pickerSelectedTextStyle(fontSize = 80.sp),
    unselectedTextStyle: PickerWheelTextStyle = pickerUnselectedTextStyle(fontSize = 40.sp),
    isLooping: Boolean = true,
    extraRows: Int = 1,
    itemSpace: Dp = MaterialTheme.spacing.mediumOne,
    negativeButtonText: String = stringResource(R.string.options),
    positiveButtonText: String = stringResource(R.string.confirm),
    positiveButtonColor: ButtonColors = ButtonDefaults.buttonColors(),
    onConfirm: (DashEvent) -> Unit,
) {
    val hours by remember { mutableStateOf((0..24).toList()) }
    val minutes by remember { mutableStateOf((0..59).toList()) }
    var selectedHours by remember { mutableIntStateOf(0) }
    var selectedMinutes by remember { mutableIntStateOf(0) }
    var optionsVisible by remember { mutableStateOf(false) }
    var timerShutdown by remember { mutableStateOf(shutdown) }
    var timerKeepWarm by remember { mutableStateOf(keepWarm) }
    Column(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.mediumOne,
                end = MaterialTheme.spacing.mediumOne,
                bottom = MaterialTheme.spacing.smallThree,
                top = MaterialTheme.spacing.extraSmall
            )
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraSmall),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    PickerWheel(
                        items = hours,
                        selectedItem = 0,
                        onItemSelected = {
                            selectedHours = hours[it]
                        },
                        space = itemSpace,
                        selectedTextStyle = selectedTextStyle,
                        unselectedTextStyle = unselectedTextStyle,
                        extraRow = extraRows,
                        isLooping = isLooping,
                        itemToString = {
                            String.format(Locale.US, "%02d", it)
                        },
                        longestText = findLongestString(hours)
                    )
                    Text(
                        text = ":",
                        color = selectedTextStyle.color,
                        fontSize = selectedTextStyle.fontSize,
                        fontWeight = selectedTextStyle.fontWeight,
                        fontFamily = selectedTextStyle.fontFamily
                    )

                    PickerWheel(
                        items = minutes,
                        selectedItem = 0,
                        onItemSelected = {
                            selectedMinutes = minutes[it]
                        },
                        space = itemSpace,
                        selectedTextStyle = selectedTextStyle,
                        unselectedTextStyle = unselectedTextStyle,
                        extraRow = extraRows,
                        isLooping = isLooping,
                        itemToString = {
                            String.format(Locale.US, "%02d", it)
                        },
                        longestText = findLongestString(minutes)
                    )
                }

                PickerMark(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
        AnimatedVisibility(
            visible = optionsVisible,
            enter = slideUpExpandEnterTransition(),
            exit = slideDownShrinkExitTransition()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.extraSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.dash_shutdown_timer),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Switch(
                        enabled = !timerKeepWarm,
                        checked = timerShutdown,
                        onCheckedChange = {
                            timerShutdown = !timerShutdown
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.dash_keep_warm_timer),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Switch(
                        enabled = !timerShutdown,
                        checked = timerKeepWarm,
                        onCheckedChange = {
                            timerKeepWarm = !timerKeepWarm
                        }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { optionsVisible = !optionsVisible },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = negativeButtonText,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                colors = positiveButtonColor,
                onClick = {
                    onConfirm(
                        DashEvent.TimerTime(
                            hours = selectedHours,
                            minutes = selectedMinutes,
                            shutdown = timerShutdown,
                            keepWarm = timerKeepWarm
                        )
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = positiveButtonText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun TimerPickerSheetPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                TimerPickerSheet(
                    shutdown = true,
                    keepWarm = false,
                    negativeButtonText = stringResource(R.string.options),
                    positiveButtonText = stringResource(R.string.confirm),
                    onConfirm = { }
                )
            }
        }
    }
}