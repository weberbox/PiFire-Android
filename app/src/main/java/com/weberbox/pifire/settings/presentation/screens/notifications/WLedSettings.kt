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
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.sheets.ValidationOptions
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.settings.data.model.local.Setting
import com.weberbox.pifire.settings.presentation.component.PreferenceInfo
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.TwoTargetSwitchPreference
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.contract.NotifContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
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
                        onNavigationRequested = onNavigationRequested,
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
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit,
    contentPadding: PaddingValues
) {
    val addressSheet = rememberCustomModalBottomSheetState()
    val durationSheet = rememberCustomModalBottomSheetState()
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
        TwoTargetSwitchPreference(
            value = state.serverData.settings.wledUseProfiles,
            onValueChange = { onEventSent(NotifContract.Event.SetWLedUseProfiles(it)) },
            title = { Text(text = stringResource(R.string.settings_wled_use_profiles)) },
            summary = { Text(text = getSummary(state.serverData.settings.wledUseProfiles)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.WLEDProfiles
                    )
                )
            }
        )
        PreferenceInfo(info = stringResource(R.string.settings_wled_use_profiles_note))
        TwoTargetSwitchPreference(
            value = state.serverData.settings.wledUseSuggestedProfiles,
            onValueChange = { onEventSent(NotifContract.Event.SetWLedUseSuggestedProfiles(it)) },
            title = { Text(text = stringResource(R.string.settings_wled_use_suggested_profiles)) },
            summary = { Text(text = getSummary(state.serverData.settings.wledUseSuggestedProfiles)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.WLEDSuggestedProfiles
                    )
                )
            }
        )
        PreferenceInfo(info = stringResource(R.string.settings_wled_use_suggested_profiles_note))
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
                onEventSent(NotifContract.Event.SetWLedAddress(Setting.wledAddress.value))
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