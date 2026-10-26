package com.weberbox.pifire.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.weberbox.pifire.core.ui.model.PickerWheelTextStyle
import com.weberbox.pifire.core.ui.theme.spacing
import kotlinx.coroutines.flow.filter
import kotlin.math.roundToInt

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
    // Nothing to select, and the index math below divides by items.size
    if (items.isEmpty()) return

    var localSelectedItem by remember(selectedItem) { mutableIntStateOf(selectedItem) }

    val listState = if (isLooping) {
        rememberLazyListState(
            nearestIndexTarget(
                localSelectedItem - extraRow,
                items.size,
            ),
        )
    } else {
        rememberLazyListState(initialFirstVisibleItemIndex = localSelectedItem)
    }

    val density = LocalDensity.current

    val selectedTextLineHeightPx = measureTextHeight(selectedTextStyle)
    val unselectedTextLineHeightPx = measureTextHeight(unselectedTextStyle)
    val unselectedTextLineHeightDp = with(density) { unselectedTextLineHeightPx.toDp() }
    val selectedTextLineWidthDp = with(density) { measureTextWidth(longestText, selectedTextStyle).toDp() }

    val wheelHeight =
        (unselectedTextLineHeightDp * (extraRow * 2)) + (space * (extraRow * 2 + 2)) +
            with(density) { selectedTextLineHeightPx.toDp() }

    val maxOffset = with(density) { unselectedTextLineHeightPx + space.toPx() }

    // The scroll position changes every frame, so it's only read in the layout and draw phases
    // below; composition reruns only when the selected item changes.
    fun progress(): Float = (listState.firstVisibleItemScrollOffset / maxOffset).coerceIn(0f, 1f)

    fun valueAt(index: Int, selected: Float, unselected: Float): Float {
        val first = listState.firstVisibleItemIndex
        return when (index) {
            first -> transition(selected, unselected, progress())
            first + 1 -> transition(unselected, selected, progress())
            else -> unselected
        }
    }

    fun colorAt(index: Int): Color {
        val first = listState.firstVisibleItemIndex
        return when (index) {
            first -> transition(selectedTextStyle.color, unselectedTextStyle.color, progress())
            first + 1 -> transition(unselectedTextStyle.color, selectedTextStyle.color, progress())
            else -> unselectedTextStyle.color
        }
    }

    val selectedIndex by remember(listState, isLooping, extraRow, items.size, maxOffset) {
        derivedStateOf {
            val pastHalf = if (listState.firstVisibleItemScrollOffset > maxOffset / 2) 1 else 0
            (listState.firstVisibleItemIndex + pastHalf + if (isLooping) extraRow else 0) % items.size
        }
    }

    LaunchedEffect(selectedIndex) {
        onItemSelected(selectedIndex)
        localSelectedItem = selectedIndex
    }

    // Snap to the nearest item when a scroll ends
    LaunchedEffect(listState, maxOffset) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect {
                val pastHalf = if (listState.firstVisibleItemScrollOffset > maxOffset / 2) 1 else 0
                listState.animateScrollToItem(listState.firstVisibleItemIndex + pastHalf)
            }
    }

    val selectedFontSize = selectedTextStyle.fontSize.value
    val unselectedFontSize = unselectedTextStyle.fontSize.value

    Box(
        modifier = modifier
            .height(wheelHeight)
            .width(selectedTextLineWidthDp),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            modifier = Modifier.width(selectedTextLineWidthDp),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space),
            contentPadding = PaddingValues(
                top = space,
                bottom = if (isLooping) {
                    space
                } else {
                    (space * (extraRow + 1)) +
                        (unselectedTextLineHeightDp * extraRow)
                },
            ),
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
                            fontFamily = unselectedTextStyle.fontFamily,
                        )
                    }
                }
            }

            items(count = if (isLooping) Int.MAX_VALUE else items.size) { index ->
                val itemIndex = index % items.size
                val item = items[itemIndex]
                val isItemSelected = localSelectedItem == itemIndex
                val adjustedIndex = if (isLooping) index - extraRow else itemIndex
                val unitsAlpha = animateFloatAsState(
                    targetValue = if (isItemSelected) 1f else 0f,
                    animationSpec = tween(durationMillis = 400),
                )

                Row(
                    modifier = Modifier.layout { measurable, constraints ->
                        val height = valueAt(
                            index = adjustedIndex,
                            selected = selectedTextLineHeightPx,
                            unselected = unselectedTextLineHeightPx,
                        ).roundToInt()
                        val placeable = measurable.measure(
                            constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity),
                        )
                        layout(placeable.width, height) {
                            placeable.place(x = 0, y = (height - placeable.height) / 2)
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Drawn at the selected size and scaled down, so a scroll only redraws it
                    BasicText(
                        text = itemToString(item),
                        modifier = Modifier.graphicsLayer {
                            val scale = valueAt(
                                index = adjustedIndex,
                                selected = selectedFontSize,
                                unselected = unselectedFontSize,
                            ) / selectedFontSize
                            scaleX = scale
                            scaleY = scale
                        },
                        style = TextStyle(
                            fontSize = selectedTextStyle.fontSize,
                            fontWeight = if (isItemSelected) {
                                selectedTextStyle.fontWeight
                            } else {
                                unselectedTextStyle.fontWeight
                            },
                            fontFamily = if (isItemSelected) {
                                selectedTextStyle.fontFamily
                            } else {
                                unselectedTextStyle.fontFamily
                            },
                        ),
                        color = { colorAt(adjustedIndex) },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    unitsString?.let {
                        Text(
                            modifier = Modifier
                                .padding(
                                    bottom = MaterialTheme.spacing.large,
                                    start = MaterialTheme.spacing.extraSmall,
                                )
                                .alpha(unitsAlpha.value),
                            text = unitsString,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

private fun transition(start: Float, end: Float, fraction: Float): Float = start + (end - start) * fraction

private fun transition(start: Color, end: Color, fraction: Float): Color = Color(
    start.red + (end.red - start.red) * fraction,
    start.green + (end.green - start.green) * fraction,
    start.blue + (end.blue - start.blue) * fraction,
    start.alpha + (end.alpha - start.alpha) * fraction,
)

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
        style = textStyle.toTextStyle(),
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
        style = textStyle.toTextStyle(),
    )
    return layoutResult.size.width.toFloat()
}
