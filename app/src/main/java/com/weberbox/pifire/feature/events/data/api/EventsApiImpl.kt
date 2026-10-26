package com.weberbox.pifire.feature.events.data.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.network.transport.PiFireTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class EventsApiImpl @Inject constructor(
    private val transport: PiFireTransport,
) : EventsApi {

    override suspend fun getEvents(): Result<String, DataError> = transport.get(ServerConstants.GA_EVENTS_DATA)

    override suspend fun listenEventsData(): Flow<Result<String, DataError>> {
        val flow = transport.listen(ServerConstants.LISTEN_EVENTS_DATA)
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
