package com.weberbox.pifire.info.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.common.data.parser.parseResponse
import com.weberbox.pifire.common.data.util.readJsonFromAssets
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.info.data.api.InfoApi
import com.weberbox.pifire.info.domain.InfoDtoToDataMapper
import com.weberbox.pifire.info.domain.LicenseDtoToDataMapper
import com.weberbox.pifire.info.presentation.model.InfoData
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.info.presentation.model.Licenses
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject

class InfoRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<InfoData>,
    private val infoApi: InfoApi,
    private val json: Json
) : InfoRepo {

    override suspend fun getInfo(): Result<Info, DataError> {
        return infoApi.getInfo().let { result ->
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

    override suspend fun getCachedData(): Result<Info, DataError.Local> {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        return if (data != null && currentServer.isNotBlank()) {
            data.infoMap[currentServer]?.let { info ->
                sessionStateHolder.setInfoData(info)
                Result.Success(info)
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun getLicences(): Result<Licenses, DataError> {
        val jsonString = readJsonFromAssets("licences.json")
        return if (jsonString.isNotBlank()) {
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