package com.weberbox.pifire.landing.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.landing.presentation.model.ServerUuidData

interface LandingRepo {

    suspend fun getServerUuid(
        url: String,
        headers: Map<String, String>
    ): Result<ServerUuidData, DataError>

}