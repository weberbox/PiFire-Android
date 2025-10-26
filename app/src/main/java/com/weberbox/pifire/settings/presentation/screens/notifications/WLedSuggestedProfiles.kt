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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.contract.NotifContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun WLedSuggestedProfilesDestination(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        WLedSuggestedProfiles(
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
private fun WLedSuggestedProfiles(
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
                title = stringResource(R.string.settings_cat_wled_suggested_profiles),
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
    val profileBootSheet = rememberCustomModalBottomSheetState()
    val profileCookingSheet = rememberCustomModalBottomSheetState()
    val profileCooldownSheet = rememberCustomModalBottomSheetState()
    val profileFaultSheet = rememberCustomModalBottomSheetState()
    val profileIdleSheet = rememberCustomModalBottomSheetState()
    val profilePelletsSheet = rememberCustomModalBottomSheetState()
    val profileNightSheet = rememberCustomModalBottomSheetState()
    val profileOvershootSheet = rememberCustomModalBottomSheetState()
    val profilePreheatSheet = rememberCustomModalBottomSheetState()
    val profileProbeSheet = rememberCustomModalBottomSheetState()
    val profileTargetSheet = rememberCustomModalBottomSheetState()
    val profileTimerSheet = rememberCustomModalBottomSheetState()
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
            title = { Text(text = stringResource(R.string.settings_wled_profile_booting)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileBooting.toString())
                )
            },
            onClick = { profileBootSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_cooking)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileCooking.toString())
                )
            },
            onClick = { profileCookingSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_cooldown)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileCooldown.toString())
                )
            },
            onClick = { profileCooldownSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_fault)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileFault.toString())
                )
            },
            onClick = { profileFaultSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_idle)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileIdle.toString())
                )
            },
            onClick = { profileIdleSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_pellets)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfilePellets.toString())
                )
            },
            onClick = { profilePelletsSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_night)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileNight.toString())
                )
            },
            onClick = { profileNightSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_overshoot)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileOvershoot.toString())
                )
            },
            onClick = { profileOvershootSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_preheat)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfilePreheat.toString())
                )
            },
            onClick = { profilePreheatSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_probe)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileProbe.toString())
                )
            },
            onClick = { profileProbeSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_target)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileTarget.toString())
                )
            },
            onClick = { profileTargetSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_profile_timer)) },
            summary = {
                Text(
                    text = getSummary(state.serverData.settings.wledProfileTimer.toString())
                )
            },
            onClick = { profileTimerSheet.open() }
        )
    }
    BottomSheet(
        sheetState = profileBootSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileBooting.toString(),
            title = stringResource(R.string.settings_wled_profile_booting),
            placeholder = stringResource(R.string.settings_wled_profile_booting),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileBooting(it.toInt()))
                profileBootSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileCookingSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileCooking.toString(),
            title = stringResource(R.string.settings_wled_profile_cooking),
            placeholder = stringResource(R.string.settings_wled_profile_cooking),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileCooking(it.toInt()))
                profileCookingSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileCooldownSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileCooldown.toString(),
            title = stringResource(R.string.settings_wled_profile_cooldown),
            placeholder = stringResource(R.string.settings_wled_profile_cooldown),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileCooling(it.toInt()))
                profileCooldownSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileFaultSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileFault.toString(),
            title = stringResource(R.string.settings_wled_profile_fault),
            placeholder = stringResource(R.string.settings_wled_profile_fault),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileFault(it.toInt()))
                profileFaultSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileIdleSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileIdle.toString(),
            title = stringResource(R.string.settings_wled_profile_idle),
            placeholder = stringResource(R.string.settings_wled_profile_idle),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileIdle(it.toInt()))
                profileIdleSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profilePelletsSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfilePellets.toString(),
            title = stringResource(R.string.settings_wled_profile_pellets),
            placeholder = stringResource(R.string.settings_wled_profile_pellets),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfilePellets(it.toInt()))
                profilePelletsSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileNightSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileNight.toString(),
            title = stringResource(R.string.settings_wled_profile_night),
            placeholder = stringResource(R.string.settings_wled_profile_night),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileNight(it.toInt()))
                profileNightSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileOvershootSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileOvershoot.toString(),
            title = stringResource(R.string.settings_wled_profile_overshoot),
            placeholder = stringResource(R.string.settings_wled_profile_overshoot),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileOvershoot(it.toInt()))
                profileOvershootSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profilePreheatSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfilePreheat.toString(),
            title = stringResource(R.string.settings_wled_profile_preheat),
            placeholder = stringResource(R.string.settings_wled_profile_preheat),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfilePreheat(it.toInt()))
                profilePreheatSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileProbeSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileProbe.toString(),
            title = stringResource(R.string.settings_wled_profile_probe),
            placeholder = stringResource(R.string.settings_wled_profile_probe),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileProbe(it.toInt()))
                profileProbeSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileTargetSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileTarget.toString(),
            title = stringResource(R.string.settings_wled_profile_target),
            placeholder = stringResource(R.string.settings_wled_profile_target),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileTarget(it.toInt()))
                profileTargetSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = profileTimerSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledProfileTimer.toString(),
            title = stringResource(R.string.settings_wled_profile_timer),
            placeholder = stringResource(R.string.settings_wled_profile_timer),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            onUpdate = {
                onEventSent(NotifContract.Event.SetWLedProfileTimer(it.toInt()))
                profileTimerSheet.close()
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
private fun WLedSuggestedProfilesPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                WLedSuggestedProfiles(
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


