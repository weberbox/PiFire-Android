package com.weberbox.pifire.feature.landing.data.repo

import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.network.networkAvailable
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.toNetworkError
import com.weberbox.pifire.core.ui.util.getReasonPhrase
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import com.weberbox.pifire.feature.landing.data.api.LandingApi
import com.weberbox.pifire.feature.landing.data.mapper.ServerUuidDtoToDataMapper
import com.weberbox.pifire.feature.landing.data.model.ServerUuidData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LandingRepoImpl @Inject constructor(
    private val landingApi: LandingApi,
    private val json: Json,
) : LandingRepo {

    override suspend fun getServerUuid(
        url: String,
        headers: Map<String, String>,
    ): Result<ServerUuidData, DataError> {
        if (networkAvailable()) {
            try {
                landingApi.getServerUuid(url, headers).let { response ->
                    if (response.isSuccessful) {
                        return response.body()?.let { r ->
                            parseGetResponse(
                                response = r.string(),
                                json = json,
                                mapper = ServerUuidDtoToDataMapper,
                                logException = false,
                            ).let { data ->
                                r.close()
                                when (data) {
                                    is Result.Error -> Result.Error(data.error)
                                    is Result.Success -> Result.Success(data.data)
                                }
                            }
                        } ?: Result.Error(DataError.Network.NULL_RESPONSE)
                    } else {
                        return if (response.code() == 401) {
                            Result.Error(DataError.Network.AUTHENTICATION)
                        } else {
                            Result.Error(
                                DataError.UiText(
                                    R.string.data_error_http_unsuccessful,
                                    uiTextArgsOf(
                                        response.code().toString(),
                                        getReasonPhrase(response.code()),
                                    ),
                                ),
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                return Result.Error(e.toNetworkError())
            }
        } else {
            return Result.Error(DataError.Network.NO_CONNECTION)
        }
    }
}
