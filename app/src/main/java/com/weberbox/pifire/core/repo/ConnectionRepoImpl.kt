package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.transport.PiFireConnection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectionRepoImpl @Inject constructor(
    private val connection: PiFireConnection,
) : ConnectionRepo {

    override val isApiV2: Boolean
        get() = connection.isApiV2

    override val isApiV2Flow: Flow<Boolean>
        get() = connection.isApiV2Flow

    override suspend fun connect(server: Server): Result<Unit, DataError> = connection.connect(server = server, disconnectOnCancellation = false)

    override fun disconnect() {
        connection.disconnect()
    }
}
