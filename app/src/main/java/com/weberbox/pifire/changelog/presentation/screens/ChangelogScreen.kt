package com.weberbox.pifire.changelog.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.changelog.presentation.component.LogAnimation
import com.weberbox.pifire.changelog.presentation.component.LogHeader
import com.weberbox.pifire.changelog.presentation.component.LogItem
import com.weberbox.pifire.changelog.presentation.contract.ChangelogContract
import com.weberbox.pifire.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.changelog.presentation.model.ChangelogData.Changelog
import com.weberbox.pifire.changelog.presentation.model.ChangelogData.Log
import com.weberbox.pifire.common.presentation.base.gradientBackground
import com.weberbox.pifire.common.presentation.component.HazeAppBar
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.safeNavigate
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow

@Composable
fun ChangelogScreenDestination(
    navController: NavHostController,
    showAnimation: Boolean = true,
    viewModel: ChangelogViewModel = hiltViewModel()
) {
    ChangelogScreen(
        showAnimation = showAnimation,
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ChangelogContract.Effect.Navigation.Back -> navController.popBackStack()
                is ChangelogContract.Effect.Navigation.NavRoute -> {
                    navController.safeNavigate(
                        route = navigationEffect.route,
                        popUp = navigationEffect.popUp
                    )
                }
            }
        }
    )
}

@Suppress("UNUSED_PARAMETER")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChangelogScreen(
    showAnimation: Boolean,
    state: ChangelogContract.State,
    effectFlow: Flow<ChangelogContract.Effect>?,
    onEventSent: (event: ChangelogContract.Event) -> Unit,
    onNavigationRequested: (ChangelogContract.Effect.Navigation) -> Unit
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val hazeState = rememberHazeState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HazeAppBar(
                title = {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.changelog_title)
                    )
                },
                scrollBehavior = scrollBehavior,
                hazeState = hazeState,
                onNavigate = { onNavigationRequested(ChangelogContract.Effect.Navigation.Back) }
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier
                .hazeSource(state = hazeState)
                .fillMaxSize(),
        ) {
            items(items = state.changelogData.changelog) { item ->
                Column {
                    LogHeader(item.current, item.version, item.date)
                    item.logs.forEach { log ->
                        LogItem(log)
                    }
                }
            }
            if (state.changelogData.changelog.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(top = MaterialTheme.spacing.largeTwo)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.size.extraLarge)
                        )
                        Text(
                            text = stringResource(R.string.changelog_error),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne)
                        )
                    }
                }
            }
        }
        if (showAnimation) {
            LogAnimation()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ChangelogScreenPreview() {
    val changelogData = ChangelogData(buildChangelogItems())
    PiFireTheme {
        Surface {
            ChangelogScreen(
                showAnimation = false,
                state = ChangelogContract.State(
                    changelogData = changelogData,
                    isInitialLoading = false,
                    isDataError = false,
                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {}
            )
        }
    }
}

private fun buildChangelogItems(): List<Changelog> {
    return listOf(
        Changelog(
            version = "3.0.0",
            date = "",
            current = true,
            logs = listOf(
                Log(
                    type = "new",
                    text = "Added high and low probe notifications for supported PiFire versions"
                ),
                Log(
                    type = "imp",
                    text = "Added elapsed time for supported PiFire versions"
                )
            )
        ),
        Changelog(
            version = "2.4.0",
            date = "",
            current = false,
            logs = listOf(
                Log(
                    type = "new",
                    text = "Added high and low probe notifications for supported PiFire versions"
                ),
                Log(
                    type = "imp",
                    text = "Added elapsed time for supported PiFire versions"
                )
            )
        )
    )
}