package com.weberbox.pifire.common.presentation.modifier

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

fun Modifier.isElementVisible(onVisibilityChanged: (Boolean) -> Unit) = composed {
    val isVisible by remember { derivedStateOf { mutableStateOf(false) } }
    LaunchedEffect(isVisible.value) { onVisibilityChanged.invoke(isVisible.value) }
    this.onGloballyPositioned { layoutCoordinates ->
        isVisible.value = layoutCoordinates.parentLayoutCoordinates?.let {
            val parentBounds = it.boundsInWindow()
            val childBounds = layoutCoordinates.boundsInWindow()
            parentBounds.overlaps(childBounds)
        } == true
    }
}