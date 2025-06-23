package com.weberbox.pifire.common.data.repo

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.util.readRawJsonFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.util.AppUpdateConfigData
import com.weberbox.pifire.core.util.ServerSupportConfigData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class RemoteConfigRepositoryImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val json: Json
) : RemoteConfigRepository {
    override suspend fun fetchServerSupportConfig(): ServerSupportConfigData? {
        val defaults = mapOf(
            AppConfig.SERVER_SUPPORT_CONFIG to readRawJsonFile(R.raw.server_support_config)
        )
        return try {
            remoteConfig.setDefaultsAsync(defaults).await()
            remoteConfig.fetchAndActivate().await().let {
                if (!it) Timber.d("Server Support Using Local Configs")
            }

            val jsonString = remoteConfig.getString(AppConfig.SERVER_SUPPORT_CONFIG)
            val parsed = json.decodeFromString<List<ServerSupportConfigData>>(jsonString)
            val currentAppVersion = BuildConfig.VERSION_CODE

            return parsed.find { it.appVersionCode == currentAppVersion }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e,"Firebase Server Support fetch exception")
            null
        }
    }

    override suspend fun fetchAppUpdateConfig(): AppUpdateConfigData? {
        val defaults = mapOf(
            AppConfig.APP_UPDATE_CONFIG to readRawJsonFile(R.raw.app_update_config)
        )
        return try {
            remoteConfig.setDefaultsAsync(defaults).await()
            remoteConfig.fetchAndActivate().await().also {
                if (!it) Timber.d("App Update Using Local Configs")
            }

            val jsonString = remoteConfig.getString(AppConfig.APP_UPDATE_CONFIG)
            val parsed = json.decodeFromString<List<AppUpdateConfigData>>(jsonString)
            val currentAppVersion = BuildConfig.VERSION_CODE

            val latestUpdate = parsed
                .sortedByDescending { it.appVersionCode }
                .firstOrNull { it.appVersionCode > currentAppVersion }

            return latestUpdate
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e(e,"Firebase App Update fetch exception")
            null
        }
    }
}