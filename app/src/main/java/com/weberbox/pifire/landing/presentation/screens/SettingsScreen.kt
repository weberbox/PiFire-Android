package com.weberbox.pifire.landing.presentation.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.util.rememberBiometricPromptManager
import com.weberbox.pifire.landing.presentation.contract.SettingsContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference

@Composable
fun SettingsScreenDestination(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceLocals {
        SettingsScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is SettingsContract.Effect.Navigation.Back -> navController.popBackStack()
                    is SettingsContract.Effect.Navigation.NavRoute -> {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    state: SettingsContract.State,
    effectFlow: Flow<SettingsContract.Effect>?,
    onEventSent: (event: SettingsContract.Event) -> Unit,
    onNavigationRequested: (SettingsContract.Effect.Navigation) -> Unit
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
                        text = stringResource(R.string.nav_settings),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = {
                    onNavigationRequested(SettingsContract.Effect.Navigation.Back)
                }
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
                    onNavigationRequested(SettingsContract.Effect.Navigation.Back)
                }

                else -> {
                    SettingsScreenContent(
                        state = state,
                        contentPadding = contentPadding,
                        onEventSent = onEventSent
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreenContent(
    state: SettingsContract.State,
    contentPadding: PaddingValues,
    onEventSent: (event: SettingsContract.Event) -> Unit
) {
    val biometricManager = rememberBiometricPromptManager()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        PreferenceCategory(
            title = {
                Text(
                    text = stringResource(R.string.landing_cat_auto_select)
                )
            }
        )
        SwitchPreference(
            value = state.autSelectEnabled,
            title = {
                Text(
                    text = stringResource(R.string.landing_select_online_title)
                )
            },
            summary = {
                Text(
                    text = stringResource(
                        R.string.landing_select_online_summary
                    )
                )
            },
            onValueChange = {
                onEventSent(SettingsContract.Event.AutoSelectEnabled(it))
            }
        )
        PreferenceCategory(
            title = {
                Text(
                    text = stringResource(R.string.settings_cat_biometrics_title)
                )
            }
        )
        SwitchPreference(
            value = state.biometricsEnabled,
            title = {
                Text(
                    text = stringResource(
                        R.string.settings_server_biometrics_title
                    )
                )
            },
            summary = {
                Text(
                    text = stringResource(R.string.settings_server_biometrics_summary)
                )
            },
            onValueChange = { enabled ->
                biometricManager?.authenticate(
                    onAuthenticationSuccess = {
                        onEventSent(SettingsContract.Event.BiometricsEnabled(enabled))
                    }
                )
            }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<SettingsContract.Effect>?,
    onNavigationRequested: (SettingsContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is SettingsContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is SettingsContract.Effect.Notification -> {
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
private fun SettingsScreenPreview() {
    PiFireTheme {
        Surface {
            ProvidePreferenceLocals {
                SettingsScreen(
                    state = SettingsContract.State(
                        autSelectEnabled = true,
                        biometricsEnabled = true,
                        isInitialLoading = false,
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