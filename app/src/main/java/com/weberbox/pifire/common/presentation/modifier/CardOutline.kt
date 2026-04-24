package com.weberbox.pifire.common.presentation.modifier

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.cardOutline(
    enabled: Boolean,
    color: Color = Color.Green.copy(0.56f),
    width: Dp = 1.5.dp,
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier = if (enabled) {
    this.then(
        Modifier.border(
            width = width,
            color = color,
            shape = shape
        )
    )
} else {
    this
}