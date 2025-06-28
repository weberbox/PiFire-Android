package com.weberbox.pifire.settings.presentation.screens.work

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.sheets.ValidationOptions
import com.weberbox.pifire.common.presentation.state.CustomModalBottomSheetState
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.settings.presentation.contract.WorkContract

@Composable
internal fun PidBottomSheets(
    state: WorkContract.State,
    pidPbSheet: CustomModalBottomSheetState,
    pidTdSheet: CustomModalBottomSheetState,
    pidTiSheet: CustomModalBottomSheetState,
    pidStableSheet: CustomModalBottomSheetState,
    pidCenterSheet: CustomModalBottomSheetState,
    pidTauSheet: CustomModalBottomSheetState,
    pidThetaSheet: CustomModalBottomSheetState,
    holdCycleTimeSheet: CustomModalBottomSheetState,
    uMinSheet: CustomModalBottomSheetState,
    uMaxSheet: CustomModalBottomSheetState,
    onEventSent: (WorkContract.Event) -> Unit,
) {
    BottomSheet(
        sheetState = pidPbSheet.sheetState
    ) {
        InputValidationSheet(
            input = when (state.serverData.settings.cntrlrSelected) {
                ServerConstants.CNTRLR_PID -> {
                    state.serverData.settings.cntrlrPidPb.toString()
                }

                ServerConstants.CNTRLR_PID_AC -> {
                    state.serverData.settings.cntrlrPidAcPb.toString()
                }

                ServerConstants.CNTRLR_PID_SP -> {
                    state.serverData.settings.cntrlrPidSpPb.toString()
                }

                else -> {
                    "Error"
                }
            },
            title = stringResource(R.string.settings_pid_cntrlr_pb),
            placeholder = stringResource(R.string.settings_pid_cntrlr_pb),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal
            ),
            onUpdate = {
                when (state.serverData.settings.cntrlrSelected) {
                    ServerConstants.CNTRLR_PID -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidPb(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_AC -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidAcPb(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_SP -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidSpPb(it.toDouble()))
                    }
                }
                pidPbSheet.close()
            },
            onDismiss = { pidPbSheet.close() }
        )
    }
    BottomSheet(
        sheetState = pidTdSheet.sheetState
    ) {
        InputValidationSheet(
            input = when (state.serverData.settings.cntrlrSelected) {
                ServerConstants.CNTRLR_PID -> {
                    state.serverData.settings.cntrlrPidTd.toString()
                }

                ServerConstants.CNTRLR_PID_AC -> {
                    state.serverData.settings.cntrlrPidAcTd.toString()
                }

                ServerConstants.CNTRLR_PID_SP -> {
                    state.serverData.settings.cntrlrPidSpTd.toString()
                }

                else -> {
                    "Error"
                }
            },
            title = stringResource(R.string.settings_pid_cntrlr_td),
            placeholder = stringResource(R.string.settings_pid_cntrlr_td),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal
            ),
            onUpdate = {
                when (state.serverData.settings.cntrlrSelected) {
                    ServerConstants.CNTRLR_PID -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidTd(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_AC -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidAcTd(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_SP -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidSpTd(it.toDouble()))
                    }
                }
                pidTdSheet.close()
            },
            onDismiss = { pidTdSheet.close() }
        )
    }
    BottomSheet(
        sheetState = pidTiSheet.sheetState
    ) {
        InputValidationSheet(
            input = when (state.serverData.settings.cntrlrSelected) {
                ServerConstants.CNTRLR_PID -> {
                    state.serverData.settings.cntrlrPidTi.toString()
                }

                ServerConstants.CNTRLR_PID_AC -> {
                    state.serverData.settings.cntrlrPidAcTi.toString()
                }

                ServerConstants.CNTRLR_PID_SP -> {
                    state.serverData.settings.cntrlrPidSpTi.toString()
                }

                else -> {
                    "Error"
                }
            },
            title = stringResource(R.string.settings_pid_cntrlr_ti),
            placeholder = stringResource(R.string.settings_pid_cntrlr_ti),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal
            ),
            onUpdate = {
                when (state.serverData.settings.cntrlrSelected) {
                    ServerConstants.CNTRLR_PID -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidTi(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_AC -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidAcTi(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_SP -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidSpTi(it.toDouble()))
                    }
                }
                pidTiSheet.close()
            },
            onDismiss = { pidTiSheet.close() }
        )
    }
    BottomSheet(
        sheetState = pidStableSheet.sheetState
    ) {
        InputValidationSheet(
            input = when (state.serverData.settings.cntrlrSelected) {
                ServerConstants.CNTRLR_PID_AC -> {
                    state.serverData.settings.cntrlrPidAcStable.toString()
                }

                ServerConstants.CNTRLR_PID_SP -> {
                    state.serverData.settings.cntrlrPidSpStable.toString()
                }

                else -> {
                    "Error"
                }
            },
            title = stringResource(R.string.settings_pid_cntrlr_sw),
            placeholder = stringResource(R.string.settings_pid_cntrlr_sw),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal
            ),
            onUpdate = {
                when (state.serverData.settings.cntrlrSelected) {
                    ServerConstants.CNTRLR_PID_AC -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidAcStable(it.toInt()))
                    }

                    ServerConstants.CNTRLR_PID_SP -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidSpStable(it.toInt()))
                    }
                }
                pidStableSheet.close()
            },
            onDismiss = { pidStableSheet.close() }
        )
    }
    BottomSheet(
        sheetState = pidCenterSheet.sheetState
    ) {
        InputValidationSheet(
            input = when (state.serverData.settings.cntrlrSelected) {
                ServerConstants.CNTRLR_PID -> {
                    state.serverData.settings.cntrlrPidCenter.toString()
                }

                ServerConstants.CNTRLR_PID_AC -> {
                    state.serverData.settings.cntrlrPidAcCenter.toString()
                }

                ServerConstants.CNTRLR_PID_SP -> {
                    state.serverData.settings.cntrlrPidSpCenter.toString()
                }

                else -> {
                    "Error"
                }
            },
            title = stringResource(R.string.settings_pid_cntrlr_center),
            placeholder = stringResource(R.string.settings_pid_cntrlr_center),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal
            ),
            onUpdate = {
                when (state.serverData.settings.cntrlrSelected) {
                    ServerConstants.CNTRLR_PID -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidCenter(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_AC -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidAcCenter(it.toDouble()))
                    }

                    ServerConstants.CNTRLR_PID_SP -> {
                        onEventSent(WorkContract.Event.SetCntrlrPidSpCenter(it.toDouble()))
                    }
                }
                pidCenterSheet.close()
            },
            onDismiss = { pidCenterSheet.close() }
        )
    }
    BottomSheet(
        sheetState = pidTauSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.cntrlrPidSpTau.toString(),
            title = stringResource(R.string.settings_pid_cntrlr_tau),
            placeholder = stringResource(R.string.settings_pid_cntrlr_tau),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetCntrlrPidSpTau(it.toInt()))
                pidTauSheet.close()
            },
            onDismiss = { pidTauSheet.close() }
        )
    }
    BottomSheet(
        sheetState = pidThetaSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.cntrlrPidSpTheta.toString(),
            title = stringResource(R.string.settings_pid_cntrlr_theta),
            placeholder = stringResource(R.string.settings_pid_cntrlr_theta),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetCntrlrPidSpTheta(it.toInt()))
                pidThetaSheet.close()
            },
            onDismiss = { pidThetaSheet.close() }
        )
    }
    BottomSheet(
        sheetState = holdCycleTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.holdCycleTime.toString(),
            title = stringResource(R.string.settings_cycle_cntrlr_cycle),
            placeholder = stringResource(R.string.settings_cycle_cntrlr_cycle),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetCntrlrTime(it.toInt()))
                holdCycleTimeSheet.close()
            },
            onDismiss = { holdCycleTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = uMinSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.uMin.toString(),
            title = stringResource(R.string.settings_cycle_cntrlr_u_min),
            placeholder = stringResource(R.string.settings_cycle_cntrlr_u_min),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal,
                min = 0.05,
                max = 0.99
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetCntrlruMin(it.toDouble()))
                uMinSheet.close()
            },
            onDismiss = { uMinSheet.close() }
        )
    }
    BottomSheet(
        sheetState = uMaxSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.uMax.toString(),
            title = stringResource(R.string.settings_cycle_cntrlr_u_max),
            placeholder = stringResource(R.string.settings_cycle_cntrlr_u_max),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal,
                min = 0.1,
                max = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetCntrlruMax(it.toDouble()))
                uMaxSheet.close()
            },
            onDismiss = { uMaxSheet.close() }
        )
    }
}