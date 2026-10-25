package com.weberbox.pifire.feature.landing.presentation.screen

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.network.headers.HeadersData.Headers.ExtraHeader
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.SettingsAppBar
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.icons.Icon
import com.weberbox.pifire.core.ui.icons.outlined.CloudKeyOutline
import com.weberbox.pifire.core.ui.state.InputModalBottomSheetState
import com.weberbox.pifire.core.ui.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.size
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.showAlerter
import com.weberbox.pifire.feature.landing.presentation.components.sheets.HeadersSheet
import com.weberbox.pifire.feature.landing.presentation.contract.HeadersContract
import com.weberbox.pifire.navigation.navigateFrom
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun HeaderSettingsScreenDestination(
    navController: NavHostController,
    viewModel: HeadersViewModel = hiltViewModel(),
) {
    val sourceEntry = rememberSourceEntry()
    ProvidePreferenceTheme {
        HeaderSettingsScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is HeadersContract.Effect.Navigation.Back -> navController.popBackStackFrom(from = sourceEntry)

                    is HeadersContract.Effect.Navigation.NavRoute -> {
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
private fun HeaderSettingsScreen(
    state: HeadersContract.State,
    effectFlow: Flow<HeadersContract.Effect>?,
    onEventSent: (event: HeadersContract.Event) -> Unit,
    onNavigationRequested: (HeadersContract.Effect.Navigation) -> Unit,
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val headersSheet = rememberInputModalBottomSheetState<ExtraHeader>()

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
                title = stringResource(R.string.settings_extra_headers),
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(HeadersContract.Effect.Navigation.Back) },
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        HeaderSettingsScreenContent(
            state = state,
            contentPadding = contentPadding,
            headersSheet = headersSheet,
            onEventSent = onEventSent,
        )
    }
}

@Composable
private fun HeaderSettingsScreenContent(
    state: HeadersContract.State,
    contentPadding: PaddingValues,
    headersSheet: InputModalBottomSheetState<ExtraHeader>,
    onEventSent: (event: HeadersContract.Event) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
    ) {
        Text(
            text = stringResource(R.string.settings_extra_headers_note),
            modifier = Modifier.padding(MaterialTheme.spacing.smallThree),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        state.extraHeaders.forEach { header ->
            Card(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.smallThree,
                    end = MaterialTheme.spacing.smallThree,
                    bottom = MaterialTheme.spacing.smallOne,
                ),
                shape = MaterialTheme.shapes.small,
                onClick = { headersSheet.open(header) },
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.smallOne)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icon.Outlined.CloudKeyOutline,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne),
                        contentDescription = null,
                    )
                    Column {
                        Text(
                            text = header.key,
                            modifier = Modifier.padding(
                                start = MaterialTheme.spacing.extraSmall,
                                top = MaterialTheme.spacing.extraSmall,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 15.sp,
                        )
                        Text(
                            text = header.value,
                            modifier = Modifier.padding(
                                start = MaterialTheme.spacing.extraSmall,
                                bottom = MaterialTheme.spacing.extraSmall,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
        if (state.extraHeaders.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.size.extraLarge),
                )
                Text(
                    text = stringResource(R.string.settings_extra_headers_not_defined),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne),
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.smallThree)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = { headersSheet.open(ExtraHeader()) },
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.settings_extra_headers_add),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
    BottomSheet(
        sheetState = headersSheet.sheetState,
    ) {
        HeadersSheet(
            extraHeader = headersSheet.data,
            onUpdate = { header ->
                headersSheet.close()
                onEventSent(HeadersContract.Event.UpdateHeader(header))
            },
            onDelete = { header ->
                headersSheet.close()
                onEventSent(HeadersContract.Event.DeleteHeader(header))
            },
        )
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<HeadersContract.Effect>?,
    onNavigationRequested: (HeadersContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is HeadersContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is HeadersContract.Effect.Notification -> {
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
private fun HeaderSettingsScreenPreview() {
    val extraHeaders = remember { mutableListOf(ExtraHeader("Test", "Test")) }
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                HeaderSettingsScreen(
                    state = HeadersContract.State(
                        extraHeaders = extraHeaders,
                        isInitialLoading = false,
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
