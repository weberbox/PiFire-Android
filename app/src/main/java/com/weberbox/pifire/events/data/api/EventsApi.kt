package com.weberbox.pifire.events.data.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import kotlinx.coroutines.flow.Flow

interface EventsApi {
    suspend fun getEvents(): Result<String, DataError>
    suspend fun listenEventsData(): Flow<Result<String, DataError>>
}