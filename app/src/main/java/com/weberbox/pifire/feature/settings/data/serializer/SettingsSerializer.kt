package com.weberbox.pifire.feature.settings.data.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.weberbox.pifire.feature.settings.presentation.model.SettingsData
import com.weberbox.pifire.feature.settings.presentation.model.SettingsData.Server
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object SettingsSerializer : Serializer<SettingsData> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
        allowSpecialFloatingPointValues = true
    }

    override val defaultValue: SettingsData
        get() = SettingsData()

    override suspend fun readFrom(input: InputStream): SettingsData {
        val text = input.readBytes().decodeToString()
        return try {
            json.decodeFromString(
                deserializer = SettingsData.serializer(),
                string = text,
            )
        } catch (e: IllegalArgumentException) {
            // SerializationException is an IllegalArgumentException
            Timber.e(e, "Settings Deserialization Error")
            salvageServers(text) ?: throw CorruptionException(
                message = "Settings data unreadable",
                cause = e,
            )
        }
    }

    override suspend fun writeTo(t: SettingsData, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = SettingsData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }

    // The saved servers are user-entered and can't be recovered once overwritten, while each
    // server's settings are a cache of the PiFire server that gets re-fetched, so keep the servers
    // and drop the settings that fail to decode. Returns null when no saved server is readable.
    private fun salvageServers(text: String): SettingsData? {
        val root = decodeOrNull { json.parseToJsonElement(text) } as? JsonObject ?: return null
        val rawServers = root["serverMap"] as? JsonObject ?: return null
        val servers = rawServers.mapNotNull { (uuid, element) ->
            decodeServer(element)?.let { server -> uuid to server }
        }.toMap()
        if (servers.isEmpty() && rawServers.isNotEmpty()) return null

        val currentServerUuid = (root["currentServerUuid"] as? JsonPrimitive)?.contentOrNull
        Timber.w("Salvaged %d of %d saved servers", servers.size, rawServers.size)
        return SettingsData(
            currentServerUuid = currentServerUuid?.takeIf { it in servers }.orEmpty(),
            serverMap = servers,
        )
    }

    private fun decodeServer(element: JsonElement): Server? {
        decodeOrNull { json.decodeFromJsonElement(Server.serializer(), element) }?.let { return it }
        val withoutSettings = (element as? JsonObject)?.let { JsonObject(it - "settings") }
            ?: return null
        return decodeOrNull { json.decodeFromJsonElement(Server.serializer(), withoutSettings) }
    }

    private inline fun <T> decodeOrNull(block: () -> T): T? {
        return try {
            block()
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
