package com.weberbox.pifire.feature.pellets.presentation.screen

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.CachedDataError
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.LinearLoadingIndicator
import com.weberbox.pifire.core.ui.components.PullToRefresh
import com.weberbox.pifire.core.ui.modifier.isElementVisible
import com.weberbox.pifire.core.ui.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.core.ui.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.UiMessage
import com.weberbox.pifire.core.ui.util.showMessage
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsBottomSheets
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsBrands
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsCurrent
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsLevel
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsLog
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsProfiles
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsUsage
import com.weberbox.pifire.feature.pellets.presentation.components.PelletsWoods
import com.weberbox.pifire.feature.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.feature.pellets.presentation.model.ProfilesData
import com.weberbox.pifire.navigation.NavGraph
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun PelletsScreenDestination(
    navController: NavHostController,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    viewModel: PelletsViewModel? = null,
) = when {
    LocalInspectionMode.current -> {
        PelletsScreenPreview(
            contentPadding = contentPadding,
            hazeState = hazeState,
        )
    }

    else -> {
        PelletsScreen(
            navController = navController,
            contentPadding = contentPadding,
            hazeState = hazeState,
            viewModel = viewModel ?: hiltViewModel(),
        )
    }
}

@Composable
private fun PelletsScreen(
    navController: NavHostController,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    viewModel: PelletsViewModel,
) {
    val sourceEntry = rememberSourceEntry()
    var isVisibleOnScreen by remember { mutableStateOf(false) }
    val onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit = { navigationEffect ->
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
    }

    // Only notify while this pager page is on screen
    HandleSideEffects(
        isVisibleOnScreen = isVisibleOnScreen,
        effectFlow = viewModel.effect,
        onNavigationRequested = onNavigationRequested,
    )

    PelletsScreenContent(
        hazeState = hazeState,
        contentPadding = contentPadding,
        state = viewModel.viewState.value,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = onNavigationRequested,
        onVisibilityChanged = { isVisibleOnScreen = it },
    )
}

@Composable
private fun PelletsScreenContent(
    hazeState: HazeState,
    contentPadding: PaddingValues,
    state: PelletsContract.State,
    onEventSent: (event: PelletsContract.Event) -> Unit,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
    onVisibilityChanged: (Boolean) -> Unit,
) {
    val scrollState = rememberScrollState()
    val currentSheet = rememberCustomModalBottomSheetState()
    val brandAddSheet = rememberCustomModalBottomSheetState()
    val brandDeleteSheet = rememberInputModalBottomSheetState<String>()
    val woodAddSheet = rememberCustomModalBottomSheetState()
    val woodDeleteSheet = rememberInputModalBottomSheetState<String>()
    val profileAddSheet = rememberInputModalBottomSheetState<ProfilesData>()
    val profileEditSheet = rememberInputModalBottomSheetState<ProfilesData>()
    val profileDeleteSheet = rememberInputModalBottomSheetState<PelletProfile>()
    val logDeleteSheet = rememberInputModalBottomSheetState<PelletLog>()
    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        modifier = Modifier.isElementVisible(onVisibilityChanged),
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError },
    ) { state ->
        when {
            state.isInitialLoading -> InitialLoadingProgress()

            state.isDataError -> CachedDataError { onEventSent(PelletsContract.Event.Refresh) }

            else -> {
                PullToRefresh(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onEventSent(PelletsContract.Event.Refresh) },
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne),
                        modifier = Modifier
                            .hazeSource(state = hazeState)
                            .verticalScroll(scrollState)
                            .padding(contentPadding)
                            .padding(MaterialTheme.spacing.smallOne)
                            .fillMaxSize(),
                    ) {
                        PelletsLevel(
                            pellets = state.pellets,
                            isConnected = state.isConnected,
                            onEventSent = onEventSent,
                        )
                        PelletsUsage(pellets = state.pellets)
                        PelletsCurrent(
                            pellets = state.pellets,
                            isConnected = state.isConnected,
                            onEventSent = { event ->
                                when (event) {
                                    is PelletsContract.Event.CurrentDialog -> currentSheet.open()
                                    else -> onEventSent(event)
                                }
                            },
                        )
                        PelletsBrands(
                            pellets = state.pellets,
                            isConnected = state.isConnected,
                            onEventSent = { event ->
                                when (event) {
                                    is PelletsContract.Event.AddBrandDialog -> {
                                        brandAddSheet.open()
                                    }

                                    is PelletsContract.Event.DeleteBrandDialog -> {
                                        brandDeleteSheet.open(event.brand)
                                    }

                                    is PelletsContract.Event.BrandsViewAll -> {
                                        onNavigationRequested(
                                            PelletsContract.Effect.Navigation.NavRoute(
                                                route = NavGraph.HomeDest.BrandsDetails,
                                            ),
                                        )
                                    }

                                    else -> onEventSent(event)
                                }
                            },
                        )
                        PelletsWoods(
                            pellets = state.pellets,
                            isConnected = state.isConnected,
                            onEventSent = { event ->
                                when (event) {
                                    is PelletsContract.Event.AddWoodDialog -> {
                                        woodAddSheet.open()
                                    }

                                    is PelletsContract.Event.DeleteWoodDialog -> {
                                        woodDeleteSheet.open(event.wood)
                                    }

                                    is PelletsContract.Event.WoodsViewAll -> {
                                        onNavigationRequested(
                                            PelletsContract.Effect.Navigation.NavRoute(
                                                route = NavGraph.HomeDest.WoodsDetails,
                                            ),
                                        )
                                    }

                                    else -> onEventSent(event)
                                }
                            },
                        )
                        PelletsProfiles(
                            pellets = state.pellets,
                            isConnected = state.isConnected,
                            onEventSent = { event ->
                                when (event) {
                                    is PelletsContract.Event.AddProfileDialog -> {
                                        profileAddSheet.open(
                                            ProfilesData(
                                                brands = state.pellets.brandsList,
                                                woods = state.pellets.woodsList,
                                            ),
                                        )
                                    }

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

                                    is PelletsContract.Event.ProfilesViewAll -> {
                                        onNavigationRequested(
                                            PelletsContract.Effect.Navigation.NavRoute(
                                                route = NavGraph.HomeDest.ProfilesDetails,
                                            ),
                                        )
                                    }

                                    else -> onEventSent(event)
                                }
                            },
                        )
                        PelletsLog(
                            pellets = state.pellets,
                            isConnected = state.isConnected,
                            onEventSent = { event ->
                                when (event) {
                                    is PelletsContract.Event.DeleteLogDialog -> {
                                        logDeleteSheet.open(event.log)
                                    }

                                    is PelletsContract.Event.LogsViewAll -> {
                                        onNavigationRequested(
                                            PelletsContract.Effect.Navigation.NavRoute(
                                                route = NavGraph.HomeDest.LogsDetails,
                                            ),
                                        )
                                    }

                                    else -> onEventSent(event)
                                }
                            },
                        )
                    }
                    LinearLoadingIndicator(
                        isLoading = state.isLoading,
                        contentPadding = contentPadding,
                    )
                }
                PelletsBottomSheets(
                    state = state,
                    currentSheet = currentSheet,
                    brandAddSheet = brandAddSheet,
                    brandDeleteSheet = brandDeleteSheet,
                    woodAddSheet = woodAddSheet,
                    woodDeleteSheet = woodDeleteSheet,
                    profileAddSheet = profileAddSheet,
                    profileEditSheet = profileEditSheet,
                    profileDeleteSheet = profileDeleteSheet,
                    logDeleteSheet = logDeleteSheet,
                    onEventSent = onEventSent,
                )
            }
        }
    }
}

@Composable
private fun HandleSideEffects(
    isVisibleOnScreen: Boolean,
    effectFlow: Flow<PelletsContract.Effect>?,
    onNavigationRequested: (PelletsContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY, isVisibleOnScreen) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PelletsContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is PelletsContract.Effect.Notification -> {
                    if (isVisibleOnScreen) {
                        activity?.showMessage(
                            message = UiMessage.of(text = effect.text, isError = effect.error),
                        )
                    }
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
internal fun PelletsScreenPreview(
    contentPadding: PaddingValues = PaddingValues(),
    hazeState: HazeState = rememberHazeState(),
) {
    PiFireTheme {
        Surface {
            PelletsScreenContent(
                hazeState = hazeState,
                contentPadding = contentPadding,
                state = PelletsContract.State(
                    pellets = buildPellets(),
                    isInitialLoading = false,
                    isDataError = false,
                    isLoading = true,
                    isRefreshing = false,
                    isConnected = true,
                ),
                onVisibilityChanged = {},
                onEventSent = {},
                onNavigationRequested = { },
            )
        }
    }
}

internal fun buildPellets(): Pellets = Pellets(
    currentPelletId = "Test",
    hopperLevel = 63,
    estUsageGrams = 4.51,
    currentBrand = "Generic",
    currentWood = "Alder",
    currentRating = 4,
    dateLoaded = "12/21 9:48pm",
    currentComments = "This is a placeholder profile. Alder is generic and used in " +
        "almost all pellets, regardless of the wood type indicated on the " +
        "packaging. It tends to burn consistently and produces mild smoke.",
    brandsList = listOf(
        "Generic",
        "Custom",
    ),
    woodsList = listOf(
        "Alder",
        "Almond",
        "Apple",
    ),
    profilesList = listOf(
        PelletProfile(
            brand = "Generic",
            wood = "Alder",
        ),
        PelletProfile(
            brand = "Generic",
            wood = "Plum",
        ),
    ),
    logsList = listOf(
        PelletLog(
            pelletID = "12/09",
            pelletName = "Generic Alder",
            pelletRating = 4,
        ),
        PelletLog(
            pelletID = "12/21",
            pelletName = "Generic Alder",
            pelletRating = 5,
        ),
        PelletLog(
            pelletID = "12/21",
            pelletName = "Generic Alder",
            pelletRating = 3,
        ),
    ),
)
