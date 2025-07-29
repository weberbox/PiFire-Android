package com.weberbox.pifire.dashboard.presentation.screens

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.Smoke
import com.weberbox.pifire.common.icons.filled.Speedometer
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.PagerIndicator
import com.weberbox.pifire.common.presentation.component.PullToRefresh
import com.weberbox.pifire.common.presentation.component.ToolTipBox
import com.weberbox.pifire.common.presentation.modifier.isElementVisible
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.CachedDataError
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.common.presentation.util.slideDownExitTransition
import com.weberbox.pifire.common.presentation.util.slideUpEnterTransition
import com.weberbox.pifire.dashboard.presentation.component.Action
import com.weberbox.pifire.dashboard.presentation.component.CriticalErrorCard
import com.weberbox.pifire.dashboard.presentation.component.ElapsedCard
import com.weberbox.pifire.dashboard.presentation.component.ErrorCard
import com.weberbox.pifire.dashboard.presentation.component.FoodProbe
import com.weberbox.pifire.dashboard.presentation.component.GrillProbe
import com.weberbox.pifire.dashboard.presentation.component.LidDetectedCard
import com.weberbox.pifire.dashboard.presentation.component.ModeCard
import com.weberbox.pifire.dashboard.presentation.component.OptionCard
import com.weberbox.pifire.dashboard.presentation.component.OutputsCard
import com.weberbox.pifire.dashboard.presentation.component.OutputsToolbar
import com.weberbox.pifire.dashboard.presentation.component.PelletsCard
import com.weberbox.pifire.dashboard.presentation.component.RecipeCard
import com.weberbox.pifire.dashboard.presentation.component.RemainingCard
import com.weberbox.pifire.dashboard.presentation.component.TimerCard
import com.weberbox.pifire.dashboard.presentation.component.WarningCard
import com.weberbox.pifire.dashboard.presentation.contract.DashContract
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.RecipeStatus
import com.weberbox.pifire.dashboard.presentation.model.ElapsedData
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.dashboard.presentation.model.TimerData
import com.weberbox.pifire.dashboard.presentation.sheets.DashBottomSheets
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlin.math.ceil

@Composable
fun DashboardScreenDestination(
    navController: NavHostController,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    viewModel: DashboardViewModel? = null
) {
    return when {
        LocalInspectionMode.current -> {
            DashboardScreenPreview(
                contentPadding = contentPadding,
                hazeState = hazeState
            )
        }

        else -> {
            DashboardScreen(
                navController = navController,
                contentPadding = contentPadding,
                hazeState = hazeState,
                viewModel = viewModel ?: hiltViewModel()
            )
        }
    }
}

@Composable
private fun DashboardScreen(
    navController: NavHostController,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    viewModel: DashboardViewModel
) {
    DashboardScreenContent(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        hazeState = hazeState,
        contentPadding = contentPadding,
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is DashContract.Effect.Navigation.NavRoute ->
                    navController.safeNavigate(
                        route = navigationEffect.route,
                        popUp = navigationEffect.popUp
                    )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenContent(
    state: DashContract.State,
    effectFlow: Flow<DashContract.Effect>?,
    onEventSent: (event: DashContract.Event) -> Unit,
    hazeState: HazeState,
    contentPadding: PaddingValues,
    onNavigationRequested: (DashContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val scrollState = rememberScrollState()
    val grillSheet = rememberCustomModalBottomSheetState()
    val foodSheet = rememberInputModalBottomSheetState<Probe>()
    val timerSheet = rememberCustomModalBottomSheetState()
    val modeSheet = rememberCustomModalBottomSheetState()
    val holdPickerSheet = rememberCustomModalBottomSheetState()
    val tooltipState = rememberTooltipState(initialIsVisible = false, isPersistent = true)
    var outputsToolbar by rememberSaveable { mutableStateOf(false) }
    var isVisibleOnScreen by remember { mutableStateOf(false) }

    HandleSideEffects(
        activity = activity,
        isVisibleOnScreen = isVisibleOnScreen,
        tooltipState = tooltipState,
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested,
    )

    LaunchedEffect(state.holdTempToolTip) {
        if (state.holdTempToolTip) {
            tooltipState.show()
        }
    }

    DisposableEffect(state.keepScreenOn) {
        val window = activity?.window
        if (state.keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        modifier = Modifier.isElementVisible {
            isVisibleOnScreen = it
        },
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError }
    ) { state ->
        when {
            state.isInitialLoading -> InitialLoadingProgress()
            state.isDataError -> CachedDataError { onEventSent(DashContract.Event.Refresh) }
            else -> {
                PullToRefresh(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onEventSent(DashContract.Event.Refresh) },
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne),
                        modifier = Modifier
                            .hazeSource(state = hazeState)
                            .verticalScroll(scrollState)
                            .padding(contentPadding)
                            .padding(vertical = MaterialTheme.spacing.smallOne)
                            .fillMaxSize(),
                    ) {
                        val totalProbes = state.dash.foodProbes.size
                        val probesPerPage = 2
                        val numPages = ceil(totalProbes.toDouble() / probesPerPage).toInt()
                        val pagerState = rememberPagerState(pageCount = { numPages })
                        CriticalErrorCard(
                            criticalError = state.dash.criticalError,
                        )
                        ErrorCard(
                            errors = state.dash.errors,
                            onClick = {
                                if (state.isConnected)
                                    onEventSent(DashContract.Event.RestartControl)
                            }
                        )
                        WarningCard(
                            warnings = state.dash.warnings,
                        )
                        LidDetectedCard(
                            lidOpenDetected = state.dash.lidOpenDetected,
                            lidOpenEndTime = state.dash.lidOpenEndTime,
                            onClick = {
                                if (state.isConnected)
                                    onEventSent(DashContract.Event.ToggleLidDetect)
                            }
                        )
                        RemainingCard(
                            currentMode = state.dash.currentMode,
                            modeStartTime = state.dash.modeStartTime,
                            startDuration = state.dash.startDuration,
                            shutdownDuration = state.dash.shutdownDuration
                        )
                        RecipeCard(
                            recipeStatus = state.dash.recipeStatus,
                            onClick = { filename, step ->
                                onNavigationRequested(
                                    DashContract.Effect.Navigation.NavRoute(
                                        NavGraph.RecipesDest.Details(filename, step)
                                    )
                                )
                            }
                        )
                        ToolTipBox(
                            title = stringResource(R.string.dash_hold_temp_tip_title),
                            details = stringResource(R.string.dash_hold_temp_tip_details),
                            buttonText = stringResource(R.string.dismiss),
                            spacing = -MaterialTheme.spacing.mediumOne,
                            onClick = {
                                onEventSent(DashContract.Event.HideHoldTempTip)
                            },
                            tooltipState = tooltipState
                        ) {
                            GrillProbe(
                                probe = state.dash.primaryProbe,
                                onClick = { if (state.isConnected) grillSheet.open() },
                                onLongClick = {
                                    if (state.dash.currentMode == RunningMode.Hold.name &&
                                        state.isConnected
                                    ) {
                                        holdPickerSheet.open()
                                    }
                                }
                            )
                        }
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = MaterialTheme.spacing.smallOne,
                            contentPadding = PaddingValues(
                                horizontal = MaterialTheme.spacing.smallOne
                            ),
                            beyondViewportPageCount = 1,
                            modifier = Modifier.fillMaxWidth(),
                        ) { page ->
                            val startIndex = page * probesPerPage
                            val endIndex = minOf(startIndex + probesPerPage, totalProbes)
                            val foodProbes = state.dash.foodProbes.subList(startIndex, endIndex)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    MaterialTheme.spacing.smallOne
                                )
                            ) {
                                foodProbes.forEach { item ->
                                    FoodProbe(
                                        probe = item,
                                        tempUnits = state.dash.tempUnits,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (state.isConnected) foodSheet.open(item)
                                        }
                                    )
                                }
                            }
                        }
                        Row(
                            Modifier
                                .wrapContentHeight()
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PagerIndicator(
                                numberOfPages = pagerState.pageCount,
                                selectedPage = pagerState.currentPage,
                                animationDurationInMillis = 500,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.spacing.smallOne)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                MaterialTheme.spacing.smallOne
                            )
                        ) {
                            with(state.dash.timer) {
                                TimerCard(
                                    time = state.timerData.remainingTime,
                                    keepWarm = keepWarm,
                                    shutdown = shutdown,
                                    paused = timerPaused,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (state.isConnected) timerSheet.open()
                                    }
                                )
                            }
                            PelletsCard(
                                level = state.dash.hopperLevel,
                                hasDistanceSensor = state.dash.hasDistanceSensor,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (state.isConnected) {
                                        onEventSent(DashContract.Event.CheckHopperLevel)
                                    }
                                }
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.spacing.smallOne)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                MaterialTheme.spacing.smallOne
                            )
                        ) {
                            ElapsedCard(
                                time = state.elapsedData.elapsedTime,
                                modifier = Modifier.weight(1f)
                            )
                            with(state.dash.outputs) {
                                OutputsCard(
                                    fanOutput = fan,
                                    augerOutput = auger,
                                    igniterOutput = igniter,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (state.isConnected &&
                                            state.dash.allowManualOutputs &&
                                            state.dash.currentMode != RunningMode.Stop.name
                                        ) {
                                            outputsToolbar = !outputsToolbar
                                        }
                                    }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.spacing.smallOne)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OptionCard(
                                title = stringResource(R.string.dash_smoke_plus),
                                icon = Icon.Filled.Smoke,
                                enabled = state.dash.smokePlus,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (state.isConnected)
                                        onEventSent(DashContract.Event.SmokePlus)
                                }
                            )
                            if (state.dash.hasDcFan) {
                                OptionCard(
                                    title = stringResource(R.string.dash_pwm_control),
                                    icon = Icon.Filled.Speedometer,
                                    enabled = state.dash.pwmControl,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (state.isConnected)
                                            onEventSent(DashContract.Event.PWMControl)
                                    }
                                )
                            }
                            ModeCard(
                                modifier = Modifier.weight(1f),
                                mode = state.dash.currentMode,
                                recipePaused = state.dash.recipeStatus.paused,
                                onClick = {
                                    if (state.isConnected) modeSheet.open()
                                }
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = outputsToolbar,
                        enter = slideUpEnterTransition(),
                        exit = slideDownExitTransition()
                    ) {
                        OutputsToolbar(
                            contentPadding = contentPadding,
                            onActionClick = { action ->
                                if (state.isConnected) {
                                    onEventSent(
                                        DashContract.Event.SendEvent(
                                            when (action) {
                                                Action.Fan -> DashEvent.ToggleFan(
                                                    !state.dash.outputs.fan
                                                )

                                                Action.Auger -> DashEvent.ToggleAuger(
                                                    !state.dash.outputs.auger
                                                )

                                                Action.Igniter -> DashEvent.ToggleIgniter(
                                                    !state.dash.outputs.igniter
                                                )
                                            }
                                        )
                                    )
                                }
                            },
                            onClose = { outputsToolbar = false }
                        )
                    }
                    LinearLoadingIndicator(
                        isLoading = state.isLoading,
                        contentPadding = contentPadding
                    )
                }
                DashBottomSheets(
                    state = state,
                    grillSheet = grillSheet,
                    foodSheet = foodSheet,
                    timerSheet = timerSheet,
                    modeSheet = modeSheet,
                    holdPickerSheet = holdPickerSheet,
                    onEventSent = onEventSent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HandleSideEffects(
    activity: Activity?,
    isVisibleOnScreen: Boolean,
    tooltipState: TooltipState,
    effectFlow: Flow<DashContract.Effect>?,
    onNavigationRequested: (DashContract.Effect.Navigation) -> Unit
) {
    LaunchedEffect(SIDE_EFFECTS_KEY, isVisibleOnScreen) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is DashContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is DashContract.Effect.Notification -> {
                    if (isVisibleOnScreen) {
                        activity?.showAlerter(
                            message = effect.text,
                            isError = effect.error
                        )
                    }
                }

                is DashContract.Effect.HideHoldTempToolTip -> {
                    tooltipState.dismiss()
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
internal fun DashboardScreenPreview(
    contentPadding: PaddingValues = PaddingValues(),
    hazeState: HazeState = rememberHazeState()
) {
    PiFireTheme {
        Surface {
            DashboardScreenContent(
                state = DashContract.State(
                    dash = buildDash(),
                    timerData = TimerData(),
                    elapsedData = ElapsedData(),
                    incrementTemps = true,
                    keepScreenOn = false,
                    isInitialLoading = false,
                    isLoading = true,
                    isDataError = false,
                    isRefreshing = false,
                    isConnected = true,
                    holdTempToolTip = false
                ),
                effectFlow = null,
                onEventSent = {},
                hazeState = hazeState,
                contentPadding = contentPadding,
                onNavigationRequested = {}
            )
        }
    }
}

private fun buildDash(): Dash {
    return Dash(
        hasDcFan = true,
        currentMode = RunningMode.Startup.name,
        recipeStatus = RecipeStatus(
            recipeMode = false
        )
    )
}