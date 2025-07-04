package com.weberbox.pifire.common.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AnimatedCounter(
    count: String,
    modifier: Modifier = Modifier,
    reverse: Boolean = false,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight = FontWeight.Bold
) {
    var oldCount by remember {
        mutableStateOf(count)
    }
    SideEffect {
        oldCount = count
    }
    Row(modifier = modifier) {
        val oldCountString = oldCount
        count.indices.forEach { index ->
            val oldChar = oldCountString.getOrNull(index)
            val newChar = count[index]
            val char = if (oldChar == newChar) {
                oldCountString[index]
            } else {
                count[index]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (reverse) slideInVertically { it } togetherWith slideOutVertically { -it }
                    else slideInVertically { -it } togetherWith slideOutVertically { it }
                }
            ) {
                Text(
                    text = it.toString(),
                    style = style,
                    fontWeight = fontWeight,
                    softWrap = false,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}