package com.weberbox.pifire.settings.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.FireExtinguisher
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.Speedometer
import com.weberbox.pifire.common.icons.filled.ThermometerProbe
import com.weberbox.pifire.common.icons.filled.TuneVerticalVariant
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.HazeAppBar
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.PullToRefresh
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.CachedDataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.annotations.DeviceSizePreviews
import com.weberbox.pifire.settings.presentation.contract.SettingsContract
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun SettingsHomeDestination(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        SettingsHome(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is SettingsContract.Effect.Navigation.Back -> navController.popBackStack()
                    is SettingsContract.Effect.Navigation.NavRoute -> {
                        navController.safeNavigate(
                            route = navigationEffect.route,
                            popUp = navigationEffect.popUp
                        )
                    }
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsHome(
    state: SettingsContract.State,
    effectFlow: Flow<SettingsContract.Effect>?,
    onEventSent: (event: SettingsContract.Event) -> Unit,
    onNavigationRequested: (SettingsContract.Effect.Navigation) -> Unit
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val hazeState = rememberHazeState()

    HandleSideEffects(
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
                        text = stringResource(R.string.nav_settings),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                hazeState = hazeState,
                onNavigate = { onNavigationRequested(SettingsContract.Effect.Navigation.Back) }
            )
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
                state.isDataError -> CachedDataError {
                    onEventSent(SettingsContract.Event.Refresh)
                }

                else -> {
                    SettingsContent(
                        state = state,
                        onEventSent = onEventSent,
                        onNavigationRequested = onNavigationRequested,
                        contentPadding = contentPadding,
                        hazeState = hazeState
                    )
                }
            }
        }

    }
}

@Composable
fun SettingsContent(
    state: SettingsContract.State,
    onEventSent: (event: SettingsContract.Event) -> Unit,
    onNavigationRequested: (SettingsContract.Effect.Navigation) -> Unit,
    contentPadding: PaddingValues,
    hazeState: HazeState
) {
    PullToRefresh(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEventSent(SettingsContract.Event.Refresh) },
        contentPadding = contentPadding
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .hazeSource(hazeState)
                .padding(
                    horizontal = MaterialTheme.spacing.smallThree,
                    vertical = MaterialTheme.spacing.small
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallTwo),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large),
                contentAlignment = Alignment.TopCenter
            ) {
                HomePreference(
                    title = stringResource(R.string.settings_app_title),
                    summary = stringResource(R.string.settings_app_summary),
                    icon = Icon.Filled.TuneVerticalVariant,
                    firstItem = true,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.App
                            )
                        )
                    }
                )
            }
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large),
            ) {
                HomePreference(
                    title = stringResource(R.string.settings_admin_title),
                    summary = stringResource(R.string.settings_admin_summary),
                    icon = Icons.Filled.AdminPanelSettings,
                    firstItem = true,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Admin
                            )
                        )
                    }
                )
                HomePreference(
                    title = stringResource(R.string.settings_probe_title),
                    summary = stringResource(R.string.settings_probe_summary),
                    icon = Icon.Filled.ThermometerProbe,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Probe
                            )
                        )
                    }
                )
                HomePreference(
                    title = stringResource(R.string.settings_naming_title),
                    summary = stringResource(R.string.settings_naming_summary),
                    icon = Icons.Filled.OutdoorGrill,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Name
                            )
                        )
                    }
                )
                HomePreference(
                    title = stringResource(R.string.settings_work_title),
                    summary = stringResource(R.string.settings_work_summary),
                    icon = Icons.Filled.Construction,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Work
                            )
                        )
                    }
                )
                if (state.supportsDcFan) {
                    HomePreference(
                        title = stringResource(R.string.settings_pwm_title),
                        summary = stringResource(R.string.settings_pwm_summary),
                        icon = Icon.Filled.Speedometer,
                        onClick = {
                            onNavigationRequested(
                                SettingsContract.Effect.Navigation.NavRoute(
                                    NavGraph.SettingsDest.Pwm
                                )
                            )
                        }
                    )
                }
                HomePreference(
                    title = stringResource(R.string.settings_pellets_title),
                    summary = stringResource(R.string.settings_pellets_summary),
                    icon = Icons.Filled.Grain,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Pellets
                            )
                        )
                    }
                )
                HomePreference(
                    title = stringResource(R.string.settings_timers_title),
                    summary = stringResource(R.string.settings_timers_summary),
                    icon = Icons.Filled.PowerSettingsNew,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Timer
                            )
                        )
                    }
                )
                HomePreference(
                    title = stringResource(R.string.settings_safety_title),
                    summary = stringResource(R.string.settings_safety_summary),
                    icon = Icons.Filled.FireExtinguisher,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Safety
                            )
                        )
                    }
                )
                HomePreference(
                    title = stringResource(R.string.settings_notifications_title),
                    summary = stringResource(R.string.settings_notifications_summary),
                    icon = Icons.Filled.Notifications,
                    onClick = {
                        onNavigationRequested(
                            SettingsContract.Effect.Navigation.NavRoute(
                                NavGraph.SettingsDest.Notifications
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun HomePreference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String,
    icon: ImageVector,
    firstItem: Boolean = false,
    onClick: () -> Unit
) {
    if (!firstItem) {
        Spacer(
            modifier = modifier
                .height(4.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        )
    }
    Preference(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        title = { Text(text = title) },
        summary = {
            Text(
                text = summary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title
            )
        },
        onClick = onClick
    )
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<SettingsContract.Effect>?,
    onNavigationRequested: (SettingsContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is SettingsContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is SettingsContract.Effect.Notification -> {
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@DeviceSizePreviews
fun SettingsHomePreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                SettingsHome(
                    state = SettingsContract.State(
                        supportsDcFan = true,
                        isInitialLoading = false,
                        isRefreshing = true,
                        isLoading = true,
                        isDataError = false
                    ),
                    effectFlow = null,
                    onEventSent = { },
                    onNavigationRequested = {}
                )
            }
        }
    }
}