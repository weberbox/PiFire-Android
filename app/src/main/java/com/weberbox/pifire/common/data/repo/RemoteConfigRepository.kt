package com.weberbox.pifire.common.data.repo

import com.weberbox.pifire.core.util.AppUpdateConfigData
import com.weberbox.pifire.core.util.ServerSupportConfigData

interface RemoteConfigRepository {
    suspend fun fetchServerSupportConfig(): ServerSupportConfigData?
    suspend fun fetchAppUpdateConfig(): AppUpdateConfigData?
}
