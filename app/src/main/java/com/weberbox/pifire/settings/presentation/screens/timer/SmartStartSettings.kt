package com.weberbox.pifire.settings.presentation.screens.timer

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.weberbox.pifire.settings.presentation.contract.TimerContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.sheets.SmartStartSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun SmartStartSettingsDestination(
    navController: NavHostController,
    viewModel: TimerSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        SmartStartSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is TimerContract.Effect.Navigation.Back -> navController.popBackStack()
                    is TimerContract.Effect.Navigation.NavRoute -> {
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
private fun SmartStartSettings(
    state: TimerContract.State,
    effectFlow: Flow<TimerContract.Effect>?,
    onEventSent: (event: TimerContract.Event) -> Unit,
    onNavigationRequested: (TimerContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is TimerContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is TimerContract.Effect.Notification -> {
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
                        text = stringResource(R.string.settings_smart_start_profiles_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(TimerContract.Effect.Navigation.Back) }
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
                    onNavigationRequested(TimerContract.Effect.Navigation.Back)
                }

                else -> {
                    SmartStartSettingsContent(
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
private fun SmartStartSettingsContent(
    state: TimerContract.State,
    onEventSent: (event: TimerContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val smartStartSheet = rememberInputModalBottomSheetState<Int>()
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.extraSmall),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.settings_smart_start_temp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.extraSmallOne)
                        .weight(1f)
                )
                Text(
                    text = stringResource(R.string.settings_smart_start_startup),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.extraSmallOne)
                        .weight(1f)
                )
                Text(
                    text = stringResource(R.string.settings_smart_start_auger_on),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.extraSmallOne)
                        .weight(1f)
                )
                Text(
                    text = stringResource(R.string.settings_smart_start_p_mode),
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
        state.serverData.settings.smartStartList.forEachIndexed { index, item ->
            Card(
                modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 5.dp),
                shape = MaterialTheme.shapes.small,
                onClick = { smartStartSheet.open(index) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val temp = when (index) {
                        0 -> {
                            "< " + item.temp + " " + state.serverData.settings.tempUnits
                        }

                        state.serverData.settings.smartStartList.size - 1 -> {
                            "> " + (item.temp - 1) + " " + state.serverData.settings.tempUnits
                        }

                        else -> {
                            state.serverData.settings.smartStartList[index - 1].temp.toString() +
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
                        text = item.startUp.toString() + "s",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                    Text(
                        text = item.augerOn.toString() + "s",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                    Text(
                        text = item.pMode.toString(),
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
                shape = RoundedCornerShape(5.dp),
                onClick = { smartStartSheet.open(-1) }
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.add_smart_profile),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    BottomSheet(
        sheetState = smartStartSheet.sheetState
    ) {
        SmartStartSheet(
            title = stringResource(R.string.settings_smart_start_editor_title),
            index = smartStartSheet.data,
            server = state.serverData,
            smartStartItems = state.serverData.settings.smartStartList,
            onDelete = {
                onEventSent(TimerContract.Event.DeleteSmartStartItem(it))
                smartStartSheet.close()
            },
            onUpdate = { event ->
                onEventSent(
                    TimerContract.Event.SetSmartStartItem(
                        index = event.index,
                        temp = event.temp,
                        startUp = event.startUp,
                        augerOn = event.augerOn,
                        pMode = event.pMode,
                        smartStartItems = event.smartStartItems
                    )
                )
                smartStartSheet.close()
            },
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun SmartStartSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                SmartStartSettings(
                    state = TimerContract.State(
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