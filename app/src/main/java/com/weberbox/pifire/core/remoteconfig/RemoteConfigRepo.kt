package com.weberbox.pifire.core.remoteconfig

interface RemoteConfigRepo {
    suspend fun fetchServerSupportConfig(): ServerSupportConfigData?

    /** Every release listed, newest or not; empty when neither the remote nor bundled list decodes. */
    suspend fun fetchAppUpdateConfig(): List<AppUpdateConfigData>
    suspend fun fetchFeatureSupportConfig(): Map<String, RemoteFeatureConfigData>
}
