package com.weberbox.pifire.common.presentation.state

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheetState
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState

class CustomModalBottomSheetState(
    val sheetState: ModalBottomSheetState
) {
    fun open() {
        sheetState.targetDetent = SheetDetent.FullyExpanded
    }

    fun close() {
        sheetState.targetDetent = SheetDetent.Hidden
    }
}

@SuppressLint("RememberSaveableSaverParameter")
@Composable
fun rememberCustomModalBottomSheetState(
    initialDetent: SheetDetent = SheetDetent.Hidden,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween(),
    velocityThreshold: () -> Dp = { 125.dp },
    positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
    confirmDetentChange: (SheetDetent) -> Boolean = { true },
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay()
): CustomModalBottomSheetState {
    val sheetState = rememberModalBottomSheetState(
        initialDetent = initialDetent,
        detents = detents,
        animationSpec = animationSpec,
        velocityThreshold = velocityThreshold,
        positionalThreshold = positionalThreshold,
        confirmDetentChange = confirmDetentChange,
        decayAnimationSpec = decayAnimationSpec
    )
    return remember {
        CustomModalBottomSheetState(sheetState)
    }
}