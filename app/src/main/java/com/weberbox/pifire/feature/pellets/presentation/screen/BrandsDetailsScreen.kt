package com.weberbox.pifire.feature.pellets.presentation.screen

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.util.withUniqueKeys
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.DataError
import com.weberbox.pifire.core.ui.components.DetailsScreen
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.components.sheets.ConfirmSheet
import com.weberbox.pifire.core.ui.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.showAlerter
import com.weberbox.pifire.feature.pellets.presentation.components.WoodBrandItem
import com.weberbox.pifire.feature.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.feature.pellets.presentation.model.PelletsEvent
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun BrandsDetailsScreen(
    navController: NavHostController,
    viewModel: SharedDetailsViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    BrandsDetailsContent(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is PelletsContract.Effect.Navigation.Back ->
                    navController.popBackStackFrom(from = sourceEntry)

                is PelletsContract.Effect.Navigation.NavRoute -> {
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

@Composable
private fun BrandsDetailsContent(
    state: PelletsContract.State,
    effectFlow: Flow<PelletsContract.Effect>?,
    onEventSent: (event: PelletsContract.Event) -> Unit,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
) {
    val brandDeleteSheet = rememberInputModalBottomSheetState<String>()

    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested,
    )

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError },
    ) { state ->
        when {
            state.isInitialLoading -> InitialLoadingProgress()

            state.isDataError -> DataError { onEventSent(PelletsContract.Event.Refresh) }

            else -> {
                DetailsScreen(
                    title = stringResource(R.string.pellets_brands),
                    isLoading = state.isLoading,
                    onNavigate = {
                        onNavigationRequested(PelletsContract.Effect.Navigation.Back)
                    },
                ) {
                    items(items = state.pellets.brandsList.withUniqueKeys { it }, key = { it.key }) { (_, brand) ->
                        WoodBrandItem(
                            item = brand,
                            onItemDelete = {
                                if (state.isConnected) {
                                    brandDeleteSheet.open(brand)
                                }
                            },
                        )
                    }
                }
                BottomSheet(
                    sheetState = brandDeleteSheet.sheetState,
                ) {
                    ConfirmSheet(
                        title = stringResource(R.string.dialog_confirm_action),
                        message = stringResource(
                            R.string.dialog_confirm_delete_item,
                            brandDeleteSheet.data,
                        ),
                        negativeButtonText = stringResource(R.string.cancel),
                        positiveButtonText = stringResource(R.string.delete),
                        positiveButtonColor = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onError,
                            containerColor = MaterialTheme.colorScheme.error,
                        ),
                        onPositive = {
                            onEventSent(
                                PelletsContract.Event.SendEvent(
                                    PelletsEvent.DeleteBrand(brand = brandDeleteSheet.data),
                                ),
                            )
                            brandDeleteSheet.close()
                        },
                        onNegative = { brandDeleteSheet.close() },
                    )
                }
            }
        }
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<PelletsContract.Effect>?,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PelletsContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error,
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
private fun BrandsDetailsScreenPreview() {
    PiFireTheme {
        Surface {
            BrandsDetailsContent(
                state = PelletsContract.State(
                    pellets = buildPellets(),
                    isInitialLoading = false,
                    isDataError = false,
                    isLoading = false,
                    isRefreshing = false,
                    isConnected = true,
                ),
                effectFlow = null,
                onEventSent = { },
                onNavigationRequested = { },
            )
        }
    }
}
