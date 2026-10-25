package com.weberbox.pifire.feature.events.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.events.EventsData.Events
import com.weberbox.pifire.core.model.events.EventsData.Events.Event
import kotlinx.coroutines.flow.Flow

interface EventsRepo {
    suspend fun getEvents(): Result<List<Event>, DataError>
    suspend fun getCachedData(): Result<List<Event>, DataError.Local>
    suspend fun listenEventsData(): Flow<Result<List<Event>, DataError>>
    suspend fun updateEventsData(events: Events)
}