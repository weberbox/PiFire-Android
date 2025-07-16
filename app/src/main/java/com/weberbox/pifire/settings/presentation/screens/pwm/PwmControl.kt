package com.weberbox.pifire.settings.presentation.screens.pwm

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.annotations.DeviceSizePreviews
import com.weberbox.pifire.settings.presentation.component.PreferenceNote
import com.weberbox.pifire.settings.presentation.contract.PwmContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.sheets.PwmSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun PwmControlDestination(
    navController: NavHostController,
    viewModel: PwmSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        PwmControl(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is PwmContract.Effect.Navigation.Back -> navController.popBackStack()
                    is PwmContract.Effect.Navigation.NavRoute -> {
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
private fun PwmControl(
    state: PwmContract.State,
    effectFlow: Flow<PwmContract.Effect>?,
    onEventSent: (event: PwmContract.Event) -> Unit,
    onNavigationRequested: (PwmContract.Effect.Navigation) -> Unit
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
                title = stringResource(R.string.settings_pwm_control_title),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(PwmContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(PwmContract.Effect.Navigation.Back)
                }

                else -> {
                    PwmControlSettingsContent(
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
private fun PwmControlSettingsContent(
    state: PwmContract.State,
    onEventSent: (event: PwmContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val pwmControlSheet = rememberInputModalBottomSheetState<Int>()
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.mediumOne,
                        end = MaterialTheme.spacing.mediumOne,
                        bottom = MaterialTheme.spacing.extraSmall
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.settings_pwm_control_range),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.extraSmallOne)
                        .weight(1f)
                )
                Text(
                    text = stringResource(R.string.settings_pwm_control_duty),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.extraSmallOne)
                        .weight(1f)
                )
            }
        }
        state.serverData.settings.pwmControlList.forEachIndexed { index, item ->
            Card(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.smallThree,
                    end = MaterialTheme.spacing.smallThree,
                    bottom = MaterialTheme.spacing.extraSmall
                ),
                shape = MaterialTheme.shapes.small,
                onClick = { pwmControlSheet.open(index) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.smallOne)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val temp = when (index) {
                        0 -> {
                            "< " + item.temp + " " + state.serverData.settings.tempUnits
                        }

                        state.serverData.settings.pwmControlList.size - 1 -> {
                            "> " + (item.temp - 1) + " " + state.serverData.settings.tempUnits
                        }

                        else -> {
                            state.serverData.settings.pwmControlList[index - 1].temp.toString() +
                                    "-" + item.temp + " " + state.serverData.settings.tempUnits
                        }
                    }
                    Text(
                        text = temp,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                    Text(
                        text = item.dutyCycle.toString() + "%",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = { pwmControlSheet.open(-1) }
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.settings_smart_start_add_profile),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        PreferenceNote(note = stringResource(R.string.settings_pwm_fan_table_note))
    }
    BottomSheet(
        sheetState = pwmControlSheet.sheetState
    ) {
        PwmSheet(
            title = stringResource(R.string.settings_pwm_editor_title),
            index = pwmControlSheet.data,
            server = state.serverData,
            controlItems = state.serverData.settings.pwmControlList,
            onDelete = {
                onEventSent(PwmContract.Event.DeletePWMControlItem(it))
                pwmControlSheet.close()
            },
            onUpdate = { event ->
                onEventSent(
                    PwmContract.Event.SetPWMControlItem(
                        index = event.index,
                        temp = event.temp,
                        dutyCycle = event.dutyCycle,
                        controlItems = event.controlItems
                    )
                )
                pwmControlSheet.close()
            }
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<PwmContract.Effect>?,
    onNavigationRequested: (PwmContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PwmContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is PwmContract.Effect.Notification -> {
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
fun PwmControlPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                PwmControl(
                    state = PwmContract.State(
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