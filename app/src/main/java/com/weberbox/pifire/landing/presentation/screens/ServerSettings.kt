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
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputStateSheet
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.landing.presentation.contract.ServerContract
import com.weberbox.pifire.landing.presentation.sheets.CredentialsSheet
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.BasicAuth
import com.weberbox.pifire.settings.presentation.component.PreferenceWarning
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.TwoTargetSwitchPreference

@Composable
fun ServerSettingsDestination(
    navController: NavHostController,
    viewModel: ServerViewModel = hiltViewModel()
) {
    ProvidePreferenceLocals {
        ServerSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is ServerContract.Effect.Navigation.Back -> navController.popBackStack()
                    is ServerContract.Effect.Navigation.NavRoute -> {
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
private fun ServerSettings(
    state: ServerContract.State,
    effectFlow: Flow<ServerContract.Effect>?,
    onEventSent: (event: ServerContract.Event) -> Unit,
    onNavigationRequested: (ServerContract.Effect.Navigation) -> Unit
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
                        text = stringResource(R.string.settings_server),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(ServerContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(ServerContract.Effect.Navigation.Back)
                }

                else -> {
                    ServerSettingsContent(
                        state = state,
                        contentPadding = contentPadding,
                        onEventSent = onEventSent,
                        onNavigationRequested = onNavigationRequested
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerSettingsContent(
    state: ServerContract.State,
    contentPadding: PaddingValues,
    onEventSent: (event: ServerContract.Event) -> Unit,
    onNavigationRequested: (ServerContract.Effect.Navigation) -> Unit
) {
    val credentialsSheet = rememberCustomModalBottomSheetState()
    val addressSheet = rememberCustomModalBottomSheetState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_server_title)) }
        )
        Preference(
            title = {
                Text(text = stringResource(R.string.settings_server_address))
            },
            summary = { Text(text = state.serverData.address) },
            onClick = { addressSheet.open() }
        )
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_basic_auth_title)) }
        )
        TwoTargetSwitchPreference(
            value = state.serverData.credentialsEnabled,
            title = { Text(text = stringResource(R.string.settings_basic_auth_title)) },
            summary = { Text(text = stringResource(R.string.settings_basic_auth_summary)) },
            onClick = { credentialsSheet.open() },
            onValueChange = { onEventSent(ServerContract.Event.EnableBasicAuth(it)) }
        )
        TwoTargetSwitchPreference(
            value = state.serverData.headersEnabled,
            title = { Text(text = stringResource(R.string.settings_extra_headers_title)) },
            summary = { Text(text = stringResource(R.string.settings_extra_headers_summary)) },
            onClick = {
                onNavigationRequested(
                    ServerContract.Effect.Navigation.NavRoute(
                        NavGraph.LandingDest.HeaderSettings(state.serverData.uuid)
                    )
                )
            },
            onValueChange = { onEventSent(ServerContract.Event.EnableHeaders(it)) }
        )
        PreferenceWarning(stringResource(R.string.settings_auth_warning))
    }
    BottomSheet(
        sheetState = addressSheet.sheetState
    ) {
        InputStateSheet(
            inputState = state.serverAddress,
            title = stringResource(R.string.settings_server_address),
            placeholder = stringResource(R.string.settings_server_address),
            onValueChange = { onEventSent(ServerContract.Event.ValidateAddress(it)) },
            onDismiss = { addressSheet.close() },
            onConfirm = {
                addressSheet.close()
                onEventSent(ServerContract.Event.UpdateAddress(it))
            }
        )
    }
    BottomSheet(
        sheetState = credentialsSheet.sheetState
    ) {
        CredentialsSheet(
            basicAuth = state.basicAuth,
            onAction = {
                onEventSent(ServerContract.Event.UpdateBasicAuth(it))
                credentialsSheet.close()
            }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<ServerContract.Effect>?,
    onNavigationRequested: (ServerContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ServerContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is ServerContract.Effect.Notification -> {
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
private fun ServerSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceLocals {
            Surface {
                ServerSettings(
                    state = ServerContract.State(
                        serverData = Server(
                            address = "https://pifire.local"
                        ),
                        basicAuth = BasicAuth(),
                        serverAddress = InputState(),
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