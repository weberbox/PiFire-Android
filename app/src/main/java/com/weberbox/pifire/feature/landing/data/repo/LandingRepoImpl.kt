package com.weberbox.pifire.feature.landing.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.network.httpRequest
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.feature.landing.data.api.LandingApi
import com.weberbox.pifire.feature.landing.data.mapper.ServerUuidDtoToDataMapper
import com.weberbox.pifire.feature.landing.data.model.ServerUuidData
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LandingRepoImpl @Inject constructor(
    private val landingApi: LandingApi,
    private val json: Json,
) : LandingRepo {

    override suspend fun getServerUuid(
        url: String,
        headers: Map<String, String>,
    ): Result<ServerUuidData, DataError> = httpRequest(
        request = { landingApi.getServerUuid(url, headers) },
        parse = { body ->
            parseGetResponse(
                response = body,
                json = json,
                mapper = ServerUuidDtoToDataMapper,
                logException = false,
            )
        },
    )
}
