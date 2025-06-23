package com.weberbox.pifire.setup.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.common.presentation.base.getPagerAnimationSpec
import com.weberbox.pifire.common.presentation.base.gradientBackground
import com.weberbox.pifire.common.presentation.component.TransparentAppBar
import com.weberbox.pifire.common.presentation.modifier.limitWidthFraction
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.setup.presentation.component.HorizontalDashedStepper
import com.weberbox.pifire.setup.presentation.model.SetupStep
import kotlinx.coroutines.launch

@Composable
fun SetupScreenDestination(
    navController: NavHostController,
    isInEditMode: Boolean = false,
    viewModel: SetupViewModel = hiltViewModel()
) {
    SetupScreen(
        isInEditMode = isInEditMode,
        onNavigateLanding = {
            viewModel.clearServerDataCache()
            navController.safeNavigate(NavGraph.LandingDest.Landing(false), true)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupScreen(
    isInEditMode: Boolean,
    onNavigateLanding: () -> Unit
) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val initialPage by remember { mutableIntStateOf(SetupStep.Terms.ordinal) }
    var currentTitle by rememberSaveable { mutableIntStateOf(SetupStep.entries[initialPage].title) }
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { SetupStep.entries.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        currentTitle = SetupStep.entries[pagerState.currentPage].title
    }

    BackHandler(enabled = true) {
        val currentPage = pagerState.currentPage
        if (currentPage != initialPage) {
            scope.launch {
                currentPage.also { page ->
                    if (page != 0) pagerState.animateScrollToPage(
                        page = currentPage - 1,
                        animationSpec = getPagerAnimationSpec()
                    )
                }
            }
        } else {
            onNavigateLanding()
        }
    }

    Scaffold(
        topBar = {
            TransparentAppBar(
                title = {
                    AnimatedContent(currentTitle) {
                        Text(
                            text = stringResource(it),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                onNavigate = { onBackDispatcher?.onBackPressed() }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground())
                .padding(contentPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.smallThree)
                    .limitWidthFraction(),
            ) {
                HorizontalDashedStepper(
                    totalSteps = SetupStep.entries.size,
                    currentStep = pagerState.currentPage
                )
            }
            HorizontalPager(
                userScrollEnabled = false,
                state = pagerState,
                beyondViewportPageCount = AppConfig.PAGER_BEYOND_COUNT,
                pageSpacing = MaterialTheme.spacing.smallOne,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                if (isInEditMode) {
                    when (page) {
                        SetupStep.Terms.ordinal ->
                            TermsScreenPreview()
                        SetupStep.Auth.ordinal ->
                            AuthScreenPreview()
                        SetupStep.Connect.ordinal ->
                            ConnectScreenPreview()
                        SetupStep.Push.ordinal ->
                            PushScreenPreview()
                        SetupStep.Finish.ordinal ->
                            FinishScreenPreview()
                    }
                } else {
                    when (page) {
                        SetupStep.Terms.ordinal ->
                            TermsScreenDestination(
                                pagerState = pagerState
                            )
                        SetupStep.Auth.ordinal ->
                            AuthScreenDestination(
                                pagerState = pagerState
                            )
                        SetupStep.Connect.ordinal ->
                            ConnectScreenDestination(
                                pagerState = pagerState
                            )
                        SetupStep.Push.ordinal ->
                            PushScreenDestination(
                                pagerState = pagerState
                            )
                        SetupStep.Finish.ordinal ->
                            FinishScreenDestination(
                                onNavigateLanding = onNavigateLanding
                            )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun SetupScreenPreview() {
    PiFireTheme {
        Surface {
            SetupScreen(
                isInEditMode = true,
                onNavigateLanding = {}
            )
        }
    }
}