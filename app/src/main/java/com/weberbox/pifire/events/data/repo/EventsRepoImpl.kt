package com.weberbox.pifire.events.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.common.data.parser.parseResponse
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.events.data.api.EventsApi
import com.weberbox.pifire.events.domain.EventsDtoToDataMapper
import com.weberbox.pifire.events.presentation.model.EventsData
import com.weberbox.pifire.events.presentation.model.EventsData.Events
import com.weberbox.pifire.events.presentation.model.EventsData.Events.Event
import com.weberbox.pifire.settings.data.model.local.Pref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import javax.inject.Inject

class EventsRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<EventsData>,
    private val eventsApi: EventsApi,
    private val prefs: Prefs,
    private val json: Json
) : EventsRepo {

    override suspend fun getEvents(): Result<List<Event>, DataError> {
        return eventsApi.getEvents().let { result ->
            when (result) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> {
                    parseGetResponse(result.data, json, EventsDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> Result.Error(data.error)
                            is Result.Success -> {
                                sessionStateHolder.setEventsData(data.data)
                                updateEventsData(data.data)
                                Result.Success(getLimitedEvents(data.data))
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCachedData(): Result<List<Event>, DataError.Local> {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        return if (data != null && currentServer.isNotBlank()) {
            data.eventsMap[currentServer]?.let { events ->
                sessionStateHolder.setEventsData(events)
                Result.Success(getLimitedEvents(events))
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun listenEventsData(): Flow<Result<List<Event>, DataError>> {
        val flow = eventsApi.listenEventsData()
        return flow.transform { result ->
            when (result) {
                is Result.Error -> emit(Result.Error(result.error))
                is Result.Success -> {
                    parseResponse(result.data, json, EventsDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> emit(Result.Error(data.error))
                            is Result.Success -> {
                                sessionStateHolder.setEventsData(data.data)
                                emit(Result.Success(getLimitedEvents(data.data)))
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun updateEventsData(events: Events) {
        dataStore.updateData { data ->
            if (events.uuid.isBlank()) return@updateData data

            val updatedMap = data.eventsMap.toMutableMap().apply {
                put(events.uuid, events)
            }

            data.copy(eventsMap = updatedMap)
        }
    }

    private fun getLimitedEvents(data: Events): List<Event> {
        return data.events.take(prefs.get(Pref.eventsAmount))
    }
}