package com.weberbox.pifire.settings.presentation.screens.safety

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
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.PreferenceWarning
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.component.getSummaryTemp
import com.weberbox.pifire.settings.presentation.contract.SafetyContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
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
fun SafetySettingsDestination(
    navController: NavHostController,
    viewModel: SafetySettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        SafetySettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is SafetyContract.Effect.Navigation.Back -> navController.popBackStack()
                    is SafetyContract.Effect.Navigation.NavRoute -> {
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
private fun SafetySettings(
    state: SafetyContract.State,
    effectFlow: Flow<SafetyContract.Effect>?,
    onEventSent: (event: SafetyContract.Event) -> Unit,
    onNavigationRequested: (SafetyContract.Effect.Navigation) -> Unit
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
                title = {
                    Text(
                        text = stringResource(R.string.settings_safety_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(SafetyContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(SafetyContract.Effect.Navigation.Back)
                }

                else -> {
                    SafetySettingsContent(
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
fun SafetySettingsContent(
    state: SafetyContract.State,
    onEventSent: (event: SafetyContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val overrideTimeSheet = rememberCustomModalBottomSheetState()
    val minStartupTempSheet = rememberCustomModalBottomSheetState()
    val maxStartupTempSheet = rememberCustomModalBottomSheetState()
    val maxTempSheet = rememberCustomModalBottomSheetState()
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
            title = { Text(text = stringResource(R.string.settings_safety_title)) },
        )
        SwitchPreference(
            value = state.serverData.settings.safetyStartupCheck,
            onValueChange = { onEventSent(SafetyContract.Event.SetSafetyStartupCheck(it)) },
            title = { Text(text = stringResource(R.string.settings_safety_startup_check)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_safety_startup_check_summary)
                )
            }
        )
        SwitchPreference(
            value = state.serverData.settings.safetyAllowManualChanges,
            onValueChange = { onEventSent(SafetyContract.Event.SetAllowManualChanges(it)) },
            title = { Text(text = stringResource(R.string.settings_safety_manual_outputs)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_safety_manual_outputs_summary)
                )
            }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_safety_manual_outputs_time)) },
            summary = {
                Text(
                    text = getSummarySeconds(
                        state.serverData.settings.safetyOverrideTime.toString()
                    )
                )
            },
            onClick = { overrideTimeSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_safety_manual_outputs_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_operating_temps_title)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_safety_min_start)) },
            summary = {
                Text(
                    text = getSummaryTemp(
                        state.serverData.settings.safetyMinStartupTemp.toString(),
                        state.serverData.settings.tempUnits
                    )
                )
            },
            onClick = { minStartupTempSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_safety_max_start)) },
            summary = {
                Text(
                    text = getSummaryTemp(
                        state.serverData.settings.safetyMaxStartupTemp.toString(),
                        state.serverData.settings.tempUnits
                    )
                )
            },
            onClick = { maxStartupTempSheet.open() }
        )
        ListPreference(
            value = state.serverData.settings.safetyReigniteRetries,
            values = (1..10).toList(),
            onValueChange = { onEventSent(SafetyContract.Event.SetReigniteRetries(it)) },
            title = { Text(text = stringResource(R.string.settings_safety_retries)) },
            summary = {
                Text(
                    text = getSummary(
                        state.serverData.settings.safetyReigniteRetries.toString()
                    )
                )
            },
            type = ListPreferenceType.DROPDOWN_MENU
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_safety_max_temp)) },
            summary = {
                Text(
                    text = getSummaryTemp(
                        state.serverData.settings.safetyMaxTemp.toString(),
                        state.serverData.settings.tempUnits
                    )
                )
            },
            onClick = { maxTempSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_safety_note))
        PreferenceWarning(stringResource(R.string.settings_safety_warning))
    }
    BottomSheet(
        sheetState = overrideTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.safetyOverrideTime.toString(),
            title = stringResource(R.string.settings_safety_manual_outputs_time),
            placeholder = stringResource(R.string.settings_safety_manual_outputs_time),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(SafetyContract.Event.SetOverrideTime(it.toInt()))
                overrideTimeSheet.close()
            },
            onDismiss = { overrideTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = minStartupTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.safetyMinStartupTemp.toString(),
            title = stringResource(R.string.settings_safety_min_start),
            placeholder = stringResource(R.string.settings_safety_min_start),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(SafetyContract.Event.SetMinStartTemp(it.toInt()))
                minStartupTempSheet.close()
            },
            onDismiss = { minStartupTempSheet.close() }
        )
    }
    BottomSheet(
        sheetState = maxStartupTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.safetyMaxStartupTemp.toString(),
            title = stringResource(R.string.settings_safety_max_start),
            placeholder = stringResource(R.string.settings_safety_max_start),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(SafetyContract.Event.SetMaxStartTemp(it.toInt()))
                maxStartupTempSheet.close()
            },
            onDismiss = { maxStartupTempSheet.close() }
        )
    }
    BottomSheet(
        sheetState = maxTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.safetyMaxTemp.toString(),
            title = stringResource(R.string.settings_safety_max_temp),
            placeholder = stringResource(R.string.settings_safety_max_temp),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(SafetyContract.Event.SetMaxGrillTemp(it.toInt()))
                maxTempSheet.close()
            },
            onDismiss = { maxTempSheet.close() }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<SafetyContract.Effect>?,
    onNavigationRequested: (SafetyContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is SafetyContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is SafetyContract.Effect.Notification -> {
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
private fun SafetySettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                SafetySettings(
                    state = SafetyContract.State(
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