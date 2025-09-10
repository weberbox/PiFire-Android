package com.weberbox.pifire.settings.presentation.screens.notifications

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
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.contract.NotifContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun WLedSettingsDestination(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        WLedSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is NotifContract.Effect.Navigation.Back -> navController.popBackStack()
                    is NotifContract.Effect.Navigation.NavRoute -> {
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
private fun WLedSettings(
    state: NotifContract.State,
    effectFlow: Flow<NotifContract.Effect>?,
    onEventSent: (event: NotifContract.Event) -> Unit,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit
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
                title = stringResource(R.string.settings_cat_wled),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(NotifContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(NotifContract.Effect.Navigation.Back)
                }

                else -> {
                    WLedContent(
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
private fun WLedContent(
    state: NotifContract.State,
    onEventSent: (event: NotifContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val addressSheet = rememberCustomModalBottomSheetState()
    val durationSheet = rememberCustomModalBottomSheetState()
    val modeHoldSheet = rememberCustomModalBottomSheetState()
    val modePrimeSheet = rememberCustomModalBottomSheetState()
    val modeReigniteSheet = rememberCustomModalBottomSheetState()
    val modeShutdownSheet = rememberCustomModalBottomSheetState()
    val modeSmokeSheet = rememberCustomModalBottomSheetState()
    val modeStartupSheet = rememberCustomModalBottomSheetState()
    val modeStopSheet = rememberCustomModalBottomSheetState()
    val eventGrillSheet = rememberCustomModalBottomSheetState()
    val eventPelletsSheet = rememberCustomModalBottomSheetState()
    val eventRecipeSheet = rememberCustomModalBottomSheetState()
    val eventTempSheet = rememberCustomModalBottomSheetState()
    val eventTimerSheet = rememberCustomModalBottomSheetState()
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
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_address)) },
            summary = { Text(text = getSummary(state.serverData.settings.wledAddress)) },
            onClick = { addressSheet.open() }
        )
        PreferenceNote(note = stringResource(R.string.settings_wled_address_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_duration)) },
            summary = {
                Text(
                    text = getSummarySeconds(state.serverData.settings.wledDuration.toString())
                )
            },
            onClick = { durationSheet.open() }
        )
        PreferenceNote(note = stringResource(R.string.settings_wled_duration_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_wled_presets)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_presets_hold)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledModeHold.toString())
                )
            },
            onClick = { modeHoldSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_presets_prime)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledModePrime.toString())
                )
            },
            onClick = { modePrimeSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_presets_reignite)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledModeReignite.toString())
                )
            },
            onClick = { modeReigniteSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_presets_shutdown)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledModeShutdown.toString())
                )
            },
            onClick = { modeShutdownSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_presets_smoke)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledModeSmoke.toString())
                )
            },
            onClick = { modeSmokeSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_presets_startup)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledModeStartup.toString())
                )
            },
            onClick = { modeStartupSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_presets_stop)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledModeStop.toString())
                )
            },
            onClick = { modeStopSheet.open() }
        )
        PreferenceNote(note = stringResource(R.string.settings_wled_presets_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_wled_events)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_events_grill)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledEventGrill.toString())
                )
            },
            onClick = { eventGrillSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_events_pellets)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledEventPellets.toString())
                )
            },
            onClick = { eventPelletsSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_events_recipe)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledEventRecipe.toString())
                )
            },
            onClick = { eventRecipeSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_events_temp)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledEventTemp.toString())
                )
            },
            onClick = { eventTempSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_events_timer)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledEventTimer.toString())
                )
            },
            onClick = { eventTimerSheet.open() }
        )
    }
    BottomSheet(
        sheetState = addressSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledAddress,
            title = stringResource(R.string.settings_wled_address),
            placeholder = stringResource(R.string.settings_wled_address),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedAddress(it))
                addressSheet.close()
            },
            onDelete = {
                onEventSent(NotifContract.Event.SetWLedAddress(""))
                addressSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = durationSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledDuration.toString(),
            title = stringResource(R.string.settings_wled_duration),
            placeholder = stringResource(R.string.settings_wled_duration),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedDuration(it.toInt()))
                durationSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = modeHoldSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModeHold.toString(),
            title = stringResource(R.string.settings_wled_presets_hold),
            placeholder = stringResource(R.string.settings_wled_presets_hold),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedModeHold(it.toInt()))
                modeHoldSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = modePrimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModePrime.toString(),
            title = stringResource(R.string.settings_wled_presets_prime),
            placeholder = stringResource(R.string.settings_wled_presets_prime),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedModePrime(it.toInt()))
                modePrimeSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = modeReigniteSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModeReignite.toString(),
            title = stringResource(R.string.settings_wled_presets_reignite),
            placeholder = stringResource(R.string.settings_wled_presets_reignite),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedModeReignite(it.toInt()))
                modeReigniteSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = modeShutdownSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModeShutdown.toString(),
            title = stringResource(R.string.settings_wled_presets_shutdown),
            placeholder = stringResource(R.string.settings_wled_presets_shutdown),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedModeShutdown(it.toInt()))
                modeShutdownSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = modeSmokeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModeSmoke.toString(),
            title = stringResource(R.string.settings_wled_presets_smoke),
            placeholder = stringResource(R.string.settings_wled_presets_smoke),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedModeSmoke(it.toInt()))
                modeSmokeSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = modeStartupSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModeStartup.toString(),
            title = stringResource(R.string.settings_wled_presets_startup),
            placeholder = stringResource(R.string.settings_wled_presets_startup),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedModeStartup(it.toInt()))
                modeStartupSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = modeStopSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModeStop.toString(),
            title = stringResource(R.string.settings_wled_presets_stop),
            placeholder = stringResource(R.string.settings_wled_presets_stop),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedModeStop(it.toInt()))
                modeStopSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = eventGrillSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledEventGrill.toString(),
            title = stringResource(R.string.settings_wled_events_grill),
            placeholder = stringResource(R.string.settings_wled_events_grill),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedEventGrill(it.toInt()))
                eventGrillSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = eventPelletsSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledEventPellets.toString(),
            title = stringResource(R.string.settings_wled_events_pellets),
            placeholder = stringResource(R.string.settings_wled_events_pellets),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedEventPellets(it.toInt()))
                eventPelletsSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = eventRecipeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledEventRecipe.toString(),
            title = stringResource(R.string.settings_wled_events_recipe),
            placeholder = stringResource(R.string.settings_wled_events_recipe),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedEventRecipe(it.toInt()))
                eventRecipeSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = eventTempSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledEventTemp.toString(),
            title = stringResource(R.string.settings_wled_events_temp),
            placeholder = stringResource(R.string.settings_wled_events_temp),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedEventTemp(it.toInt()))
                eventTempSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = eventTimerSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledEventTimer.toString(),
            title = stringResource(R.string.settings_wled_events_timer),
            placeholder = stringResource(R.string.settings_wled_events_timer),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedEventTimer(it.toInt()))
                eventTimerSheet.close()
            }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<NotifContract.Effect>?,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is NotifContract.Effect.RequestPermission -> {}
                is NotifContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is NotifContract.Effect.Notification -> {
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
private fun WLedSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                WLedSettings(
                    state = NotifContract.State(
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