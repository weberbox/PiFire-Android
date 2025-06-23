package com.weberbox.pifire.info.data.api

import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.core.singleton.SocketManager
import jakarta.inject.Inject

class InfoApiImpl @Inject constructor(
    private val socketManager: SocketManager
) : InfoApi {

    override suspend fun getInfo(): Result<String, DataError> {
        return socketManager.emitGet(ServerConstants.GA_INFO_DATA)
    }
}