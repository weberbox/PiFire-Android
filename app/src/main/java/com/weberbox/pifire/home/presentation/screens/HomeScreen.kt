package com.weberbox.pifire.home.presentation.screens

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.Menu
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.base.getPagerAnimationSpec
import com.weberbox.pifire.common.presentation.component.AppBarConnectingTitle
import com.weberbox.pifire.common.presentation.component.HazeAppBar
import com.weberbox.pifire.common.presentation.modifier.customTouch
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.util.BiometricPromptManager
import com.weberbox.pifire.core.util.rememberBiometricPromptManager
import com.weberbox.pifire.dashboard.presentation.screens.DashboardScreenDestination
import com.weberbox.pifire.events.presentation.screens.EventsScreenDestination
import com.weberbox.pifire.home.presentation.component.BottomBar
import com.weberbox.pifire.home.presentation.component.DrawerSheet
import com.weberbox.pifire.home.presentation.component.HomeAppBarActions
import com.weberbox.pifire.home.presentation.component.NavRequest
import com.weberbox.pifire.home.presentation.component.NavSideRail
import com.weberbox.pifire.home.presentation.component.OffsetNavigationDrawer
import com.weberbox.pifire.home.presentation.component.rememberOffsetDrawerState
import com.weberbox.pifire.home.presentation.contract.HomeContract
import com.weberbox.pifire.home.presentation.model.HomePages.Dashboard
import com.weberbox.pifire.home.presentation.model.HomePages.Events
import com.weberbox.pifire.home.presentation.model.HomePages.Pellets
import com.weberbox.pifire.home.presentation.model.NavigationItem
import com.weberbox.pifire.home.presentation.utils.buildNavigationItems
import com.weberbox.pifire.home.presentation.utils.buildStaticNavigationItems
import com.weberbox.pifire.home.presentation.utils.isBottomBarNavigation
import com.weberbox.pifire.home.presentation.utils.isPermDrawerNavigation
import com.weberbox.pifire.home.presentation.utils.isRailNavigation
import com.weberbox.pifire.home.presentation.utils.navContentPadding
import com.weberbox.pifire.home.presentation.utils.offsetDrawerWidth
import com.weberbox.pifire.home.presentation.utils.permDrawerAdjustments
import com.weberbox.pifire.pellets.presentation.screens.PelletsScreenDestination
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun HomeScreenDestination(
    navController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    HomeScreen(
        initialPage = Dashboard.page,
        navController = navController,
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is HomeContract.Effect.Navigation.Back -> navController.popBackStack()
                is HomeContract.Effect.Navigation.Changelog -> {
                    navController.safeNavigate(NavGraph.Changelog)
                }

                is HomeContract.Effect.Navigation.NavRoute -> {
                    navController.safeNavigate(
                        route = navigationEffect.route,
                        popUp = navigationEffect.popUp
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    initialPage: Int,
    navController: NavHostController,
    state: HomeContract.State,
    effectFlow: Flow<HomeContract.Effect>?,
    onEventSent: (event: HomeContract.Event) -> Unit,
    onNavigationRequested: (HomeContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val window = activity?.window
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val hazeState = rememberHazeState()
    val navigationItems = buildNavigationItems()
    val isPermDrawerNavigation = isPermDrawerNavigation()
    val isRailNavigation = isRailNavigation()
    val isBottomBarNavigation = isBottomBarNavigation()
    val biometricManager = rememberBiometricPromptManager()

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { navigationItems.size }
    )
    val drawerState = rememberOffsetDrawerState()
    val scope = rememberCoroutineScope()
    var title by rememberSaveable { mutableStateOf(navigationItems[initialPage].title) }

    HandleSideEffects(
        activity = activity,
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested
    )

    BackHandler(drawerState.isOpen || pagerState.currentPage != initialPage) {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        } else {
            scope.launch {
                pagerState.animateScrollToPage(
                    page = initialPage,
                    animationSpec = getPagerAnimationSpec()
                )
            }
        }
    }
    DisposableEffect(state.showBottomBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window?.isNavigationBarContrastEnforced = !state.showBottomBar
        }
        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window?.isNavigationBarContrastEnforced = true
            }
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        title = navigationItems[pagerState.currentPage].title
    }
    LaunchedEffect(Unit) {
        if (drawerState.isOpen && isPermDrawerNavigation) drawerState.close()
    }

    OffsetNavigationDrawer(
        offsetDrawerState = drawerState,
        overscrollEffect = rememberOverscrollEffect(),
        gesturesEnabled = !isPermDrawerNavigation,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        drawerContent = {
            HomeDrawerSheet(
                pagerState = pagerState,
                navigationItems = navigationItems,
                grillName = state.grillName,
                onNavigationRequested = { navRequest ->
                    scope.launch {
                        when (navRequest) {
                            is NavRequest.Close -> drawerState.close()
                            is NavRequest.NavRoute -> {
                                drawerState.close()
                                when (navRequest.destination) {
                                    is NavGraph.LandingDest.Landing ->
                                        onEventSent(HomeContract.Event.SignOut)

                                    is NavGraph.SettingsDest ->
                                        checkAuthentication(
                                            state = state,
                                            biometricManager = biometricManager,
                                            onSuccess = {
                                                onNavigationRequested(
                                                    HomeContract.Effect.Navigation.NavRoute(
                                                        navRequest.destination
                                                    )
                                                )
                                            }
                                        )

                                    else ->
                                        onNavigationRequested(
                                            HomeContract.Effect.Navigation.NavRoute(
                                                navRequest.destination
                                            )
                                        )
                                }
                            }

                            is NavRequest.PagerIndex -> {
                                drawerState.close()
                                pagerState.animateScrollToPage(
                                    page = navRequest.index,
                                    animationSpec = getPagerAnimationSpec()
                                )
                            }
                        }
                    }
                }
            )
        }
    ) {
        Row {
            if (isPermDrawerNavigation) {
                HomeDrawerSheet(
                    pagerState = pagerState,
                    navigationItems = navigationItems,
                    grillName = state.grillName,
                    onNavigationRequested = { navRequest ->
                        scope.launch {
                            when (navRequest) {
                                is NavRequest.Close -> {}
                                is NavRequest.NavRoute -> {
                                    when (navRequest.destination) {
                                        is NavGraph.LandingDest.Landing ->
                                            onEventSent(HomeContract.Event.SignOut)

                                        is NavGraph.SettingsDest ->
                                            checkAuthentication(
                                                state = state,
                                                biometricManager = biometricManager,
                                                onSuccess = {
                                                    onNavigationRequested(
                                                        HomeContract.Effect.Navigation.NavRoute(
                                                            navRequest.destination
                                                        )
                                                    )
                                                }
                                            )

                                        else ->
                                            onNavigationRequested(
                                                HomeContract.Effect.Navigation.NavRoute(
                                                    navRequest.destination
                                                )
                                            )
                                    }
                                }

                                is NavRequest.PagerIndex -> {
                                    pagerState.animateScrollToPage(
                                        page = navRequest.index,
                                        animationSpec = getPagerAnimationSpec()
                                    )
                                }
                            }
                        }
                    }
                )
            }
            if (isRailNavigation) {
                NavSideRail(
                    navigationItems = navigationItems,
                    currentPage = pagerState.currentPage,
                    onNavigationRequested = { navRequest ->
                        scope.launch {
                            when (navRequest) {
                                is NavRequest.PagerIndex -> {
                                    pagerState.animateScrollToPage(
                                        page = navRequest.index,
                                        animationSpec = getPagerAnimationSpec()
                                    )
                                }

                                else -> {}
                            }
                        }
                    }
                )
            }
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .permDrawerAdjustments(),
                topBar = {
                    HazeAppBar(
                        title = {
                            AppBarConnectingTitle(
                                title = title,
                                isConnecting = !state.isConnected,
                            )
                        },
                        windowInsets = windowInsets.only(
                            WindowInsetsSides.End + WindowInsetsSides.Top
                        ),
                        scrollBehavior = scrollBehavior,
                        hazeState = hazeState,
                        showNavigationIcon = !isPermDrawerNavigation,
                        navigationIcon = Icon.Filled.Menu,
                        actions = {
                            HomeAppBarActions(
                                lidOpenDetectEnabled = state.lidOpenDetectEnabled,
                                isConnected = state.isConnected,
                                isHoldMode = state.isHoldMode,
                                onTriggerLidEvent = {
                                    onEventSent(HomeContract.Event.TriggerLidOpen)
                                }
                            )
                        },
                        onNavigate = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                },
                bottomBar = {
                    if (isBottomBarNavigation && state.showBottomBar) {
                        BottomBar(
                            navigationItems = navigationItems,
                            pagerState = pagerState,
                            onClick = { index ->
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        page = index,
                                        animationSpec = getPagerAnimationSpec()
                                    )
                                }
                            }
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = contentColorFor(MaterialTheme.colorScheme.background),
                contentWindowInsets = windowInsets
            ) { innerPadding ->
                val contentPadding = navContentPadding(innerPadding)
                var scrollEnabled by remember { mutableStateOf(true) }
                HorizontalPager(
                    userScrollEnabled = scrollEnabled,
                    state = pagerState,
                    beyondViewportPageCount = AppConfig.PAGER_BEYOND_COUNT,
                    pageSpacing = MaterialTheme.spacing.smallOne,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (!isPermDrawerNavigation) {
                                Modifier.customTouch(
                                    onDown = {},
                                    onMove = { changeList ->
                                        changeList.firstOrNull()?.let {
                                            if (scrollEnabled &&
                                                (it.position.x - it.previousPosition.x) > 0f
                                                && pagerState.currentPage == 0
                                            ) {
                                                scrollEnabled = false
                                            }
                                        }
                                    },

                                    onUp = {
                                        scrollEnabled = true
                                    }
                                )
                            } else Modifier
                        )
                ) { page ->
                    when (page) {
                        Pellets.page -> PelletsScreenDestination(
                            navController = navController,
                            contentPadding = contentPadding,
                            hazeState = hazeState
                        )

                        Dashboard.page -> DashboardScreenDestination(
                            navController = navController,
                            contentPadding = contentPadding,
                            hazeState = hazeState
                        )

                        Events.page -> EventsScreenDestination(
                            contentPadding = contentPadding,
                            hazeState = hazeState
                        )
                    }
                }
            }
        }
    }
}

private fun checkAuthentication(
    state: HomeContract.State,
    biometricManager: BiometricPromptManager?,
    onSuccess: () -> Unit,
) {
    if (state.biometricSettingsPrompt) {
        biometricManager?.authenticate(
            onAuthenticationSuccess = onSuccess
        )
    } else {
        onSuccess()
    }
}

@Composable
private fun HomeDrawerSheet(
    pagerState: PagerState,
    navigationItems: List<NavigationItem>,
    grillName: String,
    onNavigationRequested: (NavRequest) -> Unit
) {
    val staticNavigationItems = buildStaticNavigationItems()
    DrawerSheet(
        modifier = Modifier
            .offsetDrawerWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Start + WindowInsetsSides.Vertical
                )
            ),
        currentPage = pagerState.currentPage,
        grillName = grillName,
        navigationItems = navigationItems,
        staticNavigationItems = staticNavigationItems,
        onNavigationRequested = onNavigationRequested
    )
}

@Composable
private fun HandleSideEffects(
    activity: Activity?,
    effectFlow: Flow<HomeContract.Effect>?,
    onNavigationRequested: (HomeContract.Effect.Navigation) -> Unit
) {
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is HomeContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is HomeContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }

            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, apiLevel = 35)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, apiLevel = 35)
private fun HomeScreenPreview() {
    PiFireTheme {
        Surface {
            HomeScreen(
                navController = rememberNavController(),
                initialPage = Dashboard.page,
                state = HomeContract.State(
                    showBottomBar = true,
                    isConnected = true,
                    isHoldMode = true,
                    lidOpenDetectEnabled = true,
                    biometricSettingsPrompt = false,
                    isInitialLoading = false,
                    grillName = "Development",
                    isDataError = false,
                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {}
            )
        }
    }
}