package com.weberbox.pifire.dashboard.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.common.data.parser.parseResponse
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.dashboard.data.api.DashApi
import com.weberbox.pifire.dashboard.domain.DashDtoToDataMapper
import com.weberbox.pifire.dashboard.presentation.model.DashData
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DashRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<DashData>,
    private val dashApi: DashApi,
    private val json: Json
): DashRepo {

    override suspend fun getDashData(): Result<Dash, DataError> {
        return dashApi.getDashData().let { result ->
            when (result) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> {
                    parseGetResponse(result.data, json, DashDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> Result.Error(data.error)
                            is Result.Success -> {
                                sessionStateHolder.setDashData(data.data)
                                updateDashData(data.data)
                                Result.Success(data.data)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCachedData(): Result<Dash, DataError.Local> {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        return if (data != null && currentServer.isNotBlank()) {
            data.dashMap[currentServer]?.let { dash ->
                sessionStateHolder.setDashData(dash)
                Result.Success(dash)
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun listenDashData(): Flow<Result<Dash, DataError>> {
        val flow = dashApi.listenDashData()
        return flow.transform { result ->
            when (result) {
                is Result.Error -> emit(Result.Error(result.error))
                is Result.Success -> {
                    parseResponse(result.data, json, DashDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> emit(Result.Error(data.error))
                            is Result.Success -> {
                                sessionStateHolder.setDashData(data.data)
                                emit(Result.Success(data.data))
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun updateDashData(dash: Dash) {
        dataStore.updateData { data ->
            if (dash.uuid.isBlank()) return@updateData data

            val updatedMap = data.dashMap.toMutableMap().apply {
                put(dash.uuid, dash)
            }

            data.copy(dashMap = updatedMap)
        }
    }
}