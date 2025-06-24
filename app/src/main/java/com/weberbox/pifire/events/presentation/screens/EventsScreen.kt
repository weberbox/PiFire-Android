package com.weberbox.pifire.events.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.PullToRefresh
import com.weberbox.pifire.common.presentation.modifier.isElementVisible
import com.weberbox.pifire.common.presentation.screens.CachedDataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.events.presentation.component.EventItem
import com.weberbox.pifire.events.presentation.contract.EventsContract
import com.weberbox.pifire.events.presentation.model.EventsData.Events.Event
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun EventsScreenDestination(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    viewModel: EventsViewModel = hiltViewModel()
) {
    EventsScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        hazeState = hazeState,
        contentPadding = contentPadding,
    )
}

@Composable
private fun EventsScreen(
    state: EventsContract.State,
    effectFlow: Flow<EventsContract.Effect>?,
    onEventSent: (event: EventsContract.Event) -> Unit,
    hazeState: HazeState,
    contentPadding: PaddingValues,
) {
    val activity = LocalActivity.current
    var isVisibleOnScreen by remember { mutableStateOf(false) }
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            if (isVisibleOnScreen) {
                when (effect) {
                    is EventsContract.Effect.Notification ->
                        activity?.showAlerter(
                            message = effect.text,
                            isError = effect.error
                        )
                }
            }
        }?.collect()
    }

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        modifier = Modifier.isElementVisible {
            isVisibleOnScreen = it
        },
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError }
    ) { state ->
        when {
            state.isInitialLoading -> InitialLoadingProgress()
            state.isDataError -> CachedDataError { onEventSent(EventsContract.Event.Refresh) }
            else -> {
                PullToRefresh(
                    isRefreshing = state.isRefreshing,
                    onRefresh = {
                        onEventSent(EventsContract.Event.Refresh)
                    },
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding
                ) {
                    LazyColumn(
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .hazeSource(state = hazeState)
                            .fillMaxSize()
                            .padding(
                                vertical = MaterialTheme.spacing.smallOne,
                                horizontal = MaterialTheme.spacing.smallOne
                            ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                    ) {
                        items(
                            items = state.eventsList,
                            key = { it.id }
                        ) { item ->
                            EventItem(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
internal fun EventsScreenPreview(
    contentPadding: PaddingValues = PaddingValues(),
    hazeState: HazeState = rememberHazeState()
) {
    PiFireTheme {
        Surface {
            EventsScreen(
                state = EventsContract.State(
                    eventsList = buildEventsList(),
                    isInitialLoading = false,
                    isDataError = false,
                    isRefreshing = false
                ),
                effectFlow = null,
                onEventSent = {},
                hazeState = hazeState,
                contentPadding = contentPadding,
            )
        }
    }
}

internal fun buildEventsList() = listOf(
    Event(
        date = "04-25-2025",
        time = "6:18 PM",
        message = "[INFO] Stop Mode Started",
        title = "I",
        color = "#64666666"
    ),
    Event(
        date = "04-25-2025",
        time = "6:18 PM",
        message = "[INFO] Hopper Level Checked @ 72%",
        title = "I",
        color = "#64666666"
    ),
    Event(
        date = "04-25-2025",
        time = "6:18 PM",
        message = "[WARNING] Warning Text",
        title = "W",
        color = "#32FF4731"
    ),
    Event(
        date = "04-25-2025",
        time = "6:18 PM",
        message = "[INFO] Script Ended",
        title = "I",
        color = "#64666666"
    ),
    Event(
        date = "04-25-2025",
        time = "6:18 PM",
        message = "[INFO] Script Starting",
        title = "I",
        color = "#64666666"
    ),
    Event(
        date = "04-25-2025",
        time = "6:18 PM",
        message = "[DEBUG] * Debug Testing",
        title = "D",
        color = "#32004CFA"
    )
)