package com.weberbox.pifire.landing.presentation.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.base.gradientBackground
import com.weberbox.pifire.common.presentation.component.CircularLoadingIndicator
import com.weberbox.pifire.common.presentation.component.EmptyItems
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.ConfirmSheet
import com.weberbox.pifire.common.presentation.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.util.BiometricPromptManager
import com.weberbox.pifire.core.util.rememberBiometricPromptManager
import com.weberbox.pifire.landing.presentation.component.LandingTopBar
import com.weberbox.pifire.landing.presentation.component.ServerItem
import com.weberbox.pifire.landing.presentation.contract.LandingContract
import com.weberbox.pifire.landing.presentation.model.ServerData
import com.weberbox.pifire.landing.presentation.model.ServerData.Server
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun LandingScreenDestination(
    navController: NavHostController,
    viewModel: LandingViewModel = hiltViewModel()
) {
    val activity = LocalActivity.current
    LandingScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is LandingContract.Effect.Navigation.Back -> activity?.finish()
                is LandingContract.Effect.Navigation.NavRoute -> {
                    navController.safeNavigate(
                        route = navigationEffect.route,
                        popUp = navigationEffect.popUp
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LandingScreen(
    state: LandingContract.State,
    effectFlow: Flow<LandingContract.Effect>?,
    onEventSent: (event: LandingContract.Event) -> Unit,
    onNavigationRequested: (LandingContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current as? AppCompatActivity
    val windowInsets = WindowInsets.safeDrawing
    val hazeState = rememberHazeState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val serverDeleteSheet = rememberInputModalBottomSheetState<Pair<String, String>>()
    val scrollState = rememberLazyListState()
    var fabVisible by remember { mutableStateOf(true) }
    var previousIndex by remember { mutableIntStateOf(0) }
    var previousScrollOffset by remember { mutableIntStateOf(0) }
    val biometricManager = rememberBiometricPromptManager()

    HandleSideEffects(
        activity = activity,
        biometricManager = biometricManager,
        effectFlow = effectFlow,
        onEventSent = onEventSent,
        onNavigationRequested = onNavigationRequested
    )

    LaunchedEffect(scrollState) {
        snapshotFlow {
            scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val scrollingDown = when {
                index > previousIndex -> true
                index == previousIndex && offset > previousScrollOffset -> true
                else -> false
            }
            fabVisible = !scrollingDown
            previousIndex = index
            previousScrollOffset = offset
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground()),
        topBar = {
            LandingTopBar(
                state = state,
                hazeState = hazeState,
                onSearchUpdated = { searchQuery = it },
                onNavigationRequested = onNavigationRequested
            )
        },
        floatingActionButton = {
            SmallExtendedFloatingActionButton(
                modifier = Modifier
                    .animateFloatingActionButton(
                        visible = fabVisible,
                        alignment = Alignment.BottomEnd
                    ),
                onClick = {
                    onNavigationRequested(
                        LandingContract.Effect.Navigation.NavRoute(
                            route = NavGraph.ServerSetup,
                            popUp = true
                        )
                    )
                },
                text = { Text(text = stringResource(R.string.landing_add_server)) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.landing_add_server)
                    )
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
                    onNavigationRequested(LandingContract.Effect.Navigation.Back)
                }

                else -> {
                    val filteredList = remember(searchQuery, state.serverData) {
                        state.serverData.servers.filter { server ->
                            server.name.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    LazyColumn(
                        state = scrollState,
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(
                            MaterialTheme.spacing.extraSmall
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .hazeSource(state = hazeState)
                            .padding(
                                horizontal = MaterialTheme.spacing.smallOne,
                                vertical = MaterialTheme.spacing.extraSmall
                            )
                            .animateEnterExit()
                            .fillMaxSize(),
                    ) {
                        items(
                            items = filteredList,
                            key = { it.uuid }
                        ) { server ->
                            ServerItem(
                                server = server,
                                onClick = { uuid ->
                                    if (server.online) {
                                        checkAuthentication(
                                            state = state,
                                            biometricManager = biometricManager,
                                            onSuccess = {
                                                onEventSent(
                                                    LandingContract.Event.SelectServer(
                                                        uuid = uuid
                                                    )
                                                )
                                            }
                                        )
                                    } else {
                                        activity?.showAlerter(
                                            message = R.string.alerter_server_offline,
                                            isError = true
                                        )
                                    }
                                },
                                onEdit = { uuid ->
                                    checkAuthentication(
                                        state = state,
                                        biometricManager = biometricManager,
                                        onSuccess = {
                                            onNavigationRequested(
                                                LandingContract.Effect.Navigation.NavRoute(
                                                    route = NavGraph.LandingDest.ServerSettings(
                                                        uuid = uuid
                                                    )
                                                )
                                            )
                                        }
                                    )
                                },
                                onDelete = { uuid, name ->
                                    checkAuthentication(
                                        state = state,
                                        biometricManager = biometricManager,
                                        onSuccess = {
                                            serverDeleteSheet.open(
                                                data = Pair(
                                                    first = uuid,
                                                    second = name
                                                )
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = filteredList.isEmpty(),
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        EmptyItems(
                            title = stringResource(R.string.error_no_results_landing_title),
                            summary = stringResource(R.string.error_no_results_landing_summary),
                            icon = Icons.Filled.OutdoorGrill
                        )
                    }

                    BottomSheet(
                        sheetState = serverDeleteSheet.sheetState
                    ) {
                        ConfirmSheet(
                            title = stringResource(R.string.dialog_confirm_action),
                            message = stringResource(
                                R.string.dialog_confirm_delete_item,
                                serverDeleteSheet.data.second
                            ),
                            negativeButtonText = stringResource(R.string.cancel),
                            positiveButtonText = stringResource(R.string.delete),
                            positiveButtonColor = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onError,
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            onPositive = {
                                onEventSent(
                                    LandingContract.Event.DeleteServer(
                                        serverDeleteSheet.data.first
                                    )
                                )
                                serverDeleteSheet.close()
                            },
                            onNegative = { serverDeleteSheet.close() }
                        )
                    }

                    CircularLoadingIndicator(
                        isLoading = state.isLoading
                    )
                }
            }
        }
    }
}

private fun checkAuthentication(
    state: LandingContract.State,
    biometricManager: BiometricPromptManager?,
    onSuccess: () -> Unit,
) {
    if (state.biometricServerPrompt) {
        biometricManager?.authenticate(
            onAuthenticationSuccess = onSuccess
        )
    } else {
        onSuccess()
    }
}

@Composable
private fun HandleSideEffects(
    activity: Activity?,
    biometricManager: BiometricPromptManager?,
    effectFlow: Flow<LandingContract.Effect>?,
    onEventSent: (event: LandingContract.Event) -> Unit,
    onNavigationRequested: (LandingContract.Effect.Navigation) -> Unit
) {
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is LandingContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is LandingContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }

                is LandingContract.Effect.BiometricServerPrompt -> {
                    biometricManager?.authenticate(
                        onAuthenticationSuccess = {
                            onEventSent(
                                LandingContract.Event.SelectServer(effect.uuid)
                            )
                        }
                    )
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, apiLevel = 35)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, apiLevel = 35)
//@DeviceSizePreviews
private fun LandingScreenPreview() {
    PiFireTheme {
        Surface {
            LandingScreen(
                state = LandingContract.State(
                    serverData = buildServerData(),
                    isInitialLoading = false,
                    isLoading = false,
                    isDataError = false,
                    biometricServerPrompt = false
                ),
                effectFlow = null,
                onEventSent = { },
                onNavigationRequested = { }
            )
        }
    }
}

internal fun buildServerData(): ServerData {
    return ServerData(
        servers = listOf(
            Server(
                uuid = "12345",
                name = "Development",
                address = "http://pifire.local.com/testing/server",
                online = true
            ),
            Server(
                uuid = "23456",
                name = "Development 2",
                address = "http://pifire.com",
                online = false
            )
        )
    )
}