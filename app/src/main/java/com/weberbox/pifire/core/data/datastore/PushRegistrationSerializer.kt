package com.weberbox.pifire.core.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.weberbox.pifire.core.data.crypto.Crypto
import com.weberbox.pifire.core.model.push.PushRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

object PushRegistrationSerializer : Serializer<PushRegistration> {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override val defaultValue: PushRegistration
        get() = PushRegistration()

    override suspend fun readFrom(input: InputStream): PushRegistration {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }
        // Base64, keystore and JSON failures go to the corruption handler; a lost key only means
        // registering with the relay again.
        return try {
            val decryptedBytes = Crypto.decrypt(Base64.getDecoder().decode(encryptedBytes))
            json.decodeFromString(
                deserializer = PushRegistration.serializer(),
                string = decryptedBytes.decodeToString(),
            )
        } catch (e: Exception) {
            Timber.e(e, "Push registration deserialization error")
            throw CorruptionException(message = "Unable to read push registration", cause = e)
        }
    }

    override suspend fun writeTo(t: PushRegistration, output: OutputStream) {
        val bytes = json.encodeToString(
            serializer = PushRegistration.serializer(),
            value = t,
        ).toByteArray()
        val encryptedBytesBase64 = Base64.getEncoder().encode(Crypto.encrypt(bytes))
        withContext(Dispatchers.IO) {
            output.use { it.write(encryptedBytesBase64) }
        }
    }
}
