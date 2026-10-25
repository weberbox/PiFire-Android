package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.model.settings.SettingsData.Server

/** The live socket connection to the selected PiFire server. */
interface ConnectionRepo {
    /** Connects to [server], replacing any current connection; false if it couldn't connect. */
    suspend fun connect(server: Server): Boolean
    fun disconnect()
}
