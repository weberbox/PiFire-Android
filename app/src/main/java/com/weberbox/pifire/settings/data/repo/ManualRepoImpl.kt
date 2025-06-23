package com.weberbox.pifire.settings.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.settings.data.api.SettingsApi
import com.weberbox.pifire.settings.domain.ManualDtoToDataMapper
import com.weberbox.pifire.settings.presentation.model.ManualData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ManualRepoImpl @Inject constructor(
    private val settingsApi: SettingsApi,
    private val json: Json
): ManualRepo {
    override suspend fun getManualData(): Result<ManualData, DataError> {
        return settingsApi.getManualData().let { result ->
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
}