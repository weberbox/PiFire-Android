package com.weberbox.pifire.settings.presentation.screens.timer

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
import com.weberbox.pifire.common.presentation.util.slideDownExpandEnterTransition
import com.weberbox.pifire.common.presentation.util.slideOutShrinkExitTransition
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.SwitchPreference
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.component.getSummaryGrams
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.component.getSummaryTemp
import com.weberbox.pifire.settings.presentation.contract.TimerContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun TimerSettingsDestination(
    navController: NavHostController,
    viewModel: TimerSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        TimerSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is TimerContract.Effect.Navigation.Back -> navController.popBackStack()
                    is TimerContract.Effect.Navigation.NavRoute -> {
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
private fun TimerSettings(
    state: TimerContract.State,
    effectFlow: Flow<TimerContract.Effect>?,
    onEventSent: (event: TimerContract.Event) -> Unit,
    onNavigationRequested: (TimerContract.Effect.Navigation) -> Unit
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsAppBar(
                title = stringResource(R.string.settings_timers_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(TimerContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(TimerContract.Effect.Navigation.Back)
                }

                else -> {
                    TimerSettingsContent(
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
private fun TimerSettingsContent(
    state: TimerContract.State,
    onEventSent: (event: TimerContract.Event) -> Unit,
    onNavigationRequested: (TimerContract.Effect.Navigation) -> Unit,
    contentPadding: PaddingValues
) {
    val startupDurationSheet = rememberCustomModalBottomSheetState()
    val startupGotoTempSheet = rememberCustomModalBottomSheetState()
    val startupExitTempSheet = rememberCustomModalBottomSheetState()
    val startupPrimeSheet = rememberCustomModalBottomSheetState()
    val smartStartExitTempSheet = rememberCustomModalBottomSheetState()
    val shutdownDurationSheet = rememberCustomModalBottomSheetState()
    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding
    )
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .fillMaxSize(),
    ) {
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_startup)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_startup_duration)) },
            summary = {
                Text(
                    text = getSummarySeconds(state.serverData.settings.startupDuration.toString())
                )
            },
            onClick = { startupDurationSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_startup_note))
        ListPreference(
            value = state.serverData.settings.startupGotoMode,
            values = listOf(
                stringResource(R.string.dash_mode_smoke),
                stringResource(R.string.dash_mode_hold)
            ),
            onValueChange = { onEventSent(TimerContract.Event.SetStartToMode(it)) },
            title = { Text(text = stringResource(R.string.settings_startup_goto_title)) },
            summary = { Text(text = getSummary(state.serverData.settings.startupGotoMode)) },
            type = ListPreferenceType.DROPDOWN_MENU
        )
        AnimatedVisibility(
            visible = state.serverData.settings.startupGotoMode == RunningMode.Hold.name,
            enter = slideDownExpandEnterTransition(),
            exit = slideOutShrinkExitTransition()
        ) {
            Preference(
                title = { Text(text = stringResource(R.string.settings_startup_hold_temp)) },
                summary = {
                    Text(
                        text = getSummaryTemp(
                            state.serverData.settings.startupGotoTemp.toString(),
                            state.serverData.settings.tempUnits
                        )
                    )
                },
                onClick = { startupGotoTempSheet.open() }
            )
        }
        PreferenceNote(stringResource(R.string.settings_startup_goto_note))
        SwitchPreference(
            value = state.serverData.settings.startupExitTemp != 0,
            onValueChange = {
                onEventSent(TimerContract.Event.SetStartExitTemp(if (it) 140 else 0))
            },
            title = { Text(text = stringResource(R.string.settings_startup_exit_temp)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.startupExitTemp != 0)
                )
            }
        )
        AnimatedVisibility(
            visible = state.serverData.settings.startupExitTemp != 0,
            enter = slideDownExpandEnterTransition(),
            exit = slideOutShrinkExitTransition()
        ) {
            Preference(
                title = {
                    Text(
                        text = stringResource(R.string.settings_startup_exit_temp_value)
                    )
                },
                summary = {
                    Text(
                        text = getSummaryTemp(
                            state.serverData.settings.startupExitTemp.toString(),
                            state.serverData.settings.tempUnits
                        )
                    )
                },
                onClick = { startupExitTempSheet.open() }
            )
        }
        SwitchPreference(
            value = state.serverData.settings.startupPrime != 0,
            onValueChange = {
                onEventSent(TimerContract.Event.SetPrimeOnStartup(if (it) 10 else 0))
            },
            title = { Text(text = stringResource(R.string.settings_startup_prime)) },
            summary = { Text(text = getSummary(state.serverData.settings.startupPrime != 0)) }
        )
        AnimatedVisibility(
            visible = state.serverData.settings.startupPrime != 0,
            enter = slideDownExpandEnterTransition(),
            exit = slideOutShrinkExitTransition()
        ) {
            Preference(
                title = {
                    Text(
                        text = stringResource(R.string.settings_startup_prime_value)
                    )
                },
                summary = {
                    Text(
                        text = getSummaryGrams(state.serverData.settings.startupPrime.toString())
                    )
                },
                onClick = { startupPrimeSheet.open() }
            )
        }
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_smart_start)) },
        )
        SwitchPreference(
            value = state.serverData.settings.smartStartEnabled,
            onValueChange = { onEventSent(TimerContract.Event.SetSmartStartEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_smart_start)) },
            summary = { Text(text = getSummary(state.serverData.settings.smartStartEnabled)) }
        )
        PreferenceNote(stringResource(R.string.settings_smart_start_note))
        Preference(
            title = {
                Text(
                    text = stringResource(R.string.settings_smart_start_profiles_title)
                )
            },
            summary = {
                Text(
                    text = stringResource(R.string.settings_smart_start_profiles_summary)
                )
            },
            onClick = {
                onNavigationRequested(
                    TimerContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.SmartStart
                    )
                )
            }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_smart_start_exit_temp)) },
            summary = {
                Text(
                    text = getSummaryTemp(
                        state.serverData.settings.smartStartExitTemp.toString(),
                        state.serverData.settings.tempUnits
                    )
                )
            },
            onClick = { smartStartExitTempSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_smart_start_exit_temp_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_shutdown)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_shutdown_duration)) },
            summary = {
                Text(
                    text = getSummarySeconds(state.serverData.settings.shutdownDuration.toString())
                )
            },
            onClick = { shutdownDurationSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_shutdown_note))
        SwitchPreference(
            value = state.serverData.settings.shutdownAutoPowerOff,
            onValueChange = { onEventSent(TimerContract.Event.SetAutoPowerOffEnabled(it)) },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.settings_auto_power_off)
                )
            },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.shutdownAutoPowerOff)
                )
            }
        )
        PreferenceNote(stringResource(R.string.settings_auto_power_off_note))
    }
    BottomSheet(
        sheetState = startupDurationSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.startupDuration.toString(),
            title = stringResource(R.string.settings_startup_duration),
            placeholder = stringResource(R.string.settings_startup_duration),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(TimerContract.Event.SetStartupDuration(it.toInt()))
                startupDurationSheet.close()
            },
            onDismiss = { startupDurationSheet.close() }
        )
    }
    BottomSheet(
        sheetState = startupGotoTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.startupGotoTemp.toString(),
            title = stringResource(R.string.settings_startup_hold_temp),
            placeholder = stringResource(R.string.settings_startup_hold_temp),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(TimerContract.Event.SetStartToModeTemp(it.toInt()))
                startupGotoTempSheet.close()
            },
            onDismiss = { startupGotoTempSheet.close() }
        )
    }
    BottomSheet(
        sheetState = startupExitTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.startupExitTemp.toString(),
            title = stringResource(R.string.settings_startup_exit_temp_value),
            placeholder = stringResource(R.string.settings_startup_exit_temp_value),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(TimerContract.Event.SetStartExitTemp(it.toInt()))
                startupExitTempSheet.close()
            },
            onDismiss = { startupExitTempSheet.close() }
        )
    }
    BottomSheet(
        sheetState = startupPrimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.startupPrime.toString(),
            title = stringResource(R.string.settings_startup_prime_value),
            placeholder = stringResource(R.string.settings_startup_prime_value),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(TimerContract.Event.SetPrimeOnStartup(it.toInt()))
                startupPrimeSheet.close()
            },
            onDismiss = { startupPrimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = smartStartExitTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.smartStartExitTemp.toString(),
            title = stringResource(R.string.settings_smart_start_exit_temp),
            placeholder = stringResource(R.string.settings_smart_start_exit_temp),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(TimerContract.Event.SetSmartStartExitTemp(it.toInt()))
                smartStartExitTempSheet.close()
            },
            onDismiss = { smartStartExitTempSheet.close() }
        )
    }
    BottomSheet(
        sheetState = shutdownDurationSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.shutdownDuration.toString(),
            title = stringResource(R.string.settings_shutdown_duration),
            placeholder = stringResource(R.string.settings_shutdown_duration),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(TimerContract.Event.SetShutdownDuration(it.toInt()))
                shutdownDurationSheet.close()
            },
            onDismiss = { shutdownDurationSheet.close() }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<TimerContract.Effect>?,
    onNavigationRequested: (TimerContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is TimerContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is TimerContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun TimerSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                TimerSettings(
                    state = TimerContract.State(
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