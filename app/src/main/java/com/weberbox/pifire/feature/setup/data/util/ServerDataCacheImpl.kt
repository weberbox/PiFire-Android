package com.weberbox.pifire.feature.setup.data.util

import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.feature.settings.data.model.local.HeadersData.Headers
import com.weberbox.pifire.feature.settings.data.model.local.Pref
import com.weberbox.pifire.feature.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ServerDataCacheImpl @Inject constructor(
    private val prefs: Prefs,
    private val json: Json
): ServerDataCache {

    override fun saveServerData(server: Server) {
        prefs.set(Pref.serverSetupSession, json.encodeToString(server))
    }

    override suspend fun getServerData(): Flow<Server> =
        prefs.collectPrefsFlow(Pref.serverSetupSession).map { decodeOrDefault(it) { Server() } }

    override fun saveHeaderData(headers: Headers) {
        prefs.set(Pref.headersSetupSession, json.encodeToString(headers))
    }

    override suspend fun getHeaderData(): Flow<Headers> =
        prefs.collectPrefsFlow(Pref.headersSetupSession).map { decodeOrDefault(it) { Headers() } }

    override fun clearServerData() {
        prefs.clear(Pref.serverSetupSession)
        prefs.clear(Pref.headersSetupSession)
    }

    private inline fun <reified T> decodeOrDefault(value: String, default: () -> T): T {
        if (value.isBlank()) return default()
        return try {
            json.decodeFromString<T>(value)
        } catch (_: Exception) {
            default()
        }
    }
}