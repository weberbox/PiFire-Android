package com.weberbox.pifire.settings.presentation.screens.pwm

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
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.sheets.ValidationOptions
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.SwitchPreference
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.component.getSummaryHz
import com.weberbox.pifire.settings.presentation.component.getSummaryPercent
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.contract.PwmContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun PwmSettingsDestination(
    navController: NavHostController,
    viewModel: PwmSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        PwmSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is PwmContract.Effect.Navigation.Back -> navController.popBackStack()
                    is PwmContract.Effect.Navigation.NavRoute -> {
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
private fun PwmSettings(
    state: PwmContract.State,
    effectFlow: Flow<PwmContract.Effect>?,
    onEventSent: (event: PwmContract.Event) -> Unit,
    onNavigationRequested: (PwmContract.Effect.Navigation) -> Unit
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
                title = stringResource(R.string.settings_pwm_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(PwmContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(PwmContract.Effect.Navigation.Back)
                }

                else -> {
                    PwmSettingsContent(
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
private fun PwmSettingsContent(
    state: PwmContract.State,
    onEventSent: (event: PwmContract.Event) -> Unit,
    onNavigationRequested: (PwmContract.Effect.Navigation) -> Unit,
    contentPadding: PaddingValues
) {
    val updateTimeSheet = rememberCustomModalBottomSheetState()
    val frequencySheet = rememberCustomModalBottomSheetState()
    val minDutyCycleSheet = rememberCustomModalBottomSheetState()
    val maxDutyCycleSheet = rememberCustomModalBottomSheetState()
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
            title = { Text(text = stringResource(R.string.settings_cat_pwm_fan_control)) },
        )
        SwitchPreference(
            value = state.serverData.settings.pwmControl,
            onValueChange = { onEventSent(PwmContract.Event.SetPWMControlDefault(it)) },
            title = { Text(text = stringResource(R.string.settings_pwm_fan_control_enabled)) },
            summary = { Text(text = getSummary(state.serverData.settings.pwmControl)) }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pwm_fan_update_time)) },
            summary = {
                Text(
                    text = getSummarySeconds(state.serverData.settings.pwmUpdateTime.toString())
                )
            },
            onClick = { updateTimeSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pwm_control_title)) },
            summary = { Text(text = stringResource(R.string.settings_pwm_control_summary)) },
            onClick = {
                onNavigationRequested(
                    PwmContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.PwmControl
                    )
                )
            }
        )
        PreferenceNote(stringResource(R.string.settings_pwm_fan_control_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_fan_settings)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pwm_frequency)) },
            summary = {
                Text(
                    text = getSummaryHz(state.serverData.settings.pwmFrequency.toString())
                )
            },
            onClick = { frequencySheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pwm_min_duty_cycle)) },
            summary = {
                Text(
                    text = getSummaryPercent(state.serverData.settings.pwmMinDutyCycle.toString())
                )
            },
            onClick = { minDutyCycleSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_pwm_max_duty_cycle)) },
            summary = {
                Text(
                    text = getSummaryPercent(state.serverData.settings.pwmMaxDutyCycle.toString())
                )
            },
            onClick = { maxDutyCycleSheet.open() }
        )
    }
    BottomSheet(
        sheetState = updateTimeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pwmUpdateTime.toString(),
            title = stringResource(R.string.settings_pwm_fan_update_time),
            placeholder = stringResource(R.string.settings_pwm_fan_update_time),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(PwmContract.Event.SetPWMTempUpdateTime(it.toInt()))
                updateTimeSheet.close()
            },
            onDismiss = { updateTimeSheet.close() }
        )
    }
    BottomSheet(
        sheetState = frequencySheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pwmFrequency.toString(),
            title = stringResource(R.string.settings_pwm_frequency),
            placeholder = stringResource(R.string.settings_pwm_frequency),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0
            ),
            onUpdate = {
                onEventSent(PwmContract.Event.SetPWMFanFrequency(it.toInt()))
                frequencySheet.close()
            },
            onDismiss = { frequencySheet.close() }
        )
    }
    BottomSheet(
        sheetState = minDutyCycleSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pwmMinDutyCycle.toString(),
            title = stringResource(R.string.settings_pwm_min_duty_cycle),
            placeholder = stringResource(R.string.settings_pwm_min_duty_cycle),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0,
                max = 100.0
            ),
            onUpdate = {
                onEventSent(PwmContract.Event.SetPWMMinDutyCycle(it.toInt()))
                minDutyCycleSheet.close()
            },
            onDismiss = { minDutyCycleSheet.close() }
        )
    }
    BottomSheet(
        sheetState = maxDutyCycleSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.pwmMaxDutyCycle.toString(),
            title = stringResource(R.string.settings_pwm_max_duty_cycle),
            placeholder = stringResource(R.string.settings_pwm_max_duty_cycle),
            validationOptions = ValidationOptions(
                allowBlank = false,
                keyboardType = KeyboardType.NumberPassword,
                min = 1.0,
                max = 100.0
            ),
            onUpdate = {
                onEventSent(PwmContract.Event.SetPWMMaxDutyCycle(it.toInt()))
                maxDutyCycleSheet.close()
            },
            onDismiss = { maxDutyCycleSheet.close() }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<PwmContract.Effect>?,
    onNavigationRequested: (PwmContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PwmContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is PwmContract.Effect.Notification -> {
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
private fun PwmSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                PwmSettings(
                    state = PwmContract.State(
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