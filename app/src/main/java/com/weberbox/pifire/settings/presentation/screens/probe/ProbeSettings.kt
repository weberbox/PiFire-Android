package com.weberbox.pifire.settings.presentation.screens.probe

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.ThermometerProbe
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.annotations.DeviceSizePreviews
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.component.getTempSummary
import com.weberbox.pifire.settings.presentation.contract.ProbeContract
import com.weberbox.pifire.settings.presentation.model.ProbeMap
import com.weberbox.pifire.settings.presentation.model.ProbeMap.ProbeInfo
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.sheets.ProbeSheet
import com.weberbox.pifire.settings.presentation.util.arrayToHashMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun ProbeSettingsDestination(
    navController: NavHostController,
    viewModel: ProbeSettingsViewModel = hiltViewModel(),
) {
    ProvidePreferenceTheme {
        ProbeSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is ProbeContract.Effect.Navigation.Back -> navController.popBackStack()
                    is ProbeContract.Effect.Navigation.NavRoute -> {
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
private fun ProbeSettings(
    state: ProbeContract.State,
    effectFlow: Flow<ProbeContract.Effect>?,
    onEventSent: (event: ProbeContract.Event) -> Unit,
    onNavigationRequested: (ProbeContract.Effect.Navigation) -> Unit
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
                title = stringResource(R.string.settings_probe_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(ProbeContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(ProbeContract.Effect.Navigation.Back)
                }

                else -> {
                    ProbeSettingsContent(
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
private fun ProbeSettingsContent(
    state: ProbeContract.State,
    onEventSent: (event: ProbeContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val probeSheet = rememberInputModalBottomSheetState<ProbeInfo>()
    val tempSelections = arrayToHashMap(
        context.resources.getStringArray(R.array.grill_temp_unit_entries),
        context.resources.getStringArray(R.array.grill_temp_unit_values)
    )
    LinearLoadingIndicator(
        isLoading = state.isLoading,
        contentPadding = contentPadding
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_probe_edit)) },
        )
        state.serverData.settings.probeMap.probeInfo.forEach { item ->
            Card(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.smallThree,
                        end = MaterialTheme.spacing.smallThree,
                        bottom = MaterialTheme.spacing.extraSmall
                    ),
                shape = MaterialTheme.shapes.large,
                onClick = { probeSheet.open(item) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.smallOne)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.type.equals("Primary", ignoreCase = true)) {
                        Icon(
                            modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne),
                            imageVector = Icons.Filled.Thermostat,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne),
                            imageVector = Icon.Filled.ThermometerProbe,
                            contentDescription = null
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.name,
                            modifier = Modifier.padding(start = MaterialTheme.spacing.extraSmall),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        )
                        if (item.port.contains("ADC", ignoreCase = true)) {
                            Text(
                                text = item.profile.name,
                                modifier = Modifier.padding(
                                    start = MaterialTheme.spacing.extraSmall
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.settings_probes_no_profile),
                                modifier = Modifier.padding(
                                    start = MaterialTheme.spacing.extraSmall
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
        if (state.serverData.settings.probeMap.probeInfo.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.smallOne)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.size.extraLarge)
                )
                Text(
                    text = stringResource(R.string.settings_probes_not_found),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne)
                )
            }
        }
        PreferenceCategory(
            title = { Text(text = stringResource(R.string.settings_cat_temp_units)) },
        )
        ListPreference(
            value = state.serverData.settings.tempUnits,
            values = tempSelections.keys.toList(),
            onValueChange = {
                onEventSent(
                    ProbeContract.Event.SetTempUnits(
                        tempSelections.getOrDefault(it, "f_units")
                    )
                )
            },
            title = { Text(text = stringResource(R.string.settings_grill_temp_units)) },
            summary = { Text(text = getTempSummary(state.serverData.settings.tempUnits)) },
            type = ListPreferenceType.DROPDOWN_MENU
        )
        PreferenceNote(stringResource(R.string.settings_units_note))
    }
    BottomSheet(
        sheetState = probeSheet.sheetState
    ) {
        ProbeSheet(
            probeInfo = probeSheet.data,
            profiles = state.serverData.settings.probeProfiles.values.toList(),
            onEvent = {
                onEventSent(ProbeContract.Event.UpdateProbe(it.probeDto))
                probeSheet.close()
            },
            onDismiss = { probeSheet.close() }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<ProbeContract.Effect>?,
    onNavigationRequested: (ProbeContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ProbeContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is ProbeContract.Effect.Notification -> {
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
private fun ProbeSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                ProbeSettings(
                    state = ProbeContract.State(
                        serverData = Server(
                            settings = Server.Settings(
                                probeMap = buildProbeData()
                            )
                        ),
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

private fun buildProbeData(): ProbeMap {
    return ProbeMap(
        probeInfo = List(4) {
            ProbeInfo(
                name = "Grill"
            )
        }
    )
}