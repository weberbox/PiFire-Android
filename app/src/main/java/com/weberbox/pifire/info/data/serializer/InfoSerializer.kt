package com.weberbox.pifire.info.data.serializer

import androidx.datastore.core.Serializer
import com.weberbox.pifire.info.presentation.model.InfoData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object InfoSerializer : Serializer<InfoData> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    override val defaultValue: InfoData
        get() = InfoData()

    override suspend fun readFrom(input: InputStream): InfoData {
        return try {
            json.decodeFromString(
                deserializer = InfoData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Timber.e(e, "Info Deserialization Error")
            defaultValue
        }
    }

    override suspend fun writeTo(t: InfoData, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = InfoData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}