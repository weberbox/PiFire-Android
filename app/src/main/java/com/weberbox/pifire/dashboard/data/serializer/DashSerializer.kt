package com.weberbox.pifire.dashboard.data.serializer

import androidx.datastore.core.Serializer
import com.weberbox.pifire.dashboard.presentation.model.DashData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object DashSerializer : Serializer<DashData> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    override val defaultValue: DashData
        get() = DashData()

    override suspend fun readFrom(input: InputStream): DashData {
        return try {
            json.decodeFromString(
                deserializer = DashData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Timber.e(e, "Dash Deserialization Error")
            defaultValue
        }
    }

    override suspend fun writeTo(t: DashData, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = DashData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}