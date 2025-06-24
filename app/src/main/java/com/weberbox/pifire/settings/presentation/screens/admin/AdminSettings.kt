package com.weberbox.pifire.settings.presentation.screens.admin

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
import androidx.compose.material3.ButtonDefaults
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
import com.composables.core.ModalBottomSheetState
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.ConfirmSheet
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.settings.presentation.contract.AdminContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

@Composable
fun AdminSettingsDestination(
    navController: NavHostController,
    viewModel: AdminSettingsViewModel = hiltViewModel(),
) {
    ProvidePreferenceTheme {
        AdminSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is AdminContract.Effect.Navigation.Back -> navController.popBackStack()
                    is AdminContract.Effect.Navigation.NavRoute -> {
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
private fun AdminSettings(
    state: AdminContract.State,
    effectFlow: Flow<AdminContract.Effect>?,
    onEventSent: (event: AdminContract.Event) -> Unit,
    onNavigationRequested: (AdminContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is AdminContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is AdminContract.Effect.Notification -> {
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
                        text = stringResource(R.string.settings_admin_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(AdminContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(AdminContract.Effect.Navigation.Back)
                }

                else -> {
                    AdminSettingsContent(
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
private fun AdminSettingsContent(
    state: AdminContract.State,
    onEventSent: (event: AdminContract.Event) -> Unit,
    onNavigationRequested: (AdminContract.Effect.Navigation) -> Unit,
    contentPadding: PaddingValues
) {
    val deleteHistorySheet = rememberCustomModalBottomSheetState()
    val deleteEventsSheet = rememberCustomModalBottomSheetState()
    val deletePelletLogSheet = rememberCustomModalBottomSheetState()
    val deletePelletSheet = rememberCustomModalBottomSheetState()
    val factoryResetSheet = rememberCustomModalBottomSheetState()
    val restartControlSheet = rememberCustomModalBottomSheetState()
    val restartWebAppSheet = rememberCustomModalBottomSheetState()
    val restartSupervisorSheet = rememberCustomModalBottomSheetState()
    val rebootSheet = rememberCustomModalBottomSheetState()
    val shutdownSheet = rememberCustomModalBottomSheetState()
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
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_manual_functions)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_manual)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_manual_summary)
                )
            }
        ) {
            onNavigationRequested(
                AdminContract.Effect.Navigation.NavRoute(NavGraph.SettingsDest.Manual)
            )
        }
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_admin)) },
        )
        SwitchPreference(
            value = state.adminDebug,
            onValueChange = { onEventSent(AdminContract.Event.SetDebugMode(it)) },
            title = { Text(text = stringResource(R.string.settings_admin_debug)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_debug_summary)
                )
            }
        )
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_data_manage)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_delete_history)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_delete_history_note)
                )
            },
            onClick = { deleteHistorySheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_delete_events)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_delete_events_note)
                )
            },
            onClick = { deleteEventsSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_delete_pellets_log)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_delete_pellets_log_note)
                )
            },
            onClick = { deletePelletLogSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_delete_pellets)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_delete_pellets_note)
                )
            },
            onClick = { deletePelletSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_factory_reset)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_factory_reset_note)
                )
            },
            onClick = { factoryResetSheet.open() }
        )
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_scripts)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_restart_control)) },
            onClick = { restartControlSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_restart_webapp)) },
            onClick = { restartWebAppSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_restart_supervisor)) },
            onClick = { restartSupervisorSheet.open() }
        )
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_power)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_reboot)) },
            onClick = { rebootSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_admin_shutdown)) },
            onClick = { shutdownSheet.open() }
        )
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_boot)) },
        )
        SwitchPreference(
            value = state.bootToMonitor,
            onValueChange = { onEventSent(AdminContract.Event.SetBootToMonitor(it)) },
            title = { Text(text = stringResource(R.string.settings_admin_boot_to_monitor)) },
            summary = {
                Text(
                    text = stringResource(R.string.settings_admin_boot_to_monitor_summary)
                )
            }
        )
    }
    AdminActionSheet(
        sheetState = deleteHistorySheet.sheetState,
        message = stringResource(R.string.settings_admin_delete_history_text),
        positiveButtonText = stringResource(R.string.delete),
        onDismiss = { deleteHistorySheet.close() },
        onPositive = { onEventSent(AdminContract.Event.DeleteHistory) }
    )
    AdminActionSheet(
        sheetState = deleteEventsSheet.sheetState,
        message = stringResource(R.string.settings_admin_delete_events_text),
        positiveButtonText = stringResource(R.string.delete),
        onDismiss = { deleteEventsSheet.close()},
        onPositive = { onEventSent(AdminContract.Event.DeleteEvents) }
    )
    AdminActionSheet(
        sheetState = deletePelletLogSheet.sheetState,
        message = stringResource(R.string.settings_admin_delete_pellets_log_text),
        positiveButtonText = stringResource(R.string.delete),
        onDismiss = { deletePelletLogSheet.close() },
        onPositive = { onEventSent(AdminContract.Event.DeletePelletsLog) }
    )
    AdminActionSheet(
        sheetState = deletePelletSheet.sheetState,
        message = stringResource(R.string.settings_admin_delete_pellets_text),
        positiveButtonText = stringResource(R.string.delete),
        onDismiss = { deletePelletSheet.close() },
        onPositive = { onEventSent(AdminContract.Event.DeletePellets) }
    )
    AdminActionSheet(
        sheetState = factoryResetSheet.sheetState,
        message = stringResource(R.string.settings_admin_factory_reset_text),
        positiveButtonText = stringResource(R.string.reset),
        onDismiss = { factoryResetSheet.close()},
        onPositive = { onEventSent(AdminContract.Event.FactoryReset) }
    )
    AdminActionSheet(
        sheetState = restartControlSheet.sheetState,
        message = stringResource(R.string.settings_admin_restart_control_text),
        positiveButtonText = stringResource(R.string.restart),
        onDismiss = { restartControlSheet.close() },
        onPositive = { onEventSent(AdminContract.Event.RestartControl) }
    )
    AdminActionSheet(
        sheetState = restartWebAppSheet.sheetState,
        message = stringResource(R.string.settings_admin_restart_webapp_text),
        positiveButtonText = stringResource(R.string.restart),
        onDismiss = { restartWebAppSheet.close() },
        onPositive = { onEventSent(AdminContract.Event.RestartWebApp) }
    )
    AdminActionSheet(
        sheetState = restartSupervisorSheet.sheetState,
        message = stringResource(R.string.settings_admin_restart_supervisor_text),
        positiveButtonText = stringResource(R.string.restart),
        onDismiss = { restartSupervisorSheet.close() },
        onPositive = { onEventSent(AdminContract.Event.RestartSupervisor) }
    )
    AdminActionSheet(
        sheetState = rebootSheet.sheetState,
        message = stringResource(R.string.settings_admin_reboot_text),
        positiveButtonText = stringResource(R.string.reboot),
        onDismiss = { rebootSheet.close()},
        onPositive = { onEventSent(AdminContract.Event.RebootSystem) }
    )
    AdminActionSheet(
        sheetState = shutdownSheet.sheetState,
        message = stringResource(R.string.settings_admin_shutdown_text),
        positiveButtonText = stringResource(R.string.shutdown),
        onDismiss = { shutdownSheet.close() },
        onPositive = { onEventSent(AdminContract.Event.ShutdownSystem) },
    )
}

@Composable
private fun AdminActionSheet(
    sheetState: ModalBottomSheetState,
    message: String,
    positiveButtonText: String,
    onPositive: () -> Unit,
    onDismiss: () -> Unit,
) {
    BottomSheet(
        sheetState = sheetState
    ) {
        ConfirmSheet(
            title = stringResource(R.string.dialog_confirm_action),
            message = message,
            positiveButtonText = positiveButtonText,
            positiveButtonColor = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            onNegative = { onDismiss() },
            onPositive = { onPositive(); onDismiss() }
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
fun AdminSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                AdminSettings(
                    state = AdminContract.State(
                        adminDebug = true,
                        bootToMonitor = true,
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