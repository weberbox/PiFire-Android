package com.weberbox.pifire.pellets.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.common.data.parser.parseResponse
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.pellets.data.api.PelletsApi
import com.weberbox.pifire.pellets.domain.PelletsDtoToDataMapper
import com.weberbox.pifire.pellets.presentation.model.PelletsData
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PelletsRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<PelletsData>,
    private val pelletsApi: PelletsApi,
    private val json: Json
) : PelletsRepo {

    override suspend fun getPellets(): Result<Pellets, DataError> {
        return pelletsApi.getPellets().let { result ->
            when (result) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> {
                    parseGetResponse(result.data, json, PelletsDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> Result.Error(data.error)
                            is Result.Success -> {
                                sessionStateHolder.setPelletsData(data.data)
                                updatePelletsData(data.data)
                                Result.Success(data.data)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCachedData(): Result<Pellets, DataError.Local> {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        return if (data != null && currentServer.isNotBlank()) {
            data.pelletsMap[currentServer]?.let { pellets ->
                sessionStateHolder.setPelletsData(pellets)
                Result.Success(pellets)
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun listenPelletsData(): Flow<Result<Pellets, DataError>> {
        val flow = pelletsApi.listenPelletsData()
        return flow.transform { result ->
            when (result) {
                is Result.Error -> emit(Result.Error(result.error))
                is Result.Success -> {
                    parseResponse(result.data, json, PelletsDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> emit(Result.Error(data.error))
                            is Result.Success -> {
                                sessionStateHolder.setPelletsData(data.data)
                                emit(Result.Success(data.data))
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getPelletsDataState(): Flow<Pellets?> {
        return sessionStateHolder.pelletsDataState
    }

    override suspend fun updatePelletsData(pellets: Pellets) {
        dataStore.updateData { data ->
            if (pellets.uuid.isBlank()) return@updateData data

            val updatedMap = data.pelletsMap.toMutableMap().apply {
                put(pellets.uuid, pellets)
            }

            data.copy(pelletsMap = updatedMap)
        }
    }
}