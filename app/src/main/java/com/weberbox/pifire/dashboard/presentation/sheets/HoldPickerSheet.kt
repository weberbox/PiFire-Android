package com.weberbox.pifire.dashboard.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.util.findClosestTemp
import com.weberbox.pifire.dashboard.presentation.util.getDefaultProbeTemp
import com.weberbox.pifire.dashboard.presentation.util.getProbeTempRange

@Composable
internal fun HoldPickerSheet(
    probeData: Probe,
    units: String,
    increment: Boolean = false,
    selectedTextStyle: PickerWheelTextStyle = pickerSelectedTextStyle(fontSize = 80.sp),
    unselectedTextStyle: PickerWheelTextStyle = pickerUnselectedTextStyle(fontSize = 40.sp),
    isLooping: Boolean = true,
    extraRows: Int = 1,
    itemSpace: Dp = MaterialTheme.spacing.mediumOne,
    onConfirm: (temp: String) -> Unit,
    onCancel: () -> Unit
) {
    val temps by remember {
        mutableStateOf(
            getProbeTempRange(probeData.probeType, units, probeData.maxTemp, increment)
        )
    }
    var setTemp by remember {
        mutableIntStateOf(
            if (probeData.setTemp == 0) {
                getDefaultProbeTemp(probeData.probeType, units)
            } else {
                probeData.setTemp
            }
        )
    }

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
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .padding(bottom = MaterialTheme.spacing.extraSmall)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            PickerMark(
                modifier = Modifier.align(Alignment.CenterStart),
                color = MaterialTheme.colorScheme.primaryContainer
            )
            PickerWheel(
                items = temps,
                selectedItem = temps.indexOfFirst {
                    it == findClosestTemp(
                        list = temps,
                        target = setTemp
                    )
                }.takeIf { it >= 0 } ?: 0,
                unitsString = units,
                onItemSelected = {
                    setTemp = temps[it]
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { onConfirm(setTemp.toString()) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun HoldPickerSheetPreview() {
    val probeData by remember { mutableStateOf(Probe()) }
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                HoldPickerSheet(
                    probeData = probeData,
                    units = "F",
                    onConfirm = { },
                    onCancel = { }
                )
            }
        }
    }
}