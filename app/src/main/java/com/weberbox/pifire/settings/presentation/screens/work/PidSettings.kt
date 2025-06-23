package com.weberbox.pifire.settings.presentation.screens.work

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.sheets.ValidationOptions
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.settings.presentation.component.PreferenceInfo
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.component.getSummaryPercent
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.contract.WorkContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.util.arrayToHashMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun PidSettingsDestination(
    navController: NavHostController,
    viewModel: WorkSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        PidSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is WorkContract.Effect.Navigation.Back -> navController.popBackStack()
                    is WorkContract.Effect.Navigation.NavRoute -> {
                        navController.safeNavigate(
                            route = navigationEffect.route,
                            popUp = navigationEffect.popUp
                        )
                    }
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PidSettings(
    state: WorkContract.State,
    effectFlow: Flow<WorkContract.Effect>?,
    onEventSent: (event: WorkContract.Event) -> Unit,
    onNavigationRequested: (WorkContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is WorkContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is WorkContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_cat_cntrlr_pid),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(WorkContract.Effect.Navigation.Back) }
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        AnimatedContent(
            targetState = state,
            contentKey = { it.isInitialLoading or it.isDataError }
        ) { state ->
            when {
                state.isInitialLoading -> InitialLoadingProgress()
                state.isDataError -> DataError {
                    onNavigationRequested(WorkContract.Effect.Navigation.Back)
                }

                else -> {
                    PidSettingsContent(
                        state = state,
                        onEventSent = onEventSent,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun PidSettingsContent(
    state: WorkContract.State,
    onEventSent: (event: WorkContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val pidPbSheet = rememberCustomModalBottomSheetState()
    val pidTdSheet = rememberCustomModalBottomSheetState()
    val pidTiSheet = rememberCustomModalBottomSheetState()
    val pidCenterSheet = rememberCustomModalBottomSheetState()
    val pidStableSheet = rememberCustomModalBottomSheetState()
    val pidTauSheet = rememberCustomModalBottomSheetState()
    val pidThetaSheet = rememberCustomModalBottomSheetState()
    val holdCycleTimeSheet = rememberCustomModalBottomSheetState()
    val uMinSheet = rememberCustomModalBottomSheetState()
    val uMaxSheet = rememberCustomModalBottomSheetState()
    val pidSelections = arrayToHashMap(
        context.resources.getStringArray(R.array.controller_config_entries),
        context.resources.getStringArray(R.array.controller_config_values)
    )
    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_cntrlr_config)) },
        )
        ListPreference(
            value = state.serverData.settings.cntrlrSelected,
            values = pidSelections.keys.toList().sorted(),
            onValueChange = {
                onEventSent(
                    WorkContract.Event.SetCntrlrSelected(
                        pidSelections.getOrDefault(it, ServerConstants.CNTRLR_PID)
                    )
                )
            },
            title = { Text(text = stringResource(R.string.settings_cntrlr_title)) },
            summary = {
                Text(
                    text = pidSelections.filterValues {
                        it == state.serverData.settings.cntrlrSelected
                    }.keys.joinToString("")
                )
            },
            type = ListPreferenceType.DROPDOWN_MENU
        )
        AnimatedVisibility(
            visible = state.serverData.settings.cntrlrSelected == ServerConstants.CNTRLR_PID
        ) {
            Column {
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_pb)) },
                    summary = {
                        Text(
                            text = getSummary(state.serverData.settings.cntrlrPidPb.toString())
                        )
                    },
                    onClick = { pidPbSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_pb_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_td)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidTd.toString()
                            )
                        )
                    },
                    onClick = { pidTdSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_td_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_ti)) },
                    summary = {
                        Text(
                            text = getSummary(state.serverData.settings.cntrlrPidTi.toString())
                        )
                    },
                    onClick = { pidTiSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_ti_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_center)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidCenter.toString()
                            )
                        )
                    },
                    onClick = { pidCenterSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_center_note_standard))
            }
        }
        AnimatedVisibility(
            visible = state.serverData.settings.cntrlrSelected == ServerConstants.CNTRLR_PID_AC
        ) {
            Column {
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_pb)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidAcPb.toString()
                            )
                        )
                    },
                    onClick = { pidPbSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_pb_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_td)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidAcTd.toString()
                            )
                        )
                    },
                    onClick = { pidTdSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_td_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_ti)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidAcTi.toString()
                            )
                        )
                    },
                    onClick = { pidTiSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_ti_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_sw)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidAcStable.toString()
                            )
                        )
                    },
                    onClick = { pidStableSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_sw_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_center_factor)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidAcCenter.toString()
                            )
                        )
                    },
                    onClick = { pidCenterSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_center_note_auto))
            }
        }
        AnimatedVisibility(
            visible = state.serverData.settings.cntrlrSelected == ServerConstants.CNTRLR_PID_SP
        ) {
            Column {
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_pb)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidSpPb.toString()
                            )
                        )
                    },
                    onClick = { pidPbSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_pb_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_td)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidSpTd.toString()
                            )
                        )
                    },
                    onClick = { pidTdSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_td_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_sw)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidSpStable.toString()
                            )
                        )
                    },
                    onClick = { pidStableSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_sw_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_center_factor)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidSpCenter.toString()
                            )
                        )
                    },
                    onClick = { pidCenterSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_center_note_standard))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_tau)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidSpTau.toString()
                            )
                        )
                    },
                    onClick = { pidTauSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_tau_note))
                Preference(
                    title = { Text(text = stringResource(R.string.settings_pid_cntrlr_theta)) },
                    summary = {
                        Text(
                            text = getSummary(
                                state.serverData.settings.cntrlrPidSpTheta.toString()
                            )
                        )
                    },
                    onClick = { pidThetaSheet.open() }
                )
                PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_theta_note))
            }
        }
        AnimatedVisibility(
            visible = state.serverData.settings.cntrlrSelected != ServerConstants.CNTRLR_PID &&
                    state.serverData.settings.cntrlrSelected != ServerConstants.CNTRLR_PID_AC &&
                    state.serverData.settings.cntrlrSelected != ServerConstants.CNTRLR_PID_SP
        ) {
            PreferenceNote(stringResource(R.string.settings_cycle_cntrlr_no_config))
        }
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_cntrlr_cycle)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_cycle_cntrlr_cycle)) },
            summary = {
                Text(text = getSummarySeconds(state.serverData.settings.holdCycleTime.toString()))
            },
            onClick = { holdCycleTimeSheet.open() }
        )
        PreferenceInfo(stringResource(R.string.settings_cycle_cntrlr_cycle_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_cycle_cntrlr_u_min)) },
            summary = {
                Text(text = getSummaryPercent(state.serverData.settings.uMin.toString()))
            },
            onClick = {uMinSheet.open() }
        )
        PreferenceInfo(stringResource(R.string.settings_cycle_cntrlr_u_min_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_cycle_cntrlr_u_max)) },
            summary = {
                Text(text = getSummaryPercent(state.serverData.settings.uMax.toString()))
            },
            onClick = { uMaxSheet.open() }
        )
        PreferenceInfo(stringResource(R.string.settings_cycle_cntrlr_u_max_note))
    }
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

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PidSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                PidSettings(
                    state = WorkContract.State(
                        serverData = Server(),
                        isInitialLoading = false,
                        isLoading = true,
                        isDataError = false
                    ),
                    effectFlow = null,
                    onEventSent = {},
                    onNavigationRequested = {}
                )
            }
        }
    }
}