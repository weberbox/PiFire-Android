package com.weberbox.pifire.feature.settings.presentation.screen.pellets

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.DataError
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.LinearLoadingIndicator
import com.weberbox.pifire.core.ui.components.SettingsAppBar
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.components.sheets.InputValidationSheet
import com.weberbox.pifire.core.ui.components.sheets.ValidationOptions
import com.weberbox.pifire.core.ui.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.showAlerter
import com.weberbox.pifire.feature.settings.presentation.components.PreferenceDanger
import com.weberbox.pifire.feature.settings.presentation.components.PreferenceNote
import com.weberbox.pifire.feature.settings.presentation.components.SwitchPreference
import com.weberbox.pifire.feature.settings.presentation.components.getSummary
import com.weberbox.pifire.feature.settings.presentation.components.getSummaryCm
import com.weberbox.pifire.feature.settings.presentation.components.getSummaryGs
import com.weberbox.pifire.feature.settings.presentation.components.getSummaryMinutes
import com.weberbox.pifire.feature.settings.presentation.components.getSummaryPercent
import com.weberbox.pifire.feature.settings.presentation.contract.PelletsContract
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun PelletSettingsScreenDestination(
    navController: NavHostController,
    viewModel: PelletSettingsViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    ProvidePreferenceTheme {
        PelletSettingsScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is PelletsContract.Effect.Navigation.Back -> navController.popBackStackFrom(from = sourceEntry)

                    is PelletsContract.Effect.Navigation.NavRoute -> {
                        navController.navigateFrom(
                            from = sourceEntry,
                            route = navigationEffect.route,
                            popUpToRoute = navigationEffect.popUpToRoute,
                            inclusive = navigationEffect.inclusive,
                        )
                    }
                }
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PelletSettingsScreen(
    state: PelletsContract.State,
    effectFlow: Flow<PelletsContract.Effect>?,
    onEventSent: (event: PelletsContract.Event) -> Unit,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested,
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsAppBar(
                title = stringResource(R.string.settings_pellets_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(PelletsContract.Effect.Navigation.Back) },
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        AnimatedContent(
            targetState = state,
            contentKey = { it.isInitialLoading or it.isDataError },
        ) { state ->
            when {
                state.isInitialLoading -> InitialLoadingProgress()

                state.isDataError -> DataError {
                    onNavigationRequested(PelletsContract.Effect.Navigation.Back)
                }

                else -> {
                    PelletSettingsScreenContent(
                        state = state,
                        onEventSent = onEventSent,
                        contentPadding = contentPadding,
                    )
                }
            }
        }
    }
}

@Composable
private fun PelletSettingsScreenContent(
    state: PelletsContract.State,
    onEventSent: (event: PelletsContract.Event) -> Unit,
    contentPadding: PaddingValues,
) {
    val warningTimeSheet = rememberCustomModalBottomSheetState()
    val warningLevelSheet = rememberCustomModalBottomSheetState()
    val fullLevelSheet = rememberCustomModalBottomSheetState()
    val emptyLevelSheet = rememberCustomModalBottomSheetState()
    val augerRateSheet = rememberCustomModalBottomSheetState()
    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
    ) {
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_pellet_warning)) },
        )
        SwitchPreference(
            value = state.serverData.settings.pelletsWarningEnabled,
            onValueChange = { onEventSent(PelletsContract.Event.SetWarningEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_pellet_warning)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.pelletsWarningEnabled),
                )
            },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pellet_warning_time)) },
            summary = {
                Text(
                    text = getSummaryMinutes(
                        state.serverData.settings.pelletsWarningTime.toString(),
                    ),
                )
            },
            onClick = { warningTimeSheet.open() },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pellet_warning_level)) },
            summary = {
                Text(
                    text = getSummaryPercent(
                        state.serverData.settings.pelletsWarningLevel.toString(),
                    ),
                )
            },
            onClick = { warningLevelSheet.open() },
        )
        PreferenceNote(stringResource(R.string.settings_pellet_warning_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_pellets_sensor)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pellet_sensor_empty)) },
            summary = {
                Text(
                    text = getSummaryCm(
                        state.serverData.settings.pelletsEmpty.toString(),
                    ),
                )
            },
            onClick = { emptyLevelSheet.open() },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pellet_sensor_full)) },
            summary = {
                Text(
                    text = getSummaryCm(
                        state.serverData.settings.pelletsFull.toString(),
                    ),
                )
            },
            onClick = { fullLevelSheet.open() },
        )
        PreferenceNote(stringResource(R.string.settings_pellet_sensor_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_pellets_auger)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pellet_auger_rate)) },
            summary = {
                Text(
                    text = getSummaryGs(
                        state.serverData.settings.augerRate.toString(),
                    ),
                )
            },
            onClick = { augerRateSheet.open() },
        )
        PreferenceNote(stringResource(R.string.settings_pellet_auger_rate_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_pellets_prime)) },
        )
        SwitchPreference(
            value = state.serverData.settings.primeIgnition,
            onValueChange = { onEventSent(PelletsContract.Event.SetPrimeIgnition(it)) },
            title = { Text(text = stringResource(R.string.settings_pellet_prime_igniter)) },
            summary = { Text(text = getSummary(state.serverData.settings.primeIgnition)) },
        )
        PreferenceDanger(stringResource(R.string.settings_pellet_prime_igniter_note))
    }
    BottomSheet(
        sheetState = warningTimeSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pelletsWarningTime.toString(),
            title = stringResource(R.string.settings_pellet_warning_time),
            placeholder = stringResource(R.string.settings_pellet_warning_time),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0,
            ),
            onUpdate = {
                it.toIntOrNull()?.let { value ->
                    onEventSent(PelletsContract.Event.SetWarningTime(value))
                }
                warningTimeSheet.close()
            },
            onDismiss = { warningTimeSheet.close() },
        )
    }
    BottomSheet(
        sheetState = warningLevelSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pelletsWarningLevel.toString(),
            title = stringResource(R.string.settings_pellet_warning_level),
            placeholder = stringResource(R.string.settings_pellet_warning_level),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0,
                max = 100.0,
            ),
            onUpdate = {
                it.toIntOrNull()?.let { value ->
                    onEventSent(PelletsContract.Event.SetWarningLevel(value))
                }
                warningLevelSheet.close()
            },
            onDismiss = { warningLevelSheet.close() },
        )
    }
    BottomSheet(
        sheetState = fullLevelSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pelletsFull.toString(),
            title = stringResource(R.string.settings_pellet_sensor_full),
            placeholder = stringResource(R.string.settings_pellet_sensor_full),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                max = (state.serverData.settings.pelletsEmpty - 1).toDouble(),
            ),
            onUpdate = {
                it.toIntOrNull()?.let { value ->
                    onEventSent(PelletsContract.Event.SetFullLevel(value))
                }
                fullLevelSheet.close()
            },
            onDismiss = { fullLevelSheet.close() },
        )
    }
    BottomSheet(
        sheetState = emptyLevelSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pelletsEmpty.toString(),
            title = stringResource(R.string.settings_pellet_sensor_empty),
            placeholder = stringResource(R.string.settings_pellet_sensor_empty),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0,
                max = 100.0,
            ),
            onUpdate = {
                it.toIntOrNull()?.let { value ->
                    onEventSent(PelletsContract.Event.SetEmptyLevel(value))
                }
                emptyLevelSheet.close()
            },
            onDismiss = { emptyLevelSheet.close() },
        )
    }
    BottomSheet(
        sheetState = augerRateSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.serverData.settings.augerRate.toString(),
            title = stringResource(R.string.settings_pellet_auger_rate),
            placeholder = stringResource(R.string.settings_pellet_auger_rate),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.Decimal,
                min = 0.0,
            ),
            onUpdate = {
                it.toDoubleOrNull()?.let { value ->
                    onEventSent(PelletsContract.Event.SetAugerRate(value))
                }
                augerRateSheet.close()
            },
            onDismiss = { augerRateSheet.close() },
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<PelletsContract.Effect>?,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PelletsContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is PelletsContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error,
                    )
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun PelletSettingsScreenPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                PelletSettingsScreen(
                    state = PelletsContract.State(
                        serverData = Server(),
                        isInitialLoading = false,
                        isLoading = true,
                        isDataError = false,
                    ),
                    effectFlow = null,
                    onEventSent = {},
                    onNavigationRequested = {},
                )
            }
        }
    }
}
