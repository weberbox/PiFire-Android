package com.weberbox.pifire.events.data.serializer

import androidx.datastore.core.Serializer
import com.weberbox.pifire.events.presentation.model.EventsData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object EventsSerializer : Serializer<EventsData> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    override val defaultValue: EventsData
        get() = EventsData()

    override suspend fun readFrom(input: InputStream): EventsData {
        return try {
            json.decodeFromString(
                deserializer = EventsData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Timber.e(e, "Events Deserialization Error")
            defaultValue
        }
    }

    override suspend fun writeTo(t: EventsData, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = EventsData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}