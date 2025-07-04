package com.weberbox.pifire.recipes.presentation.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.HazeAppBar
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.ConfirmSheet
import com.weberbox.pifire.common.presentation.state.CustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.common.presentation.util.navBackWithResult
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.recipes.presentation.component.FloatingActionMenu
import com.weberbox.pifire.recipes.presentation.component.RecipeAbout
import com.weberbox.pifire.recipes.presentation.component.RecipeDescription
import com.weberbox.pifire.recipes.presentation.component.RecipeIngredients
import com.weberbox.pifire.recipes.presentation.component.RecipeInstructions
import com.weberbox.pifire.recipes.presentation.component.RecipeSteps
import com.weberbox.pifire.recipes.presentation.contract.DetailsContract
import com.weberbox.pifire.recipes.presentation.model.FabItem.Action
import com.weberbox.pifire.recipes.presentation.util.printRecipe
import com.weberbox.pifire.recipes.presentation.util.shareRecipe
import com.weberbox.pifire.setup.presentation.component.RecipeAsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun RecipeDetailsScreenDestination(
    navController: NavHostController,
    viewModel: RecipeDetailsViewModel = hiltViewModel(),
) {
    RecipeDetailsScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is DetailsContract.Effect.Navigation.Back -> navController.popBackStack()

                is DetailsContract.Effect.Navigation.NavRoute ->
                    navController.safeNavigate(
                        route = navigationEffect.route,
                        popUp = navigationEffect.popUp
                    )

                is DetailsContract.Effect.Navigation.DashHome ->
                    navController.safeNavigate(
                        route = NavGraph.HomeDest,
                        builder = {
                            popUpTo(NavGraph.HomeDest) {
                                inclusive = true
                            }
                        }
                    )

                is DetailsContract.Effect.Navigation.BackRefresh ->
                    navController.navBackWithResult("refresh", true)
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RecipeDetailsScreen(
    state: DetailsContract.State,
    effectFlow: Flow<DetailsContract.Effect>?,
    onEventSent: (event: DetailsContract.Event) -> Unit,
    onNavigationRequested: (DetailsContract.Effect.Navigation) -> Unit
) {
    val context = LocalContext.current
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val deleteSheet = rememberCustomModalBottomSheetState()
    val hazeState = rememberHazeState()
    val scrollState = rememberScrollState()

    HandleSideEffects(
        deleteSheet = deleteSheet,
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested
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
                        text = state.recipeData.metadata.title
                    )
                },
                scrollBehavior = scrollBehavior,
                hazeState = hazeState,
                onNavigate = { onNavigationRequested(DetailsContract.Effect.Navigation.Back) }
            )
        },
        floatingActionButton = {
            if (!state.isDataError && !state.isInitialLoading) {
                FloatingActionMenu(
                    scrollState = scrollState,
                    onFabAction = { action ->
                        when (action) {
                            Action.Delete -> onEventSent(DetailsContract.Event.DeleteRecipeDialog)
                            Action.Share -> shareRecipe(state.recipeData, context)
                            Action.Print -> printRecipe(state.recipeData, context)
                            Action.Run -> onEventSent(DetailsContract.Event.RunRecipe)
                        }
                    }
                )
            }
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
                    onNavigationRequested(DetailsContract.Effect.Navigation.Back)
                }

                else -> {
                    RecipeDetailsContent(
                        state = state,
                        scrollState = scrollState,
                        contentPadding = contentPadding,
                        hazeState = hazeState,
                        onNavigationRequested = onNavigationRequested
                    )
                    BottomSheet(
                        sheetState = deleteSheet.sheetState,
                    ) {
                        ConfirmSheet(
                            title = stringResource(R.string.dialog_confirm_action),
                            message = stringResource(
                                R.string.dialog_confirm_delete_item,
                                state.recipeData.metadata.title
                            ),
                            negativeButtonText = stringResource(R.string.cancel),
                            positiveButtonText = stringResource(R.string.delete),
                            positiveButtonColor = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onError,
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            onPositive = {
                                onEventSent(DetailsContract.Event.DeleteRecipe)
                                deleteSheet.close()
                            },
                            onNegative = { deleteSheet.close() }
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
    onNavigationRequested: (DetailsContract.Effect.Navigation) -> Unit
) {
    val density = LocalDensity.current
    var scrollToPosition by remember { mutableIntStateOf(0) }
    var hasScrolled by rememberSaveable { mutableStateOf(false) }
    val carouselState = rememberCarouselState(
        itemCount = { state.recipeData.assets.size }
    )

    LaunchedEffect(Unit) {
        if (state.recipeStep != -1 && !hasScrolled) {
            hasScrolled = true
            delay(500)
            val paddingAdjustment = with(density) {
                contentPadding.calculateTopPadding().toPx()
            }
            scrollState.animateScrollTo(
                (scrollToPosition - paddingAdjustment).toInt().coerceAtLeast(0)
            )
        }
    }

    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding
    )
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(contentPadding)
            .hazeSource(hazeState)
            .padding(MaterialTheme.spacing.smallOne),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne)
    ) {
        if (state.recipeData.assets.isNotEmpty()) {
            HorizontalMultiBrowseCarousel(
                state = carouselState,
                modifier = Modifier
                    .height(300.dp)
                    .clip(MaterialTheme.shapes.large),
                preferredItemWidth = 600.dp,
                itemSpacing = MaterialTheme.spacing.small
            ) { index ->
                val image = remember { state.recipeImages[index] }
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
                                            index = index
                                        )
                                    )
                                )
                            }
                        )
                        .maskClip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )
            }
        }
        RecipeAbout(
            recipe = state.recipeData
        )
        AnimatedVisibility(
            visible = state.recipeData.metadata.description.isNotBlank(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition()
        ) {
            RecipeDescription(
                description = state.recipeData.metadata.description
            )
        }
        AnimatedVisibility(
            visible = state.recipeData.ingredients.isNotEmpty(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition()
        ) {
            RecipeIngredients(
                ingredients = state.recipeData.ingredients
            )
        }
        AnimatedVisibility(
            visible = state.recipeData.instructions.isNotEmpty(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition()
        ) {
            RecipeInstructions(
                modifier = Modifier.animateContentSize(),
                instructions = state.recipeData.instructions
            )
        }
        AnimatedVisibility(
            visible = state.recipeData.steps.isNotEmpty(),
            enter = fadeEnterTransition(),
            exit = fadeExitTransition()
        ) {
            RecipeSteps(
                steps = state.recipeData.steps,
                units = state.recipeData.metadata.units,
                currentStep = state.recipeStep,
                onStepPosition = { position -> scrollToPosition = position }
            )
        }
    }
}

@Composable
private fun HandleSideEffects(
    deleteSheet: CustomModalBottomSheetState,
    effectFlow: Flow<DetailsContract.Effect>?,
    onNavigationRequested: (DetailsContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is DetailsContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is DetailsContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }

                is DetailsContract.Effect.DeleteDialog -> deleteSheet.open()
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
//@DeviceSizePreviews
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
                    isDataError = false

                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {}
            )
        }
    }
}