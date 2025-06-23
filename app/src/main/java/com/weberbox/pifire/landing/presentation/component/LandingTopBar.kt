package com.weberbox.pifire.landing.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.hazeAppBarStyle
import com.weberbox.pifire.common.presentation.component.HazeLargeSearchAppBar
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.landing.presentation.contract.LandingContract
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LandingTopBar(
    modifier: Modifier = Modifier,
    state: LandingContract.State,
    hazeState: HazeState,
    onSearchUpdated: (text: String) -> Unit,
    onNavigationRequested: (LandingContract.Effect.Navigation) -> Unit
) {
    val scope = rememberCoroutineScope()
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    HazeLargeSearchAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.my_smokers),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        searchBarState = searchBarState,
        hazeState = hazeState,
        searchResults = {
            LandingSearchResults(
                serverList = state.serverData.servers,
                searchQuery = textFieldState.text,
                onResultClick = { result ->
                    textFieldState.setTextAndPlaceCursorAtEnd(result)
                    onSearchUpdated(result)
                }
            )
        },
        inputField = {
            LandingInputField(
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                hazeState = hazeState,
                searchStyle = hazeAppBarStyle(
                    MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                onSearch = { result ->
                    scope.launch {
                        searchBarState.animateToCollapsed()
                        onSearchUpdated(result)
                    }
                },
                onSearchClear = {
                    textFieldState.clearText()
                    onSearchUpdated("")
                }
            )
        },
        actions = {
            Row {
                IconButton(
                    onClick = {
                        onNavigationRequested(
                            LandingContract.Effect.Navigation.NavRoute(
                                route = NavGraph.LandingDest.Settings,
                                popUp = false
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }
        },
        onNavigate = { onNavigationRequested(LandingContract.Effect.Navigation.Back) }
    )
}