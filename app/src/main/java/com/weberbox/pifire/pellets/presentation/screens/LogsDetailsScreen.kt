package com.weberbox.pifire.pellets.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.screens.CachedDataError
import com.weberbox.pifire.common.presentation.screens.DetailsScreen
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.ConfirmSheet
import com.weberbox.pifire.common.presentation.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.pellets.presentation.component.LogItem
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.pellets.presentation.model.PelletsEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun LogsDetailsScreen(
    navController: NavHostController,
    viewModel: SharedDetailsViewModel = hiltViewModel()
) {
    LogsDetailsContent(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is PelletsContract.Effect.Navigation.Back ->
                    navController.popBackStack()

                is PelletsContract.Effect.Navigation.NavRoute -> {
                    navController.safeNavigate(
                        route = navigationEffect.route,
                        popUp = navigationEffect.popUp
                    )
                }
            }
        }
    )
}

@Composable
private fun LogsDetailsContent(
    state: PelletsContract.State,
    effectFlow: Flow<PelletsContract.Effect>?,
    onEventSent: (event: PelletsContract.Event) -> Unit,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit
) {
    val logDeleteSheet = rememberInputModalBottomSheetState<PelletLog>()

    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested
    )

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError }
    ) { state ->
        when {
            state.isInitialLoading -> InitialLoadingProgress()
            state.isDataError -> CachedDataError { onEventSent(PelletsContract.Event.Refresh) }
            else -> {
                DetailsScreen(
                    title = stringResource(R.string.pellets_log),
                    isLoading = state.isLoading,
                    onNavigate = {
                        onNavigationRequested(PelletsContract.Effect.Navigation.Back)
                    }
                ) {
                    items(items = state.pellets.logsList) { log ->
                        LogItem(
                            log = log,
                            isConnected = state.isConnected,
                            onEventSent = { event ->
                                when (event) {
                                    is PelletsContract.Event.DeleteLogDialog -> {
                                        logDeleteSheet.open(event.log)
                                    }

                                    else -> onEventSent(event)
                                }
                            }
                        )
                    }
                }
                BottomSheet(
                    sheetState = logDeleteSheet.sheetState
                ) {
                    ConfirmSheet(
                        title = stringResource(R.string.dialog_confirm_action),
                        message = stringResource(
                            R.string.dialog_confirm_delete_item,
                            logDeleteSheet.data.pelletName
                        ),
                        negativeButtonText = stringResource(R.string.cancel),
                        positiveButtonText = stringResource(R.string.delete),
                        positiveButtonColor = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onError,
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        onPositive = {
                            onEventSent(
                                PelletsContract.Event.SendEvent(
                                    PelletsEvent.DeleteLog(logDate = logDeleteSheet.data.logDate)
                                )
                            )
                            logDeleteSheet.close()
                        },
                        onNegative = { logDeleteSheet.close() }
                    )
                }
            }
        }
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<PelletsContract.Effect>?,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PelletsContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }

                is PelletsContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun LogsDetailsScreenPreview() {
    PiFireTheme {
        Surface {
            LogsDetailsContent(
                state = PelletsContract.State(
                    pellets = buildPellets(),
                    isInitialLoading = false,
                    isDataError = false,
                    isLoading = false,
                    isRefreshing = false,
                    isConnected = true
                ),
                effectFlow = null,
                onEventSent = { },
                onNavigationRequested = { }
            )
        }
    }
}