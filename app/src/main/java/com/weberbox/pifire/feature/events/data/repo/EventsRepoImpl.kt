package com.weberbox.pifire.feature.events.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.map
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.model.events.EventsData
import com.weberbox.pifire.core.model.events.EventsData.Events
import com.weberbox.pifire.core.model.events.EventsData.Events.Event
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.repo.ServerDataSync
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.events.data.api.EventsApi
import com.weberbox.pifire.feature.events.data.mapper.EventsDtoToDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class EventsRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<EventsData>,
    private val eventsApi: EventsApi,
    private val prefs: Prefs,
    private val json: Json,
) : EventsRepo {

    private val sync = ServerDataSync(
        currentServerUuid = { sessionStateHolder.currentServerUuid },
        publish = sessionStateHolder::setEventsData,
        readCache = { serverUuid -> dataStore.data.firstOrNull()?.eventsMap?.get(serverUuid) },
        writeCache = ::updateEventsData,
    )

    override suspend fun getEvents(): Result<List<Event>, DataError> = sync.fetch(
        request = eventsApi::getEvents,
        parse = { parseGetResponse(it, json, EventsDtoToDataMapper) },
    ).map(::getLimitedEvents)

    override suspend fun getCachedData(): Result<List<Event>, DataError.Local> =
        sync.cached().map(::getLimitedEvents)

    override suspend fun listenEventsData(): Flow<Result<List<Event>, DataError>> = sync.listen(
        updates = eventsApi.listenEventsData(),
        parse = { parseResponse(it, json, EventsDtoToDataMapper) },
    ).map { result -> result.map(::getLimitedEvents) }

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
