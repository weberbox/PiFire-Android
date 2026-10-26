package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.transport.PiFireConnection
import javax.inject.Inject

class ConnectionRepoImpl @Inject constructor(
    private val connection: PiFireConnection,
) : ConnectionRepo {

    override val isApiV2: Boolean
        get() = connection.isApiV2

    override suspend fun connect(server: Server): Boolean = connection.connect(server = server, disconnectOnCancellation = false)

    override fun disconnect() {
        connection.disconnect()
    }
}
