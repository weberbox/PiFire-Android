package com.weberbox.pifire.feature.setup.data.util

import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.headers.HeadersData.Headers
import kotlinx.coroutines.flow.Flow

interface ServerDataCache {
    fun saveServerData(server: Server)
    fun saveHeaderData(headers: Headers)
    suspend fun getServerData(): Flow<Server>
    suspend fun getHeaderData(): Flow<Headers>
    fun clearServerData()
}
