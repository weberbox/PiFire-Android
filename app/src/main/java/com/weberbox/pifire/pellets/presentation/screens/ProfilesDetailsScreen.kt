package com.weberbox.pifire.pellets.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.screens.CachedDataError
import com.weberbox.pifire.common.presentation.screens.DetailsScreen
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.ConfirmSheet
import com.weberbox.pifire.common.presentation.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.pellets.presentation.component.ProfileItem
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.pellets.presentation.model.PelletsEvent
import com.weberbox.pifire.pellets.presentation.model.ProfilesData
import com.weberbox.pifire.pellets.presentation.sheets.ProfilesEditSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun ProfilesDetailsScreen(
    navController: NavHostController,
    viewModel: SharedDetailsViewModel = hiltViewModel()
) {
    ProfilesDetailsContent(
        navController = navController,
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) }
    )
}

@Composable
private fun ProfilesDetailsContent(
    navController: NavHostController,
    state: PelletsContract.State,
    effectFlow: Flow<PelletsContract.Effect>?,
    onEventSent: (event: PelletsContract.Event) -> Unit
) {
    val activity = LocalActivity.current
    val profileEditSheet = rememberInputModalBottomSheetState<ProfilesData>()
    val profileDeleteSheet = rememberInputModalBottomSheetState<PelletProfile>()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PelletsContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }

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
                    title = stringResource(R.string.pellets_editor),
                    navController = navController,
                    isLoading = state.isLoading
                ) {
                    for (profile in state.pellets.profilesList) {
                        item {
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
                                                    comments = event.profile.comments
                                                )
                                            )
                                        }

                                        else -> onEventSent(event)
                                    }
                                }
                            )
                        }
                    }
                }
                BottomSheet(
                    sheetState = profileEditSheet.sheetState
                ) {
                    ProfilesEditSheet(
                        title = stringResource(R.string.pellets_editor),
                        profilesData = profileEditSheet.data,
                        onConfirm = { profile, _ ->
                            onEventSent(
                                PelletsContract.Event.SendEvent(
                                    PelletsEvent.EditProfile(
                                        profile = profile
                                    )
                                )
                            )
                            profileEditSheet.close()
                        },
                        onDismiss = { profileEditSheet.close() }
                    )
                }
                BottomSheet(
                    sheetState = profileDeleteSheet.sheetState
                ) {
                    ConfirmSheet(
                        title = stringResource(R.string.dialog_confirm_action),
                        message = stringResource(
                            R.string.dialog_confirm_delete_item,
                            profileDeleteSheet.data.brand + " " + profileDeleteSheet.data.wood
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
                                    PelletsEvent.DeleteProfile(profile = profileDeleteSheet.data.id)
                                )
                            )
                            profileDeleteSheet.close()
                        },
                        onNegative = { profileDeleteSheet.close() }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ProfilesDetailsScreenPreview() {
    PiFireTheme {
        Surface {
            ProfilesDetailsContent(
                navController = rememberNavController(),
                state = PelletsContract.State(
                    pellets = buildPellets(),
                    isInitialLoading = false,
                    isDataError = false,
                    isLoading = false,
                    isRefreshing = false,
                    isConnected = true
                ),
                effectFlow = null,
                onEventSent = { }
            )
        }
    }
}