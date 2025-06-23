package com.weberbox.pifire.events.data.api

import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.core.singleton.SocketManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class EventsApiImpl @Inject constructor(
    private val socketManager: SocketManager
) : EventsApi {

    override suspend fun getEvents(): Result<String, DataError> {
        return socketManager.emitGet(ServerConstants.GA_EVENTS_DATA)
    }

    override suspend fun listenEventsData(): Flow<Result<String, DataError>> {
        val flow = socketManager.onFlow(ServerConstants.LISTEN_EVENTS_DATA)
        return flow.transform { result ->
            when (result) {
                is Result.Error -> emit(Result.Error(result.error))
                is Result.Success -> {
                    if (result.data.isNotEmpty() && result.data[0] != null) {
                        emit(Result.Success(result.data[0].toString()))
                    } else {
                        emit(Result.Error(DataError.Network.SERVER_ERROR))
                    }
                }
            }
        }
    }
}