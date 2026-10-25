package com.weberbox.pifire.core.ui.modifier

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.weberbox.pifire.core.ui.util.WindowSize
import com.weberbox.pifire.core.ui.util.windowWidthSize

@Composable
fun Modifier.limitWidthFraction(
    widthFraction: Float = 0.6f
): Modifier {
    val windowWidthSize = windowWidthSize()
    return if (windowWidthSize == WindowSize.EXPANDED) {
        this.fillMaxWidth(fraction = widthFraction)
    } else this
}