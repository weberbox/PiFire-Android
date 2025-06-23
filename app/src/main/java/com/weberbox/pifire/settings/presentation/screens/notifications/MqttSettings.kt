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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.contract.NotifContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun MqttSettingsDestination(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel(),
) {
    ProvidePreferenceTheme {
        MqttSettings(
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
private fun MqttSettings(
    state: NotifContract.State,
    effectFlow: Flow<NotifContract.Effect>?,
    onEventSent: (event: NotifContract.Event) -> Unit,
    onNavigationRequested: (NotifContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is NotifContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is NotifContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_cat_mqtt),
                        fontWeight = FontWeight.Bold
                    )
                },
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
                    MqttSettingsContent(
                        state = state,
                        onEventSent = onEventSent,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun MqttSettingsContent(
    state: NotifContract.State,
    onEventSent: (event: NotifContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val idSheet = rememberCustomModalBottomSheetState()
    val brokerSheet = rememberCustomModalBottomSheetState()
    val portSheet = rememberCustomModalBottomSheetState()
    val usernameSheet = rememberCustomModalBottomSheetState()
    val passwordSheet = rememberCustomModalBottomSheetState()
    val updateSecSheet = rememberCustomModalBottomSheetState()
    val topicSheet = rememberCustomModalBottomSheetState()
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
        Preference(
            title = { Text(text = stringResource(R.string.settings_mqtt_id)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttId)) },
            onClick = { idSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_mqtt_id_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_mqtt_broker)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttBroker)) },
            onClick = { brokerSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_mqtt_port)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttPort.toString())) },
            onClick = { portSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_mqtt_port_note))
        Preference(
            title = { Text(text = stringResource(R.string.username)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttUsername)) },
            onClick = { usernameSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.password)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttPassword)) },
            onClick = { passwordSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_mqtt_user_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_mqtt_update_sec)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttUpdateSec.toString())) },
            onClick = { updateSecSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_mqtt_update_sec_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_mqtt_topic)) },
            summary = { Text(text = getSummary(state.serverData.settings.mqttTopic)) },
            onClick = { topicSheet.open() }
        )
        PreferenceNote(stringResource(R.string.settings_mqtt_topic_note))
    }
    BottomSheet(
        sheetState = idSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.mqttId,
            title = stringResource(R.string.settings_mqtt_id),
            placeholder = stringResource(R.string.settings_mqtt_id),
            onUpdate = {
                onEventSent(NotifContract.Event.SetMqttId(it))
                idSheet.close()
            },
            onDelete = {
                onEventSent(NotifContract.Event.SetMqttId(""))
                idSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = brokerSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.mqttBroker,
            title = stringResource(R.string.settings_mqtt_broker),
            placeholder = stringResource(R.string.settings_mqtt_broker),
            onUpdate = {
                onEventSent(NotifContract.Event.SetMqttBroker(it))
                brokerSheet.close()
            },
            onDelete = {
                onEventSent(NotifContract.Event.SetMqttBroker(""))
                brokerSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = portSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.mqttPort.toString(),
            title = stringResource(R.string.settings_mqtt_port),
            placeholder = stringResource(R.string.settings_mqtt_port),
            onUpdate = {
                onEventSent(NotifContract.Event.SetMqttPort(it.toInt()))
                portSheet.close()
            },
            onDismiss = { portSheet.close() }
        )
    }
    BottomSheet(
        sheetState = usernameSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.mqttUsername,
            title = stringResource(R.string.username),
            placeholder = stringResource(R.string.username),
            onUpdate = {
                onEventSent(NotifContract.Event.SetMqttUsername(it))
                usernameSheet.close()
            },
            onDelete = {
                onEventSent(NotifContract.Event.SetMqttUsername(""))
                usernameSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = passwordSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.mqttPassword,
            title = stringResource(R.string.password),
            placeholder = stringResource(R.string.password),
            onUpdate = {
                onEventSent(NotifContract.Event.SetMqttPassword(it))
                passwordSheet.close()
            },
            onDelete = {
                onEventSent(NotifContract.Event.SetMqttPassword(""))
                passwordSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = updateSecSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.mqttUpdateSec.toString(),
            title = stringResource(R.string.settings_mqtt_update_sec),
            placeholder = stringResource(R.string.settings_mqtt_update_sec),
            onUpdate = {
                updateSecSheet.close()
                onEventSent(NotifContract.Event.SetMqttUpdateSec(it.toInt()))
            },
            onDismiss = { updateSecSheet.close() }
        )
    }
    BottomSheet(
        sheetState = topicSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.mqttTopic,
            title = stringResource(R.string.settings_mqtt_topic),
            placeholder = stringResource(R.string.settings_mqtt_topic),
            onUpdate = {
                onEventSent(NotifContract.Event.SetMqttTopic(it))
                topicSheet.close()
            },
            onDelete = {
                onEventSent(NotifContract.Event.SetMqttTopic(""))
                topicSheet.close()
            }
        )
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun MqttSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                MqttSettings(
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