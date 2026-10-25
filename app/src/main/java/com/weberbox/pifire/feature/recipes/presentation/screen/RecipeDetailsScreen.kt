package com.weberbox.pifire.feature.recipes.presentation.screen

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.DataError
import com.weberbox.pifire.core.ui.components.HazeAppBar
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.LinearLoadingIndicator
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.components.sheets.ConfirmSheet
import com.weberbox.pifire.core.ui.state.CustomModalBottomSheetState
import com.weberbox.pifire.core.ui.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.fadeEnterTransition
import com.weberbox.pifire.core.ui.util.fadeExitTransition
import com.weberbox.pifire.core.ui.util.showAlerter
import com.weberbox.pifire.feature.home.presentation.screen.SHOW_DASHBOARD_KEY
import com.weberbox.pifire.feature.recipes.presentation.components.FloatingActionMenu
import com.weberbox.pifire.feature.recipes.presentation.components.RecipeAbout
import com.weberbox.pifire.feature.recipes.presentation.components.RecipeDescription
import com.weberbox.pifire.feature.recipes.presentation.components.RecipeIngredients
import com.weberbox.pifire.feature.recipes.presentation.components.RecipeInstructions
import com.weberbox.pifire.feature.recipes.presentation.components.RecipeSteps
import com.weberbox.pifire.feature.recipes.presentation.contract.DetailsContract
import com.weberbox.pifire.feature.recipes.presentation.model.FabItem.Action
import com.weberbox.pifire.feature.recipes.presentation.util.printRecipe
import com.weberbox.pifire.feature.recipes.presentation.util.shareRecipe
import com.weberbox.pifire.feature.setup.presentation.components.RecipeAsyncImage
import com.weberbox.pifire.navigation.NavGraph
import com.weberbox.pifire.navigation.navBackWithResultFrom
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RecipeDetailsScreenDestination(
    navController: NavHostController,
    viewModel: RecipeDetailsViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    RecipeDetailsScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is DetailsContract.Effect.Navigation.Back -> navController.popBackStackFrom(from = sourceEntry)

                is DetailsContract.Effect.Navigation.NavRoute ->
                    navController.navigateFrom(
                        from = sourceEntry,
                        route = navigationEffect.route,
                        popUpToRoute = navigationEffect.popUpToRoute,
                        inclusive = navigationEffect.inclusive,
                    )

                is DetailsContract.Effect.Navigation.DashHome -> {
                    // Return to the Home already on the back stack instead of recreating it
                    val homeEntry = runCatching {
                        navController.getBackStackEntry<NavGraph.HomeDest.DashHome>()
                    }.getOrNull()
                    if (homeEntry != null) {
                        homeEntry.savedStateHandle[SHOW_DASHBOARD_KEY] = true
                        navController.popBackStackFrom(
                            from = sourceEntry,
                            route = NavGraph.HomeDest.DashHome,
                            inclusive = false,
                        )
                    } else {
                        navController.navigateFrom(
                            from = sourceEntry,
                            route = NavGraph.HomeDest,
                            popUpToRoute = NavGraph.RecipesDest.Details::class,
                        )
                    }
                }

                is DetailsContract.Effect.Navigation.BackRefresh ->
                    navController.navBackWithResultFrom(
                        from = sourceEntry,
                        key = RecipesViewModel.REFRESH_KEY,
                        arg = true,
                    )
            }
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RecipeDetailsScreen(
    state: DetailsContract.State,
    effectFlow: Flow<DetailsContract.Effect>?,
    onEventSent: (event: DetailsContract.Event) -> Unit,
    onNavigationRequested: (DetailsContract.Effect.Navigation) -> Unit,
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val deleteSheet = rememberCustomModalBottomSheetState()
    val hazeState = rememberHazeState()
    val scrollState = rememberScrollState()

    HandleSideEffects(
        deleteSheet = deleteSheet,
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested,
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HazeAppBar(
                title = {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = state.recipeData.metadata.title,
                    )
                },
                scrollBehavior = scrollBehavior,
                hazeState = hazeState,
                onNavigate = { onNavigationRequested(DetailsContract.Effect.Navigation.Back) },
            )
        },
        floatingActionButton = {
            if (!state.isDataError && !state.isInitialLoading) {
                FloatingActionMenu(
                    scrollState = scrollState,
                    onFabAction = { action ->
                        when (action) {
                            Action.Delete -> onEventSent(DetailsContract.Event.DeleteRecipeDialog)
                            Action.Share -> onEventSent(DetailsContract.Event.ShareRecipe)
                            Action.Print -> onEventSent(DetailsContract.Event.PrintRecipe)
                            Action.Run -> onEventSent(DetailsContract.Event.RunRecipe)
                        }
                    },
                )
            }
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
                    onNavigationRequested(DetailsContract.Effect.Navigation.Back)
                }

                else -> {
                    RecipeDetailsContent(
                        state = state,
                        scrollState = scrollState,
                        contentPadding = contentPadding,
                        hazeState = hazeState,
                        onNavigationRequested = onNavigationRequested,
                    )
                    BottomSheet(
                        sheetState = deleteSheet.sheetState,
                    ) {
                        ConfirmSheet(
                            title = stringResource(R.string.dialog_confirm_action),
                            message = stringResource(
                                R.string.dialog_confirm_delete_item,
                                state.recipeData.metadata.title,
                            ),
                            negativeButtonText = stringResource(R.string.cancel),
                            positiveButtonText = stringResource(R.string.delete),
                            positiveButtonColor = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onError,
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                            onPositive = {
                                onEventSent(DetailsContract.Event.DeleteRecipe)
                                deleteSheet.close()
                            },
                            onNegative = { deleteSheet.close() },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailsContent(
    modifier: Modifier = Modifier,
    state: DetailsContract.State,
    scrollState: ScrollState,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    onNavigationRequested: (DetailsContract.Effect.Navigation) -> Unit,
) {
    val density = LocalDensity.current
    var scrollToPosition by remember { mutableIntStateOf(0) }
    var hasScrolled by rememberSaveable { mutableStateOf(false) }
    val carouselState = rememberCarouselState(
        itemCount = { state.recipeData.assets.size },
    )

    LaunchedEffect(Unit) {
        if (state.recipeStep != -1 && !hasScrolled) {
            hasScrolled = true
            delay(500.milliseconds)
            val paddingAdjustment = with(density) {
                contentPadding.calculateTopPadding().toPx()
            }
            scrollState.animateScrollTo(
                (scrollToPosition - paddingAdjustment).toInt().coerceAtLeast(0),
            )
        }
    }

    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding,
    )
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .hazeSource(hazeState)
            .padding(MaterialTheme.spacing.smallOne),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne),
    ) {
        if (state.recipeData.assets.isNotEmpty()) {
            HorizontalMultiBrowseCarousel(
                state = carouselState,
                modifier = Modifier
                    .height(300.dp)
                    .clip(MaterialTheme.shapes.large),
                preferredItemWidth = 600.dp,
                itemSpacing = MaterialTheme.spacing.small,
            ) { index ->
                val image = state.recipeImages.getOrNull(index)
                RecipeAsyncImage(
                    image = image,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = {
                                onNavigationRequested(
                                    DetailsContract.Effect.Navigation.NavRoute(
                                        NavGraph.RecipesDest.Images(
                                            filename = state.recipeData.recipeFilename,
                                            index = index,
                                        ),
                                    ),
                                )
                            },
                        )
                        .maskClip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        RecipeAbout(
            recipe = state.recipeData,
        )
        AnimatedVisibility(
            visible = state.recipeData.metadata.description.isNotBlank(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition(),
        ) {
            RecipeDescription(
                description = state.recipeData.metadata.description,
            )
        }
        AnimatedVisibility(
            visible = state.recipeData.ingredients.isNotEmpty(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition(),
        ) {
            RecipeIngredients(
                ingredients = state.recipeData.ingredients,
            )
        }
        AnimatedVisibility(
            visible = state.recipeData.instructions.isNotEmpty(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition(),
        ) {
            RecipeInstructions(
                modifier = Modifier.animateContentSize(),
                instructions = state.recipeData.instructions,
            )
        }
        AnimatedVisibility(
            visible = state.recipeData.steps.isNotEmpty(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition(),
        ) {
            RecipeSteps(
                steps = state.recipeData.steps,
                units = state.recipeData.metadata.units,
                currentStep = state.recipeStep,
                onStepPosition = { position -> scrollToPosition = position },
            )
        }
    }
}

@Composable
private fun HandleSideEffects(
    deleteSheet: CustomModalBottomSheetState,
    effectFlow: Flow<DetailsContract.Effect>?,
    onNavigationRequested: (DetailsContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is DetailsContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is DetailsContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error,
                    )
                }

                is DetailsContract.Effect.DeleteDialog -> deleteSheet.open()

                is DetailsContract.Effect.PrintRecipe -> printRecipe(effect.recipeData, context)

                is DetailsContract.Effect.ShareRecipe -> shareRecipe(effect.recipeData, context)
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
// @DeviceSizePreviews
private fun RecipeDetailsScreenPreview() {
    PiFireTheme {
        Surface {
            RecipeDetailsScreen(
                state = DetailsContract.State(
                    recipeData = buildRecipe()[0],
                    recipeStep = -1,
                    recipeImages = emptyList(),
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
