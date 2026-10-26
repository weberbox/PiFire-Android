package com.weberbox.pifire.feature.events.presentation.screen

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.events.data.repo.EventsRepo
import com.weberbox.pifire.feature.events.presentation.contract.EventsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventsRepo: EventsRepo,
) : BaseViewModel<EventsContract.Event, EventsContract.State, EventsContract.Effect>() {

    init {
        getEventsData(false)
        listenEventsData()
    }

    override fun setInitialState() = EventsContract.State(
        eventsList = emptyList(),
        isInitialLoading = true,
        isDataError = false,
        isRefreshing = false,
    )

    override fun handleEvents(event: EventsContract.Event) {
        when (event) {
            EventsContract.Event.Refresh -> getEventsData(forced = true)
        }
    }

    private fun listenEventsData() {
        viewModelScope.launch {
            eventsRepo.listenEventsData().collect { result ->
                when (result) {
                    is Result.Error -> {
                        if (result.error != DataError.Network.SOCKET_ERROR) {
                            setEffect {
                                EventsContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true,
                                )
                            }
                        }
                        setState { copy(isRefreshing = false) }
                    }

                    is Result.Success -> {
                        setState {
                            copy(
                                eventsList = result.data,
                                isInitialLoading = false,
                                isRefreshing = false,
                                isDataError = false,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getEventsData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val result = eventsRepo.getEvents()) {
                is Result.Error -> {
                    if (forced) {
                        setEffect {
                            EventsContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true,
                            )
                        }
                        setState { copy(isRefreshing = false) }
                    } else {
                        getCachedData()
                    }
                }

                is Result.Success -> {
                    setState {
                        copy(
                            eventsList = result.data,
                            isInitialLoading = false,
                            isRefreshing = false,
                            isDataError = false,
                        )
                    }
                }
            }
        }
    }

    private suspend fun getCachedData() {
        val result = eventsRepo.getCachedData()
        when (result) {
            is Result.Error -> {
                setState {
                    copy(
                        eventsList = emptyList(),
                        isInitialLoading = false,
                        isRefreshing = false,
                        isDataError = true,
                    )
                }
            }

            is Result.Success -> {
                setState {
                    copy(
                        eventsList = result.data,
                        isInitialLoading = false,
                        isRefreshing = false,
                        isDataError = false,
                    )
                }
                setEffect {
                    EventsContract.Effect.Notification(
                        text = UiText(R.string.alerter_cached_results),
                        error = true,
                    )
                }
            }
        }
    }
}
