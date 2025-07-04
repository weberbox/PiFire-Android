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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.dashboard.presentation.util.findClosestTemp

@Composable
internal fun PrimePickerSheet(
    selectedTextStyle: PickerWheelTextStyle = pickerSelectedTextStyle(fontSize = 80.sp),
    unselectedTextStyle: PickerWheelTextStyle = pickerUnselectedTextStyle(fontSize = 40.sp),
    isLooping: Boolean = true,
    extraRows: Int = 1,
    itemSpace: Dp = MaterialTheme.spacing.mediumOne,
    onConfirm: (amount: Int, nextMode: String) -> Unit,
) {
    val items by remember {
        mutableStateOf(
            (AppConfig.PRIME_MIN_GRAMS..AppConfig.PRIME_MAX_GRAMS).filter {
                it % 5 == 0
            }.reversed().toList()
        )
    }
    var primeAmount by remember { mutableIntStateOf(AppConfig.PRIME_MIN_GRAMS) }
    var optionsVisible by remember { mutableStateOf(false) }
    var startup by remember { mutableStateOf(false) }

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
                items = items,
                selectedItem = items.indexOfFirst {
                    it == findClosestTemp(
                        list = items,
                        target = primeAmount
                    )
                }.takeIf { it >= 0 } ?: 0,
                unitsString = "G",
                onItemSelected = {
                    primeAmount = items[it]
                },
                space = itemSpace,
                selectedTextStyle = selectedTextStyle,
                unselectedTextStyle = unselectedTextStyle,
                extraRow = extraRows,
                isLooping = isLooping,
                itemToString = { it.toString() },
                longestText = findLongestString(items)
            )
            PickerMark(
                modifier = Modifier.align(Alignment.CenterEnd),
                color = MaterialTheme.colorScheme.primaryContainer
            )
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
                        text = stringResource(R.string.dash_prime_and_startup),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = startup,
                        onCheckedChange = {
                            startup = !startup
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
                onClick = {
                    optionsVisible = !optionsVisible
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
            Button(
                onClick = {
                    onConfirm(
                        primeAmount,
                        if (startup) RunningMode.Startup.name else RunningMode.Stop.name
                    )
                },
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
private fun PrimePickerSheetPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                PrimePickerSheet(
                    onConfirm = { _, _ -> }
                )
            }
        }
    }
}