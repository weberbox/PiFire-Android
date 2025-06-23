package com.weberbox.pifire.common.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberbox.pifire.common.presentation.model.PickerWheelTextStyle
import com.weberbox.pifire.common.presentation.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun <T> PickerWheel(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: Int,
    unitsString: String? = null,
    onItemSelected: (Int) -> Unit,
    space: Dp,
    selectedTextStyle: PickerWheelTextStyle,
    unselectedTextStyle: PickerWheelTextStyle,
    extraRow: Int,
    isLooping: Boolean,
    itemToString: (T) -> String,
    longestText: String,
) {
    var localSelectedItem by remember { mutableIntStateOf(selectedItem) }

    val listState = if (isLooping) {
        rememberLazyListState(
            nearestIndexTarget(
                localSelectedItem - extraRow,
                items.size
            )
        )
    } else {
        rememberLazyListState(initialFirstVisibleItemIndex = localSelectedItem)
    }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val selectedTextLineHeightPx = measureTextHeight(selectedTextStyle)
    val selectedTextLineHeightDp = with(density) { selectedTextLineHeightPx.toDp() }

    val unselectedTextLineHeightPx = measureTextHeight(unselectedTextStyle)
    val unselectedTextLineHeightDp = with(density) { unselectedTextLineHeightPx.toDp() }

    val selectedTextLineWidthPx = measureTextWidth(longestText, selectedTextStyle)
    val selectedTextLineWidthDp = with(density) { selectedTextLineWidthPx.toDp() }

    val wheelHeight =
        (unselectedTextLineHeightDp * (extraRow * 2)) + (space * (extraRow * 2 + 2)) +
                selectedTextLineHeightDp

    val maxOffset = with(density) { unselectedTextLineHeightPx + space.toPx() }

    val firstIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }
    val progress = (firstVisibleOffset / maxOffset).coerceIn(0f, 1f)
    val unitsWidthInDp by remember { mutableStateOf(0.dp) }

    val sizeInterpolator = { index: Int ->
        when (index) {
            firstIndex -> transition(
                selectedTextStyle.fontSize.value,
                unselectedTextStyle.fontSize.value,
                progress
            )

            firstIndex + 1 -> transition(
                unselectedTextStyle.fontSize.value,
                selectedTextStyle.fontSize.value,
                progress
            )

            else -> unselectedTextStyle.fontSize.value
        }
    }

    val heightInterpolator = { index: Int ->
        when (index) {
            firstIndex -> transition(selectedTextLineHeightPx, unselectedTextLineHeightPx, progress)
            firstIndex + 1 -> transition(
                unselectedTextLineHeightPx,
                selectedTextLineHeightPx,
                progress
            )

            else -> unselectedTextLineHeightPx
        }
    }

    val colorInterpolator = { index: Int ->
        when (index) {
            firstIndex -> transition(selectedTextStyle.color, unselectedTextStyle.color, progress)
            firstIndex + 1 -> transition(
                unselectedTextStyle.color,
                selectedTextStyle.color,
                progress
            )

            else -> unselectedTextStyle.color
        }
    }

    LaunchedEffect(firstVisibleOffset) {
        val selected = if (isLooping) {
            (firstIndex + if (firstVisibleOffset > maxOffset / 2) extraRow +
                    1 else extraRow) % items.size
        } else {
            (firstIndex + if (firstVisibleOffset > maxOffset / 2) 1 else 0) % items.size
        }
        onItemSelected(selected)
        localSelectedItem = selected
    }

    LaunchedEffect(isScrolling) {
        if (!isScrolling) {
            coroutineScope.launch {
                listState.animateScrollToItem(
                    firstIndex +
                            if (firstVisibleOffset > maxOffset / 2) 1 else 0
                )
            }
        }
    }

    Box(
        modifier = modifier
            .height(wheelHeight)
            .width(selectedTextLineWidthDp)
            .then(
                unitsString?.let {
                    Modifier.offset {
                        IntOffset(
                            x = unitsWidthInDp.roundToPx(),
                            y = 0
                        )
                    }
                } ?: Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.width(selectedTextLineWidthDp),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space),
            contentPadding = PaddingValues(
                top = space,
                bottom = if (isLooping) space else (space * (extraRow + 1)) +
                        (unselectedTextLineHeightDp * extraRow)
            )
        ) {
            if (!isLooping) {
                @Suppress("unused")
                for (x in 1..extraRow) {
                    item {
                        Text(
                            modifier = Modifier
                                .height(unselectedTextLineHeightDp)
                                .fillMaxWidth(),
                            text = " ",
                            color = Color.Transparent,
                            fontSize = unselectedTextStyle.fontSize,
                            fontWeight = FontWeight.Normal,
                            fontFamily = unselectedTextStyle.fontFamily
                        )
                    }
                }
            }

            items(count = if (isLooping) Int.MAX_VALUE else items.size) { index ->
                val itemIndex = index % items.size
                val item = items[itemIndex]
                val countIndex = index % Int.MAX_VALUE
                val isItemSelected = localSelectedItem == itemIndex
                val adjustedIndex = if (isLooping) countIndex - extraRow else itemIndex
                val unitsAlpha = animateFloatAsState(
                    targetValue = if (isItemSelected) 1f else 0f,
                    animationSpec = tween(durationMillis = 400)
                )

                Row(
                    modifier = Modifier.height(with(density) {
                        heightInterpolator(adjustedIndex).toDp()
                    }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = itemToString(item),
                        color = colorInterpolator(adjustedIndex),
                        fontSize = sizeInterpolator(adjustedIndex).sp,
                        fontWeight = if (isItemSelected)
                            selectedTextStyle.fontWeight else unselectedTextStyle.fontWeight,
                        fontFamily = if (isItemSelected)
                            selectedTextStyle.fontFamily else unselectedTextStyle.fontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    unitsString?.let {
                        Text(
                            modifier = Modifier
                                .padding(
                                    bottom = MaterialTheme.spacing.large,
                                    start = MaterialTheme.spacing.extraSmall
                                )
                                .alpha(unitsAlpha.value),
                            text = unitsString,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


private fun transition(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

private fun transition(start: Color, end: Color, fraction: Float): Color {
    return Color(
        start.red + (end.red - start.red) * fraction,
        start.green + (end.green - start.green) * fraction,
        start.blue + (end.blue - start.blue) * fraction,
        start.alpha + (end.alpha - start.alpha) * fraction
    )
}

private fun nearestIndexTarget(target: Int, size: Int): Int {
    val initialIndex = Int.MAX_VALUE / 2
    val upperLimit = (initialIndex / size) * size + target
    val lowerLimit = upperLimit + size
    return if ((initialIndex - upperLimit) <= (lowerLimit - initialIndex)) {
        upperLimit
    } else {
        lowerLimit
    }
}

@Composable
internal fun measureTextHeight(
    textStyle: PickerWheelTextStyle,
): Float {
    val textMeasurer = rememberTextMeasurer()

    val layoutResult = textMeasurer.measure(
        text = " \n ",
        style = textStyle.toTextStyle()
    )

    return (layoutResult.size.height * 0.6).toFloat()
}

@Composable
internal fun measureTextWidth(
    text: String,
    textStyle: PickerWheelTextStyle,
): Float {
    val textMeasurer = rememberTextMeasurer()

    val layoutResult = textMeasurer.measure(
        text = "$text ",
        style = textStyle.toTextStyle()
    )
    return layoutResult.size.width.toFloat()
}