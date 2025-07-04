package com.weberbox.pifire.recipes.data.serializer

import androidx.datastore.core.Serializer
import com.weberbox.pifire.recipes.presentation.model.RecipesData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object RecipesSerializer : Serializer<RecipesData> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    override val defaultValue: RecipesData
        get() = RecipesData()

    override suspend fun readFrom(input: InputStream): RecipesData {
        return try {
            json.decodeFromString(
                deserializer = RecipesData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Timber.e(e, "Recipes Deserialization Error")
            defaultValue
        }
    }

    override suspend fun writeTo(t: RecipesData, output: OutputStream) {
        output.write(
            json.encodeToString(
                serializer = RecipesData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}