package com.weberbox.pifire.feature.pellets.data.serializer

import androidx.datastore.core.Serializer
import com.weberbox.pifire.core.model.pellets.PelletsData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object PelletsSerializer : Serializer<PelletsData> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
        allowSpecialFloatingPointValues = true
    }

    override val defaultValue: PelletsData
        get() = PelletsData()

    override suspend fun readFrom(input: InputStream): PelletsData = try {
        json.decodeFromString(
            deserializer = PelletsData.serializer(),
            string = input.readBytes().decodeToString(),
        )
    } catch (e: SerializationException) {
        Timber.e(e, "Pellets Deserialization Error")
        defaultValue
    }

    override suspend fun writeTo(t: PelletsData, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = PelletsData.serializer(),
                value = t,
            ).encodeToByteArray(),
        )
    }
}
