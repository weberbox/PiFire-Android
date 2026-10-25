package com.weberbox.pifire.feature.settings.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.network.api.SettingsApi
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.feature.settings.data.mapper.ManualDtoToDataMapper
import com.weberbox.pifire.feature.settings.data.model.ManualData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ManualRepoImpl @Inject constructor(
    private val settingsApi: SettingsApi,
    private val json: Json,
) : ManualRepo {
    override suspend fun getManualData(): Result<ManualData, DataError> = settingsApi.getManualData().let { result ->
        when (result) {
            is Result.Error -> Result.Error(result.error)

            is Result.Success -> {
                parseGetResponse(result.data, json, ManualDtoToDataMapper).let { data ->
                    when (data) {
                        is Result.Error -> Result.Error(data.error)

                        is Result.Success -> {
                            Result.Success(data.data)
                        }
                    }
                }
            }
        }
    }
}
