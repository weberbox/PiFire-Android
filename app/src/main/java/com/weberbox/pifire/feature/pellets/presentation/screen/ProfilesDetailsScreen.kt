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
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.CachedDataError
import com.weberbox.pifire.core.ui.components.DetailsScreen
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.components.sheets.ConfirmSheet
import com.weberbox.pifire.core.ui.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.UiMessage
import com.weberbox.pifire.core.ui.util.showMessage
import com.weberbox.pifire.feature.pellets.presentation.components.ProfileItem
import com.weberbox.pifire.feature.pellets.presentation.components.sheets.ProfilesEditSheet
import com.weberbox.pifire.feature.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.feature.pellets.presentation.model.PelletsEvent
import com.weberbox.pifire.feature.pellets.presentation.model.ProfilesData
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun ProfilesDetailsScreenDestination(
    navController: NavHostController,
    viewModel: SharedDetailsViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    ProfilesDetailsScreen(
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
private fun ProfilesDetailsScreen(
    state: PelletsContract.State,
    effectFlow: Flow<PelletsContract.Effect>?,
    onEventSent: (event: PelletsContract.Event) -> Unit,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
) {
    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested,
    )

    ProfilesDetailsScreenContent(
        state = state,
        onEventSent = onEventSent,
        onNavigationRequested = onNavigationRequested,
    )
}

@Composable
private fun ProfilesDetailsScreenContent(
    state: PelletsContract.State,
    onEventSent: (event: PelletsContract.Event) -> Unit,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
) {
    val profileEditSheet = rememberInputModalBottomSheetState<ProfilesData>()
    val profileDeleteSheet = rememberInputModalBottomSheetState<PelletProfile>()

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError },
    ) { state ->
        when {
            state.isInitialLoading -> InitialLoadingProgress()

            state.isDataError -> CachedDataError { onEventSent(PelletsContract.Event.Refresh) }

            else -> {
                DetailsScreen(
                    title = stringResource(R.string.pellets_editor),
                    isLoading = state.isLoading,
                    onNavigate = {
                        onNavigationRequested(PelletsContract.Effect.Navigation.Back)
                    },
                ) {
                    items(items = state.pellets.profilesList.withUniqueKeys { it.id }, key = { it.key }) { (_, profile) ->
                        ProfileItem(
                            profile = profile,
                            isConnected = state.isConnected,
                            onEventSent = { event ->
                                when (event) {
                                    is PelletsContract.Event.DeleteProfileDialog -> {
                                        profileDeleteSheet.open(event.profile)
                                    }

                                    is PelletsContract.Event.EditProfileDialog -> {
                                        profileEditSheet.open(
                                            ProfilesData(
                                                brands = state.pellets.brandsList,
                                                woods = state.pellets.woodsList,
                                                id = event.profile.id,
                                                currentBrand = event.profile.brand,
                                                currentWood = event.profile.wood,
                                                rating = event.profile.rating,
                                                comments = event.profile.comments,
                                            ),
                                        )
                                    }

                                    else -> onEventSent(event)
                                }
                            },
                        )
                    }
                }
                BottomSheet(
                    sheetState = profileEditSheet.sheetState,
                ) {
                    ProfilesEditSheet(
                        title = stringResource(R.string.pellets_editor),
                        profilesData = profileEditSheet.data,
                        onConfirm = { profile, _ ->
                            onEventSent(
                                PelletsContract.Event.SendEvent(
                                    PelletsEvent.EditProfile(
                                        profile = profile,
                                    ),
                                ),
                            )
                            profileEditSheet.close()
                        },
                        onDismiss = { profileEditSheet.close() },
                    )
                }
                BottomSheet(
                    sheetState = profileDeleteSheet.sheetState,
                ) {
                    ConfirmSheet(
                        title = stringResource(R.string.dialog_confirm_action),
                        message = stringResource(
                            R.string.dialog_confirm_delete_item,
                            profileDeleteSheet.data.brand + " " + profileDeleteSheet.data.wood,
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
                                    PelletsEvent.DeleteProfile(profile = profileDeleteSheet.data.id),
                                ),
                            )
                            profileDeleteSheet.close()
                        },
                        onNegative = { profileDeleteSheet.close() },
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
                    activity?.showMessage(
                        message = UiMessage.of(text = effect.text, isError = effect.error),
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
private fun ProfilesDetailsScreenPreview() {
    PiFireTheme {
        Surface {
            ProfilesDetailsScreenContent(
                state = PelletsContract.State(
                    pellets = buildPellets(),
                    isInitialLoading = false,
                    isDataError = false,
                    isLoading = false,
                    isRefreshing = false,
                    isConnected = true,
                ),
                onEventSent = { },
                onNavigationRequested = { },
            )
        }
    }
}
