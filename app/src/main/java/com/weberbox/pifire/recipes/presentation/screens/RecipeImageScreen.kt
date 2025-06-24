package com.weberbox.pifire.recipes.presentation.screens

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.PagerIndicator
import com.weberbox.pifire.common.presentation.modifier.pagerCubeInDepthTransition
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.LockScreenOrientation
import com.weberbox.pifire.common.presentation.util.rememberPaletteColorState
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.recipes.presentation.contract.ImagesContract
import com.weberbox.pifire.recipes.presentation.util.decodeBase64Image
import com.weberbox.pifire.setup.presentation.component.RecipeAsyncImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun RecipeImagesScreenDestination(
    navController: NavHostController,
    viewModel: RecipeImageViewModel = hiltViewModel(),
) {
    RecipeImagesScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ImagesContract.Effect.Navigation.Back -> navController.popBackStack()
            }
        }
    )
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun RecipeImagesScreen(
    state: ImagesContract.State,
    effectFlow: Flow<ImagesContract.Effect>?,
    onEventSent: (event: ImagesContract.Event) -> Unit,
    onNavigationRequested: (ImagesContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val pagerState = rememberPagerState(
        initialPage = state.imageIndex,
        pageCount = { state.imageList.size }
    )
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.surfaceContainer,
        1f to MaterialTheme.colorScheme.surfaceContainerLow
    )
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ImagesContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is ImagesContract.Effect.Notification ->
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
            }
        }?.collect()
    }

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError }
    ) { state ->
        val paletteColor by rememberPaletteColorState()
        var currentImage: ByteArray? by remember { mutableStateOf(null) }

        LaunchedEffect(currentImage) {
            currentImage?.let {
                paletteColor.updateStartingColor(currentImage)
            }
        }

        LaunchedEffect(state.imageIndex) {
            if (state.imageIndex != 0) pagerState.animateScrollToPage(state.imageIndex)
        }

        when {
            state.isInitialLoading -> InitialLoadingProgress()
            state.isDataError -> DataError {
                onNavigationRequested(ImagesContract.Effect.Navigation.Back)
            }

            else -> {
                LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                Surface {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(colorStops = colorStops))
                            .background(Brush.verticalGradient(colors = paletteColor.brushColor))
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledIconButton(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(
                                    start = MaterialTheme.spacing.extraSmall,
                                    top = MaterialTheme.spacing.smallTwo
                                ),
                            onClick = {
                                onNavigationRequested(
                                    ImagesContract.Effect.Navigation.Back
                                )
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = MaterialTheme.spacing.smallOne,
                            contentPadding = PaddingValues(
                                horizontal = MaterialTheme.spacing.smallOne
                            ),
                            beyondViewportPageCount = AppConfig.PAGER_BEYOND_COUNT
                        ) { index ->
                            if (state.imageList.isNotEmpty()) {
                                val image = remember { state.imageList[index] }
                                if (index == 0) currentImage = image
                                RecipeAsyncImage(
                                    image = image,
                                    modifier = Modifier.pagerCubeInDepthTransition(
                                        page = index,
                                        pagerState = pagerState
                                    ),
                                )
                            }
                        }
                        Row(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(bottom = MaterialTheme.spacing.smallOne),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PagerIndicator(
                                numberOfPages = pagerState.pageCount,
                                selectedPage = pagerState.currentPage,
                                defaultRadius = 8.dp,
                                selectedLength = 20.dp,
                                space = 5.dp,
                                animationDurationInMillis = 500,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
//@DeviceSizePreviews
private fun RecipeImagesScreenPreview() {
    PiFireTheme {
        Surface {
            RecipeImagesScreen(
                state = ImagesContract.State(
                    imageList = listOf(
                        decodeBase64Image(buildRecipe()[0].assets[0].encodedImage)
                    ),
                    imageIndex = 0,
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
