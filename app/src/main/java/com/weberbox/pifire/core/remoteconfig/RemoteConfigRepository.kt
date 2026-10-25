package com.weberbox.pifire.core.remoteconfig


interface RemoteConfigRepository {
    suspend fun fetchServerSupportConfig(): ServerSupportConfigData?
    suspend fun fetchAppUpdateConfig(): AppUpdateConfigData?
    suspend fun fetchFeatureSupportConfig(): Map<String, RemoteFeatureConfigData>
}
