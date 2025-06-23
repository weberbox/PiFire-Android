package com.weberbox.pifire.common.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.weberbox.pifire.common.presentation.base.pickerSelectedTextStyle
import com.weberbox.pifire.common.presentation.base.pickerUnselectedTextStyle
import com.weberbox.pifire.common.presentation.component.PickerMark
import com.weberbox.pifire.common.presentation.component.PickerWheel
import com.weberbox.pifire.common.presentation.model.PickerWheelTextStyle
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.findLongestString

@Composable
fun <T> CircularPickerSheet(
    items: List<T>,
    initialItem: T,
    unitsString: String? = null,
    selectedTextStyle: PickerWheelTextStyle = pickerSelectedTextStyle(),
    unselectedTextStyle: PickerWheelTextStyle = pickerUnselectedTextStyle(),
    isLooping: Boolean = true,
    extraRows: Int = 1,
    itemSpace: Dp = 50.dp,
    itemToString: (T) -> String,
    negativeButtonText: String = stringResource(R.string.cancel),
    positiveButtonText: String = stringResource(R.string.confirm),
    negativeButtonColor: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    positiveButtonColor: ButtonColors = ButtonDefaults.buttonColors(),
    onNegative: () -> Unit,
    onPositive: (T) -> Unit
) {
    var selectedItem by remember { mutableStateOf(initialItem) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.mediumOne
                    ),
                    items = items,
                    selectedItem = items.indexOfFirst { it == initialItem }.takeIf { it >= 0 } ?: 0,
                    unitsString = unitsString,
                    onItemSelected = {
                        selectedItem = items[it]
                    },
                    space = itemSpace,
                    selectedTextStyle = selectedTextStyle,
                    unselectedTextStyle = unselectedTextStyle,
                    extraRow = extraRows,
                    isLooping = isLooping,
                    itemToString = itemToString,
                    longestText = findLongestString(items)
                )
                PickerMark(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.spacing.mediumOne,
                    end = MaterialTheme.spacing.mediumOne,
                    bottom = MaterialTheme.spacing.smallThree
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                colors = negativeButtonColor,
                onClick = { onNegative() },
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
                onClick = { onPositive(selectedItem) },
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
private fun CircularPickerSheetPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                CircularPickerSheet(
                    items = (0..300).filter { it % 5 == 0 }.toList(),
                    initialItem = 200,
                    selectedTextStyle = pickerSelectedTextStyle(fontSize = 80.sp),
                    unselectedTextStyle = pickerUnselectedTextStyle(),
                    isLooping = true,
                    extraRows = 1,
                    itemSpace = MaterialTheme.spacing.mediumFour,
                    itemToString = { it.toString() },
                    negativeButtonText = stringResource(R.string.cancel),
                    positiveButtonText = stringResource(R.string.confirm),
                    onNegative = {},
                    onPositive = {}
                )
            }
        }
    }
}