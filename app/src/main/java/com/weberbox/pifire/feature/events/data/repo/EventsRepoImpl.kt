package com.weberbox.pifire.feature.events.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.model.events.EventsData
import com.weberbox.pifire.core.model.events.EventsData.Events
import com.weberbox.pifire.core.model.events.EventsData.Events.Event
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.events.data.api.EventsApi
import com.weberbox.pifire.feature.events.data.mapper.EventsDtoToDataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class EventsRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<EventsData>,
    private val eventsApi: EventsApi,
    private val prefs: Prefs,
    private val json: Json,
) : EventsRepo {

    override suspend fun getEvents(): Result<List<Event>, DataError> = withContext(Dispatchers.IO) {
        eventsApi.getEvents().let { result ->
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

    override suspend fun getCachedData(): Result<List<Event>, DataError.Local> = withContext(Dispatchers.IO) {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        if (data != null && currentServer.isNotBlank()) {
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
        val parsed: Flow<Result<List<Event>, DataError>> = flow.transform { result ->
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
        return parsed.flowOn(Dispatchers.IO)
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

    private fun getLimitedEvents(data: Events): List<Event> = data.events.take(prefs.get(Pref.eventsAmount))
}
