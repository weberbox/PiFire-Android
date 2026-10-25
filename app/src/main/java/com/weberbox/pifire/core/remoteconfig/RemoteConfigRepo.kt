package com.weberbox.pifire.core.remoteconfig

interface RemoteConfigRepo {
    suspend fun fetchServerSupportConfig(): ServerSupportConfigData?
    suspend fun fetchAppUpdateConfig(): AppUpdateConfigData?
    suspend fun fetchFeatureSupportConfig(): Map<String, RemoteFeatureConfigData>
}
