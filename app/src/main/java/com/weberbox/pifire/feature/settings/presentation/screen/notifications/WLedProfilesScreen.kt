package com.weberbox.pifire.feature.settings.presentation.screen.notifications

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
import com.weberbox.pifire.feature.settings.presentation.components.SwitchPreference
import com.weberbox.pifire.feature.settings.presentation.components.getSummary
import com.weberbox.pifire.feature.settings.presentation.components.getSummaryPercent
import com.weberbox.pifire.feature.settings.presentation.contract.NotifContract
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun WLedProfilesScreenDestination(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    ProvidePreferenceTheme {
        WLedProfilesScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is NotifContract.Effect.Navigation.Back -> navController.popBackStackFrom(from = sourceEntry)

                    is NotifContract.Effect.Navigation.NavRoute -> {
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
private fun WLedProfilesScreen(
    state: NotifContract.State,
    effectFlow: Flow<NotifContract.Effect>?,
    onEventSent: (event: NotifContract.Event) -> Unit,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit,
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
                title = stringResource(R.string.settings_cat_wled_profiles),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(NotifContract.Effect.Navigation.Back) },
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
                    onNavigationRequested(NotifContract.Effect.Navigation.Back)
                }

                else -> {
                    WLedProfilesScreenContent(
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
private fun WLedProfilesScreenContent(
    state: NotifContract.State,
    onEventSent: (event: NotifContract.Event) -> Unit,
    contentPadding: PaddingValues,
) {
    val idleBrightnessSheet = rememberCustomModalBottomSheetState()
    val ledCountSheet = rememberCustomModalBottomSheetState()
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
        ListPreference(
            value = state.serverData.settings.wledCookingColor,
            values = listOf("Blue", "Green"),
            onValueChange = { onEventSent(NotifContract.Event.SetWLedCookingColor(it.lowercase())) },
            title = { Text(text = stringResource(R.string.settings_wled_cooking_color)) },
            summary = {
                Text(
                    text = getSummary(
                        state.serverData.settings.wledCookingColor.replaceFirstChar { it.uppercase() },
                    ),
                )
            },
            type = ListPreferenceType.DROPDOWN_MENU,
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_idle_brightness)) },
            summary = {
                Text(
                    text = getSummaryPercent(
                        state.serverData.settings.wledIdleBrightness.toString(),
                    ),
                )
            },
            onClick = { idleBrightnessSheet.open() },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_wled_led_count)) },
            summary = { Text(text = getSummary(state.serverData.settings.wledLedCount.toString())) },
            onClick = { ledCountSheet.open() },
        )
        SwitchPreference(
            value = state.serverData.settings.wledNightMode,
            onValueChange = { onEventSent(NotifContract.Event.SetWLEDNightMode(it)) },
            title = { Text(text = stringResource(R.string.settings_wled_night_mode)) },
            summary = { Text(text = getSummary(state.serverData.settings.wledNightMode)) },
        )
    }
    BottomSheet(
        sheetState = idleBrightnessSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledIdleBrightness.toString(),
            title = stringResource(R.string.settings_wled_idle_brightness),
            placeholder = stringResource(R.string.settings_wled_idle_brightness),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword,
            ),
            onUpdate = {
                it.toIntOrNull()?.let { value ->
                    onEventSent(NotifContract.Event.SetWLedIdleBrightness(value))
                }
                idleBrightnessSheet.close()
            },
        )
    }
    BottomSheet(
        sheetState = ledCountSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.serverData.settings.wledModeHold.toString(),
            title = stringResource(R.string.settings_wled_led_count),
            placeholder = stringResource(R.string.settings_wled_led_count),
            validationOptions = ValidationOptions(
                keyboardType = KeyboardType.NumberPassword,
            ),
            onUpdate = {
                it.toIntOrNull()?.let { value ->
                    onEventSent(NotifContract.Event.SetWLedLedCount(value))
                }
                ledCountSheet.close()
            },
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<NotifContract.Effect>?,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit,
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
                        isError = effect.error,
                    )
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun WLedProfilePreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                WLedProfilesScreen(
                    state = NotifContract.State(
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
