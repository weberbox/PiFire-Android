package com.weberbox.pifire.setup.data.util

import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow

interface ServerDataCache {
    fun saveServerData(server: Server)
    fun saveHeaderData(headers: Headers)
    suspend fun getServerData(): Flow<Server>
    suspend fun getHeaderData(): Flow<Headers>
    fun clearServerData()
}