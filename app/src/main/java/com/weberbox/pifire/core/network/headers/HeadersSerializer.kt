package com.weberbox.pifire.core.network.headers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.weberbox.pifire.core.data.crypto.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        // Base64, keystore and JSON failures all go to the corruption handler instead of
        // crashing every reader of this DataStore.
        return try {
            val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedBytes)
            val decryptedBytes = Crypto.decrypt(encryptedBytesDecoded)
            json.decodeFromString(decryptedBytes.decodeToString())
        } catch (e: Exception) {
            Timber.e(e, "Headers Deserialization Error")
            throw CorruptionException(message = "Unable to read headers data", cause = e)
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
