package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.SocketManager
import javax.inject.Inject

class ConnectionRepoImpl @Inject constructor(
    private val socketManager: SocketManager,
) : ConnectionRepo {

    override suspend fun connect(server: Server): Boolean = socketManager.initSocket(server)

    override fun disconnect() {
        socketManager.disconnect()
    }
}
