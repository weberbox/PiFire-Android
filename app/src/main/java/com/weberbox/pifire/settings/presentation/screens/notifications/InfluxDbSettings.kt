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
import com.weberbox.pifire.settings.presentation.component.getSummary
import com.weberbox.pifire.settings.presentation.contract.NotifContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun InfluxDbSettingsDestination(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceTheme {
        InfluxDbSettings(
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
private fun InfluxDbSettings(
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
                        text = stringResource(R.string.settings_cat_influxdb),
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
                    InfluxDbContent(
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
private fun InfluxDbContent(
    state: NotifContract.State,
    onEventSent: (event: NotifContract.Event) -> Unit,
    contentPadding: PaddingValues
) {
    val urlSheet = rememberCustomModalBottomSheetState()
    val tokenSheet = rememberCustomModalBottomSheetState()
    val orgSheet = rememberCustomModalBottomSheetState()
    val bucketSheet = rememberCustomModalBottomSheetState()
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
            title = { Text(text = stringResource(R.string.settings_influxdb_url)) },
            summary = { Text(text = getSummary(state.serverData.settings.influxDbUrl)) },
            onClick = { urlSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_influxdb_token)) },
            summary = { Text(text = getSummary(state.serverData.settings.influxDbToken)) },
            onClick = { tokenSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_influxdb_org)) },
            summary = { Text(text = getSummary(state.serverData.settings.influxDbOrg)) },
            onClick = { orgSheet.open() }
        )
        Preference(
            title = { Text(text = stringResource(R.string.settings_influxdb_bucket)) },
            summary = { Text(text = getSummary(state.serverData.settings.influxDbBucket)) },
            onClick = { bucketSheet.open() }
        )
    }
    BottomSheet(
        sheetState = urlSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.influxDbUrl,
            title = stringResource(R.string.settings_influxdb_url),
            placeholder = stringResource(R.string.settings_influxdb_url),
            onUpdate = {
                urlSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbUrl(it))
            },
            onDelete = {
                urlSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbUrl(""))
            }
        )
    }
    BottomSheet(
        sheetState = tokenSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.influxDbToken,
            title = stringResource(R.string.settings_influxdb_token),
            placeholder = stringResource(R.string.settings_influxdb_token),
            onUpdate = {
                tokenSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbToken(it))
            },
            onDelete = {
                tokenSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbToken(""))
            }
        )
    }
    BottomSheet(
        sheetState = orgSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.influxDbOrg,
            title = stringResource(R.string.settings_influxdb_org),
            placeholder = stringResource(R.string.settings_influxdb_org),
            onUpdate = {
                orgSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbOrg(it))
            },
            onDelete = {
                orgSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbOrg(""))
            }
        )
    }
    BottomSheet(
        sheetState = bucketSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.serverData.settings.influxDbBucket,
            title = stringResource(R.string.settings_influxdb_bucket),
            placeholder = stringResource(R.string.settings_influxdb_bucket),
            onUpdate = {
                bucketSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbBucket(it))
            },
            onDelete = {
                bucketSheet.close()
                onEventSent(NotifContract.Event.SetInfluxDbBucket(""))
            }
        )
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfluxDbSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                InfluxDbSettings(
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