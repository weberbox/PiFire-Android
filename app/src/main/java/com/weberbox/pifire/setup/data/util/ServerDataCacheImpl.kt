package com.weberbox.pifire.setup.data.util

import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ServerDataCacheImpl @Inject constructor(
    private val prefs: Prefs,
    private val json: Json
): ServerDataCache {

    override fun saveServerData(server: Server) {
        prefs.set(Pref.serverSetupSession, json.encodeToString(server))
    }

    override suspend fun getServerData(): Flow<Server> = callbackFlow {
        prefs.collectPrefsFlow(Pref.serverSetupSession).collect {
            if (it.isNotBlank()) {
                try {
                    send(json.decodeFromString<Server>(it))
                } catch (_: Exception) {
                    send(Server())
                }
            } else {
                send(Server())
            }
        }
    }

    override fun saveHeaderData(headers: Headers) {
        prefs.set(Pref.headersSetupSession, json.encodeToString(headers))
    }

    override suspend fun getHeaderData(): Flow<Headers> = callbackFlow {
        prefs.collectPrefsFlow(Pref.headersSetupSession).collect {
            if (it.isNotBlank()) {
                try {
                    send(json.decodeFromString<Headers>(it))
                } catch (_: Exception) {
                    send(Headers())
                }
            } else {
                send(Headers())
            }
        }
    }

    override fun clearServerData() {
        prefs.clear(Pref.serverSetupSession)
        prefs.clear(Pref.headersSetupSession)
    }
}