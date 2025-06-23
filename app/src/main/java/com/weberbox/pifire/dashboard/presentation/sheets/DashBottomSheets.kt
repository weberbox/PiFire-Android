package com.weberbox.pifire.dashboard.presentation.sheets

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.state.CustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.state.InputModalBottomSheetState
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.dashboard.presentation.contract.DashContract
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe

@Composable
internal fun DashBottomSheets(
    state: DashContract.State,
    grillSheet: CustomModalBottomSheetState,
    foodSheet: InputModalBottomSheetState<Probe>,
    timerSheet: CustomModalBottomSheetState,
    modeSheet: CustomModalBottomSheetState,
    holdPickerSheet: CustomModalBottomSheetState,
    onEventSent: (event: DashContract.Event) -> Unit,
) {
    val primePickerSheet = rememberCustomModalBottomSheetState()
    BottomSheet(
        sheetState = grillSheet.sheetState,
    ) {
        ProbePickerSheet(
            probeData = state.dash.primaryProbe,
            units = state.dash.tempUnits,
            increment = state.incrementTemps,
            onConfirm = {
                onEventSent(DashContract.Event.ProbeNotify(it))
                grillSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = foodSheet.sheetState
    ) {
        ProbePickerSheet(
            probeData = foodSheet.data,
            units = state.dash.tempUnits,
            increment = state.incrementTemps,
            onConfirm = {
                onEventSent(DashContract.Event.ProbeNotify(it))
                foodSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = timerSheet.sheetState
    ) {
        if (state.dash.timer.timerActive) {
            TimerActiveSheet(
                paused = state.dash.timer.timerPaused,
                onClick = { event ->
                    onEventSent(DashContract.Event.SendEvent(event))
                    timerSheet.close()
                }
            )
        } else {
            TimerPickerSheet(
                shutdown = state.dash.timer.shutdown,
                keepWarm = state.dash.timer.keepWarm,
                onConfirm = { event ->
                    onEventSent(DashContract.Event.SendEvent(event))
                    timerSheet.close()
                }
            )
        }
    }
    BottomSheet(
        sheetState = modeSheet.sheetState
    ) {
        ModeSheet(
            currentMode = state.dash.currentMode,
            recipePaused = state.dash.recipeStatus.paused,
            startupCheck = state.dash.startupCheck,
            onEvent = { event ->
                onEventSent(DashContract.Event.SendEvent(event))
                modeSheet.close()
            },
            onPrime = {
                modeSheet.close()
                primePickerSheet.open()
            },
            onHold = {
                modeSheet.close()
                holdPickerSheet.open()
            }
        )
    }
    BottomSheet(
        sheetState = primePickerSheet.sheetState
    ) {
        PrimePickerSheet(
            itemSpace = MaterialTheme.spacing.mediumOne,
            onConfirm = { primeAmount, nextMode ->
                onEventSent(
                    DashContract.Event.SendEvent(
                        DashEvent.PrimeGrill(
                            primeAmount = primeAmount,
                            nextMode = nextMode
                        )
                    )
                )
                primePickerSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = holdPickerSheet.sheetState
    ) {
        HoldPickerSheet(
            probeData = state.dash.primaryProbe,
            units = state.dash.tempUnits,
            increment = state.incrementTemps,
            onConfirm = { temp ->
                onEventSent(DashContract.Event.SendEvent(DashEvent.GrillHoldTemp(temp)))
                holdPickerSheet.close()
            }
        )
    }
}