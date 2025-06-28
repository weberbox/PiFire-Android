package com.weberbox.pifire.settings.presentation.screens.admin

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.contract.ManualContract
import com.weberbox.pifire.settings.presentation.model.ManualData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@Composable
fun ManualSettingsDestination(
    navController: NavHostController,
    viewModel: ManualSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        ManualSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is ManualContract.Effect.Navigation.Back -> navController.popBackStack()
                    is ManualContract.Effect.Navigation.NavRoute -> {
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
private fun ManualSettings(
    state: ManualContract.State,
    effectFlow: Flow<ManualContract.Effect>?,
    onEventSent: (event: ManualContract.Event) -> Unit,
    onNavigationRequested: (ManualContract.Effect.Navigation) -> Unit
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
                        text = stringResource(R.string.settings_manual),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(ManualContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(ManualContract.Effect.Navigation.Back)
                }

                else -> {
                    ManualSettingsContent(
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
private fun ManualSettingsContent(
    state: ManualContract.State,
    onEventSent: (event: ManualContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    var pwm by remember(state.manualData.pwm) { mutableIntStateOf(state.manualData.pwm) }
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
            title = { Text(text = stringResource(R.string.settings_manual)) },
        )
        SwitchPreference(
            value = state.manualData.active,
            onValueChange = { onEventSent(ManualContract.Event.SetManualMode(it)) },
            title = { Text(text = stringResource(R.string.settings_manual)) },
            summary = { Text(text = getSummary(state.manualData.active)) }
        )
        PreferenceNote(stringResource(R.string.settings_admin_manual_note))
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_manual_functions)) },
        )
        SwitchPreference(
            value = state.manualData.fan,
            onValueChange = { onEventSent(ManualContract.Event.SetFanEnabled(it)) },
            enabled = state.manualData.active,
            title = { Text(text = stringResource(R.string.settings_manual_fan)) },
            summary = { Text(text = getSummary(state.manualData.fan)) }
        )
        if (state.manualData.dcFan) {
            SliderPreference(
                value = pwm.toFloat(),
                onValueChange = { onEventSent(ManualContract.Event.SetDutyCycle(it.toInt())) },
                enabled = state.manualData.active && state.manualData.fan,
                sliderValue = pwm.toFloat(),
                valueRange = 0f..100f,
                onSliderValueChange = {
                    pwm = it.toInt()
                },
                title = { Text(text = stringResource(R.string.settings_manual_pwm)) },
                summary = { Text(text = "$pwm %") }
            )
        }
        SwitchPreference(
            value = state.manualData.auger,
            onValueChange = { onEventSent(ManualContract.Event.SetAugerEnabled(it)) },
            enabled = state.manualData.active,
            title = { Text(text = stringResource(R.string.settings_manual_auger)) },
            summary = { Text(text = getSummary(state.manualData.auger)) }
        )
        SwitchPreference(
            value = state.manualData.igniter,
            onValueChange = { onEventSent(ManualContract.Event.SetIgniterEnabled(it)) },
            enabled = state.manualData.active,
            title = { Text(text = stringResource(R.string.settings_manual_igniter)) },
            summary = { Text(text = getSummary(state.manualData.igniter)) }
        )
        SwitchPreference(
            value = state.manualData.power,
            onValueChange = { onEventSent(ManualContract.Event.SetPowerEnabled(it)) },
            enabled = state.manualData.active,
            title = { Text(text = stringResource(R.string.settings_manual_power)) },
            summary = { Text(text = getSummary(state.manualData.power)) }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<ManualContract.Effect>?,
    onNavigationRequested: (ManualContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ManualContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is ManualContract.Effect.Notification -> {
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
private fun ManualSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                ManualSettings(
                    state = ManualContract.State(
                        manualData = ManualData(
                            active = true,
                            fan = true,
                            dcFan = true
                        ),
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