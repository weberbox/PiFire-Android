package com.weberbox.pifire.feature.info.data.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.network.transport.PiFireTransport
import javax.inject.Inject

class InfoApiImpl @Inject constructor(
    private val transport: PiFireTransport,
) : InfoApi {

    override suspend fun getInfo(): Result<String, DataError> = transport.get(ServerConstants.GA_INFO_DATA)
}
