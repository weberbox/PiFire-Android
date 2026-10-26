package com.weberbox.pifire.feature.settings.presentation.screen.notifications

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.model.push.PushDevice
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.DataError
import com.weberbox.pifire.core.ui.components.EmptyItems
import com.weberbox.pifire.core.ui.components.InitialLoadingProgress
import com.weberbox.pifire.core.ui.components.LinearLoadingIndicator
import com.weberbox.pifire.core.ui.components.SettingsAppBar
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.components.sheets.ConfirmSheet
import com.weberbox.pifire.core.ui.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.showAlerter
import com.weberbox.pifire.feature.settings.presentation.contract.AppPushContract
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun AppPushScreenDestination(
    navController: NavHostController,
    viewModel: AppPushViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    ProvidePreferenceTheme {
        AppPushScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is AppPushContract.Effect.Navigation.Back -> navController.popBackStackFrom(from = sourceEntry)

                    is AppPushContract.Effect.Navigation.NavRoute -> {
                        navController.navigateFrom(
                            from = sourceEntry,
                            route = navigationEffect.route,
                            popUpToRoute = navigationEffect.popUpToRoute,
                            inclusive = navigationEffect.inclusive,
                        )
                    }
                }
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppPushScreen(
    state: AppPushContract.State,
    effectFlow: Flow<AppPushContract.Effect>?,
    onEventSent: (event: AppPushContract.Event) -> Unit,
    onNavigationRequested: (AppPushContract.Effect.Navigation) -> Unit,
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    HandleSideEffects(
        effectFlow = effectFlow,
        onNavigationRequested = onNavigationRequested,
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsAppBar(
                title = stringResource(R.string.settings_app_push_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(AppPushContract.Effect.Navigation.Back) },
            )
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
                    onNavigationRequested(AppPushContract.Effect.Navigation.Back)
                }

                else -> AppPushScreenContent(
                    state = state,
                    contentPadding = contentPadding,
                    onEventSent = onEventSent,
                )
            }
        }
    }
}

@Composable
private fun AppPushScreenContent(
    state: AppPushContract.State,
    contentPadding: PaddingValues,
    onEventSent: (event: AppPushContract.Event) -> Unit,
) {
    val deleteSheet = rememberCustomModalBottomSheetState()
    var pendingDelete by remember { mutableStateOf<PushDevice?>(null) }

    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding,
    )
    if (state.devices.isEmpty()) {
        EmptyItems(
            title = stringResource(R.string.settings_app_push_empty_title),
            summary = stringResource(R.string.settings_app_push_empty_summary),
            icon = Icons.Outlined.PhoneAndroid,
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            item(key = "header") {
                PreferenceCategory(
                    title = { Text(text = stringResource(R.string.settings_app_push_devices)) },
                )
            }
            items(items = state.devices, key = { device -> device.id }) { device ->
                PushDeviceItem(
                    device = device,
                    onDelete = {
                        pendingDelete = device
                        deleteSheet.open()
                    },
                )
            }
        }
    }
    BottomSheet(
        sheetState = deleteSheet.sheetState,
    ) {
        val device = pendingDelete
        if (device != null) {
            ConfirmSheet(
                title = stringResource(R.string.dialog_confirm_action),
                message = stringResource(R.string.settings_app_push_delete_message, device.displayName()),
                negativeButtonText = stringResource(R.string.cancel),
                positiveButtonText = stringResource(R.string.delete),
                positiveButtonColor = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error,
                ),
                onPositive = {
                    onEventSent(AppPushContract.Event.DeleteDevice(device.id))
                    deleteSheet.close()
                },
                onNegative = { deleteSheet.close() },
            )
        }
    }
}

@Composable
private fun PushDeviceItem(
    device: PushDevice,
    onDelete: () -> Unit,
) {
    val registered = remember(device.registered) {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(device.registered * 1000))
    }
    Preference(
        title = {
            Text(
                text = if (device.isThisPhone) {
                    stringResource(R.string.settings_app_push_this_phone, device.displayName())
                } else {
                    device.displayName()
                },
            )
        },
        summary = {
            Text(text = stringResource(R.string.settings_app_push_device_summary, device.appVersion, registered))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.PhoneAndroid,
                contentDescription = null,
            )
        },
        widgetContainer = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                )
            }
        },
    )
}

@Composable
private fun PushDevice.displayName(): String = deviceName.ifBlank { stringResource(R.string.settings_app_push_unnamed) }

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<AppPushContract.Effect>?,
    onNavigationRequested: (AppPushContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is AppPushContract.Effect.Navigation -> onNavigationRequested(effect)

                is AppPushContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error,
                    )
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun AppPushScreenPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                AppPushScreen(
                    state = AppPushContract.State(
                        devices = listOf(
                            PushDevice(
                                id = "a1",
                                deviceName = "Pixel 9 Pro XL",
                                appVersion = "3.4.0",
                                registered = 1_761_400_000,
                                isThisPhone = true,
                            ),
                            PushDevice(
                                id = "b2",
                                deviceName = "Galaxy S25",
                                appVersion = "3.4.0",
                                registered = 1_761_300_000,
                                isThisPhone = false,
                            ),
                        ),
                        isInitialLoading = false,
                        isLoading = false,
                        isDataError = false,
                    ),
                    effectFlow = null,
                    onEventSent = {},
                    onNavigationRequested = {},
                )
            }
        }
    }
}
