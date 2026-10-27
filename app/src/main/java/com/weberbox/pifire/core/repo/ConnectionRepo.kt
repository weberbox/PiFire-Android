package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.model.settings.SettingsData.Server
import kotlinx.coroutines.flow.Flow

/** The live connection to the selected PiFire server (Socket.IO or API v2). */
interface ConnectionRepo {
    /** True while connected through API v2 (docs/V2_API.md). */
    val isApiV2: Boolean

    /** [isApiV2], updated on every connect. */
    val isApiV2Flow: Flow<Boolean>

    /** Connects to [server], replacing any current connection; false if it couldn't connect. */
    suspend fun connect(server: Server): Boolean
    fun disconnect()
}
