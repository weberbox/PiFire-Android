package com.weberbox.pifire.events.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.events.presentation.model.EventsData.Events
import com.weberbox.pifire.events.presentation.model.EventsData.Events.Event
import kotlinx.coroutines.flow.Flow

interface EventsRepo {
    suspend fun getEvents(): Result<List<Event>, DataError>
    suspend fun getCachedData(): Result<List<Event>, DataError.Local>
    suspend fun listenEventsData(): Flow<Result<List<Event>, DataError>>
    suspend fun updateEventsData(events: Events)
}