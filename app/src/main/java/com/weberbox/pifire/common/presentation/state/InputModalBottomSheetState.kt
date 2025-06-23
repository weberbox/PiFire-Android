package com.weberbox.pifire.common.presentation.state

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheetState
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import com.weberbox.pifire.core.annotations.RequiresSerialization
import kotlinx.serialization.json.Json

@RequiresSerialization
class InputModalBottomSheetState<T : Any>(
    val sheetState: ModalBottomSheetState,
    data: T? = null
) {
    val dataState = mutableStateOf(data)

    internal val data: T
        get() = dataState.value!!

    fun open(data: T) {
        dataState.value = data
        sheetState.targetDetent = SheetDetent.FullyExpanded
    }

    fun close() {
        sheetState.targetDetent = SheetDetent.Hidden
    }

    companion object {
        val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
        }
        inline fun <reified T : Any> Saver(sheetState: ModalBottomSheetState):
                Saver<InputModalBottomSheetState<T>, String> {
            return Saver(
                save = { json.encodeToString(it.dataState.value) },
                restore = {
                    try {
                        InputModalBottomSheetState(sheetState, json.decodeFromString<T>(it))
                    } catch (_: Exception) {
                        InputModalBottomSheetState(sheetState, null)
                    }
                }
            )
        }
    }
}

@RequiresSerialization
@Composable
inline fun <reified T : Any> rememberInputModalBottomSheetState(
    initialDetent: SheetDetent = SheetDetent.Hidden,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween(),
    noinline velocityThreshold: () -> Dp = { 125.dp },
    noinline positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
    noinline confirmDetentChange: (SheetDetent) -> Boolean = { true },
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay()
): InputModalBottomSheetState<T> {
    val sheetState = rememberModalBottomSheetState(
        initialDetent = initialDetent,
        detents = detents,
        animationSpec = animationSpec,
        velocityThreshold = velocityThreshold,
        positionalThreshold = positionalThreshold,
        confirmDetentChange = confirmDetentChange,
        decayAnimationSpec = decayAnimationSpec
    )
    return rememberSaveable(saver = InputModalBottomSheetState.Saver(sheetState)) {
        InputModalBottomSheetState(sheetState)
    }
}