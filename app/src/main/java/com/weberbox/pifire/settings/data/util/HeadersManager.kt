package com.weberbox.pifire.settings.data.util

import androidx.datastore.core.DataStore
import com.weberbox.pifire.common.domain.Uuid
import com.weberbox.pifire.common.presentation.util.toImmutableMap
import com.weberbox.pifire.settings.data.model.local.HeadersData
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.BasicAuth
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.ExtraHeader
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import okhttp3.Credentials.basic
import javax.inject.Inject

class HeadersManager @Inject constructor(
    private val dataStore: DataStore<HeadersData>
) {

    suspend fun updateExtraHeader(uuid: Uuid, header: ExtraHeader) {
        dataStore.updateData { current ->
            val currentHeaders = current.headersMap[uuid] ?: Headers()
            val updatedHeaders = currentHeaders.copy(
                extraHeaders = currentHeaders.extraHeaders.addOrUpdateByKey(header)
            )
            current.copy(
                headersMap = current.headersMap + (uuid to updatedHeaders)
            )
        }
    }

    suspend fun deleteExtraHeader(uuid: Uuid, header: ExtraHeader) {
        dataStore.updateData { current ->
            val currentHeaders = current.headersMap[uuid] ?: Headers()
            val updatedHeaders = currentHeaders.copy(
                extraHeaders = currentHeaders.extraHeaders.filterNot {
                    it.key == header.key
                }
            )
            current.copy(
                headersMap = current.headersMap + (uuid to updatedHeaders)
            )
        }
    }

    suspend fun setBasicAuth(uuid: Uuid, auth: BasicAuth) {
        dataStore.updateData { current ->
            val currentHeaders = current.headersMap[uuid] ?: Headers()
            val updatedHeaders = currentHeaders.copy(
                credentials = auth
            )
            current.copy(
                headersMap = current.headersMap + (uuid to updatedHeaders)
            )
        }
    }

    fun getExtraHeadersFlow(uuid: String): Flow<List<ExtraHeader>> {
        return dataStore.data.map { data ->
            data.headersMap[uuid]?.extraHeaders ?: emptyList()
        }
    }

    fun getBasicAuthFlow(uuid: String): Flow<BasicAuth> {
        return dataStore.data.map { data ->
            data.headersMap[uuid]?.credentials ?: BasicAuth()
        }
    }

    suspend fun buildServerHeadersList(server: Server): Map<String, List<String>> {
        val data = dataStore.data.firstOrNull() ?: return emptyMap()
        val headers = data.headersMap[server.uuid] ?: return emptyMap()

        val result = buildMap {
            if (server.credentialsEnabled) {
                with(headers.credentials) {
                    if (user.isNotBlank() || pass.isNotBlank()) {
                        put("Authorization", listOf(basic(user, pass)))
                    }
                }
            }

            if (server.headersEnabled) {
                headers.extraHeaders
                    .filter { it.key.isNotBlank() }
                    .forEach { put(it.key, listOf(it.value)) }
            }
        }

        return result
    }

    suspend fun buildServerHeaders(server: Server): Map<String, String> {
        val data = dataStore.data.firstOrNull() ?: return emptyMap()
        val entry = data.headersMap[server.uuid] ?: return emptyMap()

        return buildMap {
            if (server.credentialsEnabled) {
                with(entry.credentials) {
                    if (user.isNotBlank() || pass.isNotBlank()) {
                        put("Authorization", basic(user, pass))
                    }
                }
            }

            if (server.headersEnabled) {
                entry.extraHeaders
                    .filter { it.key.isNotBlank() }
                    .forEach { put(it.key, it.value) }
            }
        }
    }

    fun buildSetupServerHeaders(
        credentials: BasicAuth,
        headers: List<ExtraHeader>
    ): Map<String, String> {
        val headersMap = buildMap {
            if (credentials.user.isNotBlank() || credentials.pass.isNotBlank()) {
                put("Authorization", basic(credentials.user, credentials.pass))
            }

            headers.forEach { header ->
                put(header.key, header.value)
            }
        }

        return headersMap.toImmutableMap()
    }

    private fun List<ExtraHeader>.addOrUpdateByKey(header: ExtraHeader): List<ExtraHeader> {
        val index = indexOfFirst { it.key == header.key }
        return if (index >= 0) {
            toMutableList().apply { this[index] = header }
        } else {
            this + header
        }
    }

}