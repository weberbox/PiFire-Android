package com.weberbox.pifire.feature.pellets.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.pellets.PelletsData
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.pellets.data.api.PelletsApi
import com.weberbox.pifire.feature.pellets.data.mapper.PelletsDtoToDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PelletsRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<PelletsData>,
    private val pelletsApi: PelletsApi,
    private val json: Json,
) : PelletsRepo {

    override suspend fun getPellets(): Result<Pellets, DataError> = pelletsApi.getPellets().let { result ->
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

    override suspend fun getPelletsDataState(): Flow<Pellets?> = sessionStateHolder.pelletsDataState

    override suspend fun updatePelletsData(pellets: Pellets) {
        dataStore.updateData { data ->
            if (pellets.uuid.isBlank()) return@updateData data

            val updatedMap = data.pelletsMap.toMutableMap().apply {
                put(pellets.uuid, pellets)
            }

            data.copy(pelletsMap = updatedMap)
        }
    }

    override suspend fun getPelletLevel(): Result<Unit, DataError> =
        pelletsApi.getPelletLevel()

    override suspend fun addPelletWood(wood: String): Result<Unit, DataError> =
        pelletsApi.addPelletWood(wood = wood)

    override suspend fun deletePelletWood(wood: String): Result<Unit, DataError> =
        pelletsApi.deletePelletWood(wood = wood)

    override suspend fun addPelletBrand(brand: String): Result<Unit, DataError> =
        pelletsApi.addPelletBrand(brand = brand)

    override suspend fun deletePelletBrand(brand: String): Result<Unit, DataError> =
        pelletsApi.deletePelletBrand(brand = brand)

    override suspend fun deletePelletLog(logDate: String): Result<Unit, DataError> =
        pelletsApi.deletePelletLog(logDate = logDate)

    override suspend fun loadPelletProfile(profile: String): Result<Unit, DataError> =
        pelletsApi.loadPelletProfile(profile = profile)

    override suspend fun addPelletProfile(profile: PelletProfile, load: Boolean): Result<Unit, DataError> =
        pelletsApi.addPelletProfile(profile = profile, load = load)

    override suspend fun editPelletProfile(profile: PelletProfile): Result<Unit, DataError> =
        pelletsApi.editPelletProfile(profile = profile)

    override suspend fun deletePelletProfile(profile: String): Result<Unit, DataError> =
        pelletsApi.deletePelletProfile(profile = profile)
}
