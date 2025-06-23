package com.weberbox.pifire.events.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.events.data.repo.EventsRepo
import com.weberbox.pifire.events.presentation.contract.EventsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventsRepo: EventsRepo
) : BaseViewModel<EventsContract.Event, EventsContract.State, EventsContract.Effect>() {

    init {
        getEventsData(false)
        listenEventsData()
    }

    override fun setInitialState() = EventsContract.State(
        eventsList = emptyList(),
        isInitialLoading = true,
        isDataError = false,
        isRefreshing = false
    )

    override fun handleEvents(event: EventsContract.Event) {
        when (event) {
            EventsContract.Event.Refresh -> getEventsData(forced = true)
        }
    }

    private fun listenEventsData() {
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepo.listenEventsData().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Error -> {
                            if (result.error != DataError.Network.SOCKET_ERROR) {
                                setEffect {
                                    EventsContract.Effect.Notification(
                                        text = result.error.asUiText(),
                                        error = true
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
                                    isRefreshing = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getEventsData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = eventsRepo.getEvents()) {
                is Result.Error -> {
                    if (forced) {
                        withContext(Dispatchers.Main) {
                            setEffect {
                                EventsContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true
                                )
                            }
                            setState { copy(isRefreshing = false) }
                        }
                    } else {
                        getCachedData()
                    }
                }

                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        setState {
                            copy(
                                eventsList = result.data,
                                isInitialLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getCachedData() {
        val result = eventsRepo.getCachedData()
        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Error -> {
                    setState {
                        copy(
                            eventsList = emptyList(),
                            isInitialLoading = false,
                            isRefreshing = false,
                            isDataError = true
                        )
                    }
                }

                is Result.Success -> {
                    setState {
                        copy(
                            eventsList = result.data,
                            isInitialLoading = false,
                            isRefreshing = false
                        )
                    }
                    setEffect {
                        EventsContract.Effect.Notification(
                            text = UiText(R.string.not_connected_cached_results),
                            error = true
                        )
                    }
                }
            }
        }
    }
}