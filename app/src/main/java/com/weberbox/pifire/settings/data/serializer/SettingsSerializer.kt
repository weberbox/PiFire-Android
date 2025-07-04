package com.weberbox.pifire.settings.data.serializer

import androidx.datastore.core.Serializer
import com.weberbox.pifire.settings.presentation.model.SettingsData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
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
    }

    override val defaultValue: SettingsData
        get() = SettingsData()

    override suspend fun readFrom(input: InputStream): SettingsData {
        return try {
            json.decodeFromString(
                deserializer = SettingsData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Timber.e(e, "Settings Deserialization Error")
            defaultValue
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
}