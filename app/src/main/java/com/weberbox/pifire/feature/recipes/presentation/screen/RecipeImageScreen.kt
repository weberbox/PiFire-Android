package com.weberbox.pifire.feature.recipes.presentation.screen

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.DataError
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.PagerIndicator
import com.weberbox.pifire.core.ui.modifier.limitWidthFraction
import com.weberbox.pifire.core.ui.modifier.pagerCubeInDepthTransition
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.UiMessage
import com.weberbox.pifire.core.ui.util.rememberPaletteColorState
import com.weberbox.pifire.core.ui.util.showMessage
import com.weberbox.pifire.feature.recipes.presentation.contract.ImagesContract
import com.weberbox.pifire.feature.recipes.presentation.util.decodeBase64Image
import com.weberbox.pifire.feature.setup.presentation.components.RecipeAsyncImage
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import net.engawapg.lib.zoomable.rememberZoomState

@Composable
fun RecipeImagesScreenDestination(
    navController: NavHostController,
    viewModel: RecipeImageViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    RecipeImagesScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ImagesContract.Effect.Navigation.Back -> navController.popBackStackFrom(from = sourceEntry)
            }
        },
    )
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun RecipeImagesScreen(
    state: ImagesContract.State,
    effectFlow: Flow<ImagesContract.Effect>?,
    onEventSent: (event: ImagesContract.Event) -> Unit,
    onNavigationRequested: (ImagesContract.Effect.Navigation) -> Unit,
) {
    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested,
    )

    RecipeImagesScreenContent(
        state = state,
        onNavigationRequested = onNavigationRequested,
    )
}

@Composable
private fun RecipeImagesScreenContent(
    state: ImagesContract.State,
    onNavigationRequested: (ImagesContract.Effect.Navigation) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = state.imageIndex,
        pageCount = { state.imageList.size },
    )
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.surfaceContainer,
        1f to MaterialTheme.colorScheme.surfaceContainerLow,
    )

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError },
    ) { state ->
        val paletteColor by rememberPaletteColorState()
        // Saved so rotating doesn't jump back to the image the screen was opened on
        var initialPageShown by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(state.imageList) {
            if (!initialPageShown && state.imageList.isNotEmpty()) {
                pagerState.scrollToPage(state.imageIndex.coerceIn(0, state.imageList.lastIndex))
                initialPageShown = true
            }
        }

        LaunchedEffect(pagerState.currentPage, state.imageList) {
            state.imageList.getOrNull(pagerState.currentPage)?.let { image ->
                withContext(Dispatchers.Default) { paletteColor.updateStartingColor(image) }
            }
        }

        when {
            state.isInitialLoading -> InitialLoadingProgress()

            state.isDataError -> DataError {
                onNavigationRequested(ImagesContract.Effect.Navigation.Back)
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colorStops = colorStops))
                        .background(Brush.verticalGradient(colors = paletteColor.brushColor))
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                ) {
                    FilledIconButton(
                        modifier = Modifier
                            .padding(
                                start = MaterialTheme.spacing.extraSmall,
                                top = MaterialTheme.spacing.smallTwo,
                            ),
                        onClick = {
                            onNavigationRequested(
                                ImagesContract.Effect.Navigation.Back,
                            )
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                    HorizontalPager(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = pagerState,
                        pageSpacing = MaterialTheme.spacing.smallOne,
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.spacing.smallOne,
                        ),
                        beyondViewportPageCount = AppConfig.PAGER_BEYOND_COUNT,
                    ) { index ->
                        state.imageList.getOrNull(index)?.let { image ->
                            RecipeAsyncImage(
                                image = image,
                                zoomState = rememberZoomState(),
                                modifier = Modifier
                                    .limitWidthFraction()
                                    .pagerCubeInDepthTransition(
                                        page = index,
                                        pagerState = pagerState,
                                    ),
                            )
                        }
                    }
                    Row(
                        Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(bottom = MaterialTheme.spacing.smallOne),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        PagerIndicator(
                            numberOfPages = pagerState.pageCount,
                            selectedPage = pagerState.currentPage,
                            animationDurationInMillis = 500,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<ImagesContract.Effect>?,
    onNavigationRequested: (ImagesContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ImagesContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is ImagesContract.Effect.Notification ->
                    activity?.showMessage(
                        message = UiMessage.of(text = effect.text, isError = effect.error),
                    )
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeImagesScreenPreview() {
    PiFireTheme {
        Surface {
            RecipeImagesScreen(
                state = ImagesContract.State(
                    imageList = listOf(
                        decodeBase64Image(buildRecipe()[0].assets[0].encodedImage),
                    ),
                    imageIndex = 0,
                    isInitialLoading = false,
                    isDataError = false,

                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {},
            )
        }
    }
}
