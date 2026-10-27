package com.weberbox.pifire.core.remoteconfig

interface RemoteConfigRepo {
    /** The rule covering this app version; null when none does. */
    suspend fun fetchServerSupportRule(): ServerSupportRule?

    /** Null when neither the remote nor the bundled config decodes. */
    suspend fun fetchAppUpdateConfig(): AppUpdateConfigData?
    suspend fun fetchFeatureSupportConfig(): Map<String, RemoteFeatureConfigData>
}
