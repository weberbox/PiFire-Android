package com.weberbox.pifire.feature.info.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.data.util.readJsonFromAssets
import com.weberbox.pifire.core.model.info.InfoData
import com.weberbox.pifire.core.model.info.InfoData.Info
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.info.data.api.InfoApi
import com.weberbox.pifire.feature.info.data.mapper.InfoDtoToDataMapper
import com.weberbox.pifire.feature.info.data.mapper.LicenseDtoToDataMapper
import com.weberbox.pifire.feature.info.data.model.Licenses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class InfoRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<InfoData>,
    private val infoApi: InfoApi,
    private val json: Json,
) : InfoRepo {

    override suspend fun getInfo(): Result<Info, DataError> = withContext(Dispatchers.IO) {
        infoApi.getInfo().let { result ->
            when (result) {
                is Result.Error -> Result.Error(result.error)

                is Result.Success -> {
                    parseGetResponse(result.data, json, InfoDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> Result.Error(data.error)

                            is Result.Success -> {
                                sessionStateHolder.setInfoData(data.data)
                                updateInfoData(data.data)
                                Result.Success(data.data)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCachedData(): Result<Info, DataError.Local> = withContext(Dispatchers.IO) {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        if (data != null && currentServer.isNotBlank()) {
            data.infoMap[currentServer]?.let { info ->
                sessionStateHolder.setInfoData(info)
                Result.Success(info)
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun getLicences(): Result<Licenses, DataError> = withContext(Dispatchers.IO) {
        val jsonString = readJsonFromAssets("licences.json")
        if (jsonString.isNotBlank()) {
            parseResponse(jsonString, json, LicenseDtoToDataMapper).let { data ->
                when (data) {
                    is Result.Error -> Result.Error(data.error)
                    is Result.Success -> Result.Success(data.data)
                }
            }
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun updateInfoData(info: Info) {
        dataStore.updateData { data ->
            if (info.uuid.isBlank()) return@updateData data

            val updatedMap = data.infoMap.toMutableMap().apply {
                put(info.uuid, info)
            }

            data.copy(infoMap = updatedMap)
        }
    }
}
