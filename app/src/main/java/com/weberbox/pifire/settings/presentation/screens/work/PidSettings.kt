package com.weberbox.pifire.settings.presentation.screens.work

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
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
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.settings.presentation.component.PreferenceInfo
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.getSummaryPercent
import com.weberbox.pifire.settings.presentation.component.getSummarySeconds
import com.weberbox.pifire.settings.presentation.contract.WorkContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.util.arrayToHashMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun PidSettingsDestination(
    navController: NavHostController,
    viewModel: WorkSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        PidSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is WorkContract.Effect.Navigation.Back -> navController.popBackStack()
                    is WorkContract.Effect.Navigation.NavRoute -> {
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
private fun PidSettings(
    state: WorkContract.State,
    effectFlow: Flow<WorkContract.Effect>?,
    onEventSent: (event: WorkContract.Event) -> Unit,
    onNavigationRequested: (WorkContract.Effect.Navigation) -> Unit
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
                title = {
                    Text(
                        text = stringResource(R.string.settings_cat_cntrlr_pid),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(WorkContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(WorkContract.Effect.Navigation.Back)
                }

                else -> {
                    PidSettingsContent(
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
private fun PidSettingsContent(
    state: WorkContract.State,
    onEventSent: (event: WorkContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val pidPbSheet = rememberCustomModalBottomSheetState()
    val pidTdSheet = rememberCustomModalBottomSheetState()
    val pidTiSheet = rememberCustomModalBottomSheetState()
    val pidCenterSheet = rememberCustomModalBottomSheetState()
    val pidStableSheet = rememberCustomModalBottomSheetState()
    val pidTauSheet = rememberCustomModalBottomSheetState()
    val pidThetaSheet = rememberCustomModalBottomSheetState()
    val holdCycleTimeSheet = rememberCustomModalBottomSheetState()
    val uMinSheet = rememberCustomModalBottomSheetState()
    val uMaxSheet = rememberCustomModalBottomSheetState()
    val pidSelections = arrayToHashMap(
        context.resources.getStringArray(R.array.controller_config_entries),
        context.resources.getStringArray(R.array.controller_config_values)
    )
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
            title = { Text(text = stringResource(R.string.settings_cat_cntrlr_config)) },
        )
        ListPreference(
            value = state.serverData.settings.cntrlrSelected,
            values = pidSelections.keys.toList().sorted(),
            onValueChange = {
                onEventSent(
                    WorkContract.Event.SetCntrlrSelected(
                        pidSelections.getOrDefault(it, ServerConstants.CNTRLR_PID)
                    )
                )
            },
            title = { Text(text = stringResource(R.string.settings_cntrlr_title)) },
            summary = {
                Text(
                    text = pidSelections.filterValues {
                        it == state.serverData.settings.cntrlrSelected
                    }.keys.joinToString("")
                )
            },
            type = ListPreferenceType.DROPDOWN_MENU
        )
        PidControlSettings(
            state = state,
            pidPbSheet = pidPbSheet,
            pidTdSheet = pidTdSheet,
            pidTiSheet = pidTiSheet,
            pidCenterSheet = pidCenterSheet
        )
        PidAcControlSettings(
            state = state,
            pidPbSheet = pidPbSheet,
            pidTdSheet = pidTdSheet,
            pidTiSheet = pidTiSheet,
            pidCenterSheet = pidCenterSheet,
            pidStableSheet = pidStableSheet
        )
        PidSpControlSettings(
            state = state,
            pidPbSheet = pidPbSheet,
            pidTdSheet = pidTdSheet,
            pidCenterSheet = pidCenterSheet,
            pidStableSheet = pidStableSheet,
            pidTauSheet = pidTauSheet,
            pidThetaSheet = pidThetaSheet
        )

        AnimatedVisibility(
            visible = state.serverData.settings.cntrlrSelected != ServerConstants.CNTRLR_PID &&
                    state.serverData.settings.cntrlrSelected != ServerConstants.CNTRLR_PID_AC &&
                    state.serverData.settings.cntrlrSelected != ServerConstants.CNTRLR_PID_SP
        ) {
            PreferenceNote(stringResource(R.string.settings_cycle_cntrlr_no_config))
        }
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_cntrlr_cycle)) },
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_cycle_cntrlr_cycle)) },
            summary = {
                Text(text = getSummarySeconds(state.serverData.settings.holdCycleTime.toString()))
            },
            onClick = { holdCycleTimeSheet.open() }
        )
        PreferenceInfo(stringResource(R.string.settings_cycle_cntrlr_cycle_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_cycle_cntrlr_u_min)) },
            summary = {
                Text(text = getSummaryPercent(state.serverData.settings.uMin.toString()))
            },
            onClick = {uMinSheet.open() }
        )
        PreferenceInfo(stringResource(R.string.settings_cycle_cntrlr_u_min_note))
        Preference(
            title = { Text(text = stringResource(R.string.settings_cycle_cntrlr_u_max)) },
            summary = {
                Text(text = getSummaryPercent(state.serverData.settings.uMax.toString()))
            },
            onClick = { uMaxSheet.open() }
        )
        PreferenceInfo(stringResource(R.string.settings_cycle_cntrlr_u_max_note))
    }
    PidBottomSheets(
        state = state,
        pidPbSheet = pidPbSheet,
        pidTdSheet = pidTdSheet,
        pidTiSheet = pidTiSheet,
        pidCenterSheet = pidCenterSheet,
        pidStableSheet = pidStableSheet,
        pidTauSheet = pidTauSheet,
        pidThetaSheet = pidThetaSheet,
        holdCycleTimeSheet = holdCycleTimeSheet,
        uMinSheet = uMinSheet,
        uMaxSheet = uMaxSheet,
        onEventSent = onEventSent
    )
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<WorkContract.Effect>?,
    onNavigationRequested: (WorkContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is WorkContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is WorkContract.Effect.Notification -> {
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
private fun PidSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                PidSettings(
                    state = WorkContract.State(
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