package com.weberbox.pifire.feature.setup.data.util

import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.feature.settings.data.model.local.HeadersData.Headers
import kotlinx.coroutines.flow.Flow

interface ServerDataCache {
    fun saveServerData(server: Server)
    fun saveHeaderData(headers: Headers)
    suspend fun getServerData(): Flow<Server>
    suspend fun getHeaderData(): Flow<Headers>
    fun clearServerData()
}