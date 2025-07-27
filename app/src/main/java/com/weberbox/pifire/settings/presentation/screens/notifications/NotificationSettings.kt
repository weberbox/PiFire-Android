package com.weberbox.pifire.settings.presentation.screens.notifications

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.config.Secrets
import com.weberbox.pifire.core.util.NotificationsPermissionDetailsProvider
import com.weberbox.pifire.core.util.rememberPermissionState
import com.weberbox.pifire.settings.presentation.component.TwoTargetSwitchPreference
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.contract.NotifContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun NotificationSettingsDestination(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        NotificationSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is NotifContract.Effect.Navigation.Back -> navController.popBackStack()
                    is NotifContract.Effect.Navigation.NavRoute -> {
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
private fun NotificationSettings(
    state: NotifContract.State,
    effectFlow: Flow<NotifContract.Effect>?,
    onEventSent: (event: NotifContract.Event) -> Unit,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsAppBar(
                title = stringResource(R.string.settings_notifications_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(NotifContract.Effect.Navigation.Back) }
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
                state.isDataError -> DataError {
                    onNavigationRequested(NotifContract.Effect.Navigation.Back)
                }

                else -> {
                    NotificationSettingsContent(
                        state = state,
                        onEventSent = onEventSent,
                        onNavigationRequested = onNavigationRequested,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsContent(
    state: NotifContract.State,
    onEventSent: (event: NotifContract.Event) -> Unit,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit,
    contentPadding: PaddingValues
) {
    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        if (Secrets.ONESIGNAL_APP_ID.isNotBlank()) {
            TwoTargetSwitchPreference(
                value = state.serverData.settings.onesignalEnabled,
                onValueChange = { onEventSent(NotifContract.Event.SetOneSignalEnabled(it)) },
                title = { Text(text = stringResource(R.string.settings_cat_onesignal)) },
                summary = { Text(text = getSummary(state.serverData.settings.onesignalEnabled)) },
                onClick = {
                    onNavigationRequested(
                        NotifContract.Effect.Navigation.NavRoute(
                            NavGraph.SettingsDest.Push
                        )
                    )
                }
            )
        }
        TwoTargetSwitchPreference(
            value = state.serverData.settings.iftttEnabled,
            onValueChange = { onEventSent(NotifContract.Event.SetIFTTTEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_cat_ifttt)) },
            summary = { Text(text = getSummary(state.serverData.settings.iftttEnabled)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.Ifttt
                    )
                )
            }
        )
        TwoTargetSwitchPreference(
            value = state.serverData.settings.pushoverEnabled,
            onValueChange = { onEventSent(NotifContract.Event.SetPushOverEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_cat_pushover)) },
            summary = { Text(text = getSummary(state.serverData.settings.pushoverEnabled)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.Pushover
                    )
                )
            }
        )
        TwoTargetSwitchPreference(
            value = state.serverData.settings.pushbulletEnabled,
            onValueChange = { onEventSent(NotifContract.Event.SetPushBulletEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_cat_pushbullet)) },
            summary = { Text(text = getSummary(state.serverData.settings.pushbulletEnabled)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.Pushbullet
                    )
                )
            }
        )
        TwoTargetSwitchPreference(
            value = state.serverData.settings.influxDbEnabled,
            onValueChange = { onEventSent(NotifContract.Event.SetInfluxDbEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_cat_influxdb)) },
            summary = { Text(text = getSummary(state.serverData.settings.influxDbEnabled)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.InfluxDb
                    )
                )
            }
        )
        TwoTargetSwitchPreference(
            value = state.serverData.settings.mqttEnabled,
            onValueChange = { onEventSent(NotifContract.Event.SetMqttEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_cat_mqtt)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttEnabled)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.Mqtt
                    )
                )
            }
        )
        TwoTargetSwitchPreference(
            value = state.serverData.settings.appriseEnabled,
            onValueChange = { onEventSent(NotifContract.Event.SetAppriseEnabled(it)) },
            title = { Text(text = stringResource(R.string.settings_cat_apprise)) },
            summary = { Text(text = getSummary(state.serverData.settings.appriseEnabled)) },
            onClick = {
                onNavigationRequested(
                    NotifContract.Effect.Navigation.NavRoute(
                        NavGraph.SettingsDest.Apprise
                    )
                )
            }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<NotifContract.Effect>?,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val notificationPermission = rememberPermissionState(
        permissionDetailsProvider = NotificationsPermissionDetailsProvider()
    )
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is NotifContract.Effect.RequestPermission -> {
                    notificationPermission.launchPermissionRequest()
                }

                is NotifContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is NotifContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error,
                        duration = 6000
                    )
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun NotificationsSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                NotificationSettings(
                    state = NotifContract.State(
                        serverData = Server(),
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
}