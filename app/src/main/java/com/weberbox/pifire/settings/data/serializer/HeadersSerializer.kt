package com.weberbox.pifire.settings.data.serializer

import androidx.datastore.core.Serializer
import com.weberbox.pifire.settings.data.model.local.HeadersData
import com.weberbox.pifire.settings.data.util.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

object HeadersSerializer : Serializer<HeadersData> {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    override val defaultValue: HeadersData
        get() = HeadersData()

    override suspend fun readFrom(input: InputStream): HeadersData {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }
        val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedBytes)
        val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
        val decodedJsonString = decryptedBytes.decodeToString()
        return try {
            json.decodeFromString(decodedJsonString)
        } catch (e: SerializationException) {
            Timber.e(e, "Headers Deserialization Error")
            defaultValue
        }
    }

    override suspend fun writeTo(t: HeadersData, output: OutputStream) {
        val jsonString = json.encodeToString(t)
        val bytes = jsonString.toByteArray()
        val encryptedBytes = Crypto.encrypt(bytes)
        val encryptedBytesBase64 = Base64.getEncoder().encode(encryptedBytes)
        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}