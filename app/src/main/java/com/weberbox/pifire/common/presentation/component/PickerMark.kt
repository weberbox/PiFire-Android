package com.weberbox.pifire.common.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
fun PickerMark(
    modifier: Modifier = Modifier,
    width: Dp = MaterialTheme.size.mediumOne,
    height: Dp = MaterialTheme.size.extraSmall,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Surface(
        modifier = modifier.padding(vertical = MaterialTheme.spacing.smallThree),
        color = color,
        shape = shape
    ) {
        Box(Modifier.size(width = width, height = height))
    }
}