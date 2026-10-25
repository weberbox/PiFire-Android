package com.weberbox.pifire.feature.landing.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.feature.landing.data.model.ServerUuidData

interface LandingRepo {

    suspend fun getServerUuid(
        url: String,
        headers: Map<String, String>,
    ): Result<ServerUuidData, DataError>
}
