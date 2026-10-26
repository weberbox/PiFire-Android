package com.weberbox.pifire.feature.settings.presentation.screen.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.remoteconfig.Feature
import com.weberbox.pifire.core.remoteconfig.FeatureGate
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.DataError
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.LinearLoadingIndicator
import com.weberbox.pifire.core.ui.components.SettingsAppBar
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.components.sheets.InputValidationSheet
import com.weberbox.pifire.core.ui.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.showAlerter
import com.weberbox.pifire.feature.settings.presentation.components.SwitchPreference
import com.weberbox.pifire.feature.settings.presentation.components.getSummary
import com.weberbox.pifire.feature.settings.presentation.contract.UIContract
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
fun UISettingsScreenDestination(
    navController: NavHostController,
    viewModel: UISettingsViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    ProvidePreferenceTheme {
        UISettingsScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is UIContract.Effect.Navigation.Back -> navController.popBackStackFrom(from = sourceEntry)

                    is UIContract.Effect.Navigation.NavRoute -> {
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
private fun UISettingsScreen(
    state: UIContract.State,
    effectFlow: Flow<UIContract.Effect>?,
    onEventSent: (event: UIContract.Event) -> Unit,
    onNavigationRequested: (UIContract.Effect.Navigation) -> Unit,
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
                title = stringResource(R.string.settings_ui_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(UIContract.Effect.Navigation.Back) },
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
                    onNavigationRequested(UIContract.Effect.Navigation.Back)
                }

                else -> {
                    UISettingsScreenContent(
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
private fun UISettingsScreenContent(
    state: UIContract.State,
    onEventSent: (event: UIContract.Event) -> Unit,
    contentPadding: PaddingValues,
) {
    val nameSheet = rememberCustomModalBottomSheetState()
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
            title = { Text(text = stringResource(R.string.settings_cat_grill_name)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_naming_title)) },
            summary = { Text(text = getSummary(state.grillName)) },
            onClick = { nameSheet.open() },
        )
        FeatureGate(
            feature = Feature.ETACalculations,
        ) {
            PreferenceCategory(
                title = { Text(text = stringResource(R.string.settings_cat_dashboard)) },
            )
            SwitchPreference(
                value = state.etaCalculations,
                title = { Text(text = stringResource(R.string.settings_eta_title)) },
                summary = {
                    Text(
                        text = stringResource(R.string.settings_eta_summary),
                    )
                },
                onValueChange = { onEventSent(UIContract.Event.SetETACalculations(it)) },
            )
        }
    }
    BottomSheet(
        sheetState = nameSheet.sheetState,
    ) {
        InputValidationSheet(
            input = state.grillName,
            placeholder = stringResource(R.string.settings_naming_title),
            title = stringResource(R.string.settings_naming_title),
            onUpdate = {
                onEventSent(UIContract.Event.SetGrillName(it))
                nameSheet.close()
            },
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<UIContract.Effect>?,
    onNavigationRequested: (UIContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is UIContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is UIContract.Effect.Notification -> {
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
private fun UISettingsScreenPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                UISettingsScreen(
                    state = UIContract.State(
                        grillName = "Development",
                        etaCalculations = true,
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
