package com.weberbox.pifire.settings.presentation.screens.work

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.sheets.ValidationOptions
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.settings.presentation.component.PreferenceInfo
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.component.getSummaryPercent
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.component.getSummaryTemp
import com.weberbox.pifire.settings.presentation.contract.WorkContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.sheets.PModeSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

@Composable
fun WorkSettingsDestination(
    navController: NavHostController,
    viewModel: WorkSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        WorkSettings(
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
private fun WorkSettings(
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
                        text = stringResource(R.string.settings_work_title),
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
                    WorkSettingsContent(
                        state = state,
                        onEventSent = onEventSent,
                        onNavigationRequested = onNavigationRequested,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkSettingsContent(
    state: WorkContract.State,
    onEventSent: (event: WorkContract.Event) -> Unit,
    onNavigationRequested: (WorkContract.Effect.Navigation) -> Unit,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val onCycleTimeSheet = rememberCustomModalBottomSheetState()
    val offCycleTimeSheet = rememberCustomModalBottomSheetState()
    val pModeSheet = rememberCustomModalBottomSheetState()
    val sPlusDutyCycleSheet = rememberCustomModalBottomSheetState()
    val sPlusOnTimeSheet = rememberCustomModalBottomSheetState()
    val sPlusOffTimeSheet = rememberCustomModalBottomSheetState()
    val sPlusMinTempSheet = rememberCustomModalBottomSheetState()
    val sPlusMaxTempSheet = rememberCustomModalBottomSheetState()
    val lidThreshSheet = rememberCustomModalBottomSheetState()
    val lidPauseTimeSheet = rememberCustomModalBottomSheetState()
    val keepWarmTempSheet = rememberCustomModalBottomSheetState()
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
            title = { Text(text = stringResource(R.string.settings_cat_cntrlr_pid)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pid_cntrlr_title)) },
            summary = { Text(text = stringResource(R.string.settings_pid_cntrlr_summary)) },
            onClick = {
                onNavigationRequested(
                    WorkContract.Effect.Navigation.NavRoute(
                        route = NavGraph.SettingsDest.Pid
                    )
                )
            }
        )
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_cycle)) },
        )
        PreferenceInfo(stringResource(R.string.settings_pmode_info_one))
        Preference(
            title = { Text(text = stringResource(R.string.settings_pmode_auger_on)) },
            summary = {
                Text(
                    text = getSummarySeconds(
                        state.serverData.settings.smokeOnCycleTime.toString()
                    )
                )
            },
            onClick = { onCycleTimeSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pmode_auger_off)) },
            summary = {
                Text(
                    text = getSummarySeconds(
                        state.serverData.settings.smokeOffCycleTime.toString()
                    )
                )
            },
            onClick = { offCycleTimeSheet.open() }
        )
        PreferenceInfo(stringResource(R.string.settings_pmode_info_two))
        ListPreference(
            value = state.serverData.settings.pMode,
            values = (0..9).toList(),
            onValueChange = { onEventSent(WorkContract.Event.SetPMode(it)) },
            title = { Text(text = stringResource(R.string.settings_pmode_mode)) },
            summary = {
                Text(
                    text = stringResource(
                        R.string.settings_pmode,
                        state.serverData.settings.pMode
                    )
                )
            },
            type = ListPreferenceType.DROPDOWN_MENU
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pmode_table)) },
            summary = { Text(text = stringResource(R.string.settings_pmode_summary)) },
            onClick = { pModeSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_pmode_note)) {
            Intent(Intent.ACTION_VIEW).apply {
                data = context.getString(R.string.def_pmode_link).toUri()
                context.startActivity(this)
            }
        }
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_splus)) },
        )
        SwitchPreference(
            value = state.serverData.settings.sPlusEnabled,
            onValueChange = { onEventSent(WorkContract.Event.SetSPlusEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_splus_enabled)) },
            summary = { Text(text = getSummary(state.serverData.settings.sPlusEnabled)) }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_splus_min)) },
            summary = {
                Text(
                    text = getSummaryTemp(
                        state.serverData.settings.sPlusMinTemp.toString(),
                        state.serverData.settings.tempUnits
                    )
                )
            },
            onClick = { sPlusMinTempSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_splus_max)) },
            summary = {
                Text(
                    text = getSummaryTemp(
                        state.serverData.settings.sPlusMaxTemp.toString(),
                        state.serverData.settings.tempUnits
                    )
                )
            },
            onClick = { sPlusMaxTempSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_splus_on_time)) },
            summary = {
                Text(
                    text = getSummarySeconds(
                        state.serverData.settings.sPlusOnTime.toString()
                    )
                )
            },
            onClick = { sPlusOnTimeSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_splus_off_time)) },
            summary = {
                Text(
                    text = getSummarySeconds(
                        state.serverData.settings.sPlusOffTime.toString()
                    )
                )
            },
            onClick = { sPlusOffTimeSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_splus_note))
        if (state.serverData.settings.dcFan) {
            SwitchPreference(
                value = state.serverData.settings.sPlusFanRamp,
                onValueChange = { onEventSent(WorkContract.Event.SetFanRampEnabled(it)) },
                title = { Text(text = stringResource(R.string.settings_splus_fan_ramp)) },
                summary = {
                    Text(
                        text = getSummary(state.serverData.settings.sPlusFanRamp)
                    )
                }
            )
            Preference(
                title = { Text(text = stringResource(R.string.settings_splus_ramp_dc)) },
                summary = {
                    Text(
                        text = getSummaryPercent(
                            state.serverData.settings.sPlusDutyCycle.toString()
                        )
                    )
                },
                onClick = { sPlusDutyCycleSheet.open() }
            )
            PreferenceNote(stringResource(R.string.settings_splus_fan_ramp_note))
        }
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_lid_detection)) },
        )
        SwitchPreference(
            value = state.serverData.settings.lidOpenDetectEnabled,
            onValueChange = { onEventSent(WorkContract.Event.SetLidOpenDetectEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_lid_open_enabled)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.lidOpenDetectEnabled)
                )
            }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_lid_open_thresh)) },
            summary = {
                Text(
                    text = getSummaryPercent(
                        state.serverData.settings.lidOpenThreshold.toString()
                    )
                )
            },
            onClick = { lidThreshSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_lid_open_pause)) },
            summary = {
                Text(
                    text = getSummarySeconds(
                        state.serverData.settings.lidOpenPauseTime.toString()
                    )
                )
            },
            onClick = { lidPauseTimeSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_lid_open_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_keep_warm)) },
        )
        SwitchPreference(
            value = state.serverData.settings.keepWarmSPlus,
            onValueChange = { onEventSent(WorkContract.Event.SetKeepWarmEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_keep_warm_s_plus)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.keepWarmSPlus)
                )
            }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_keep_warm_temp)) },
            summary = {
                Text(
                    text = getSummaryTemp(
                        state.serverData.settings.keepWarmTemp.toString(),
                        state.serverData.settings.tempUnits
                    )
                )
            },
            onClick = { keepWarmTempSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_keep_warm_note))
    }
    BottomSheet(
        sheetState = onCycleTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.smokeOnCycleTime.toString(),
            title = stringResource(R.string.settings_pmode_auger_on),
            placeholder = stringResource(R.string.settings_pmode_auger_on),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetAugerOnTime(it.toInt()))
                onCycleTimeSheet.close()
            },
            onDismiss = { onCycleTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = offCycleTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.smokeOffCycleTime.toString(),
            title = stringResource(R.string.settings_pmode_auger_off),
            placeholder = stringResource(R.string.settings_pmode_auger_off),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetAugerOffTime(it.toInt()))
                offCycleTimeSheet.close()
            },
            onDismiss = { offCycleTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = pModeSheet.sheetState
    ) {
        PModeSheet(
            augerOn = state.serverData.settings.augerRate.toString(),
        )
    }
    BottomSheet(
        sheetState = sPlusDutyCycleSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.sPlusDutyCycle.toString(),
            title = stringResource(R.string.settings_splus_ramp_dc),
            placeholder = stringResource(R.string.settings_splus_ramp_dc),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = state.serverData.settings.pwmMinDutyCycle.toDouble(),
                max = 100.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetFanRampDutyCycle(it.toInt()))
                sPlusDutyCycleSheet.close()
            },
            onDismiss = { sPlusDutyCycleSheet.close() }
        )
    }
    BottomSheet(
        sheetState = sPlusOnTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.sPlusOnTime.toString(),
            title = stringResource(R.string.settings_splus_on_time),
            placeholder = stringResource(R.string.settings_splus_on_time),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetFanOnTime(it.toInt()))
                sPlusOnTimeSheet.close()
            },
            onDismiss = { sPlusOnTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = sPlusOffTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.sPlusOffTime.toString(),
            title = stringResource(R.string.settings_splus_off_time),
            placeholder = stringResource(R.string.settings_splus_off_time),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetFanOffTime(it.toInt()))
                sPlusOffTimeSheet.close()
            },
            onDismiss = { sPlusOffTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = sPlusMinTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.sPlusMinTemp.toString(),
            title = stringResource(R.string.settings_splus_min),
            placeholder = stringResource(R.string.settings_splus_min),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetMinTemp(it.toInt()))
                sPlusMinTempSheet.close()
            },
            onDismiss = { sPlusMinTempSheet.close() }
        )
    }
    BottomSheet(
        sheetState = sPlusMaxTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.sPlusMaxTemp.toString(),
            title = stringResource(R.string.settings_splus_max),
            placeholder = stringResource(R.string.settings_splus_max),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetMaxTemp(it.toInt()))
                sPlusMaxTempSheet.close()
            },
            onDismiss = { sPlusMaxTempSheet.close() }
        )
    }
    BottomSheet(
        sheetState = lidThreshSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.lidOpenThreshold.toString(),
            title = stringResource(R.string.settings_lid_open_thresh),
            placeholder = stringResource(R.string.settings_lid_open_thresh),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0,
                max = 80.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetLidOpenThresh(it.toInt()))
                lidThreshSheet.close()
            },
            onDismiss = { lidThreshSheet.close() }
        )
    }
    BottomSheet(
        sheetState = lidPauseTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.lidOpenPauseTime.toString(),
            title = stringResource(R.string.settings_lid_open_pause),
            placeholder = stringResource(R.string.settings_lid_open_pause),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 10.0,
                max = 1000.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetLidOpenPauseTime(it.toInt()))
                lidPauseTimeSheet.close()
            },
            onDismiss = { lidPauseTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = keepWarmTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.keepWarmTemp.toString(),
            title = stringResource(R.string.settings_keep_warm_temp),
            placeholder = stringResource(R.string.settings_keep_warm_temp),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(WorkContract.Event.SetKeepWarmTemp(it.toInt()))
                keepWarmTempSheet.close()
            },
            onDismiss = { keepWarmTempSheet.close() }
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun WorkSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                WorkSettings(
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