package com.weberbox.pifire.core.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlin.math.roundToInt

// Celsius servers send decimals where Fahrenheit servers send whole numbers.
object IntOrDoubleSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("IntOrDouble", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int? = decoder.asJsonDecoder().decodeJsonElement().toIntOrNull()

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Int?) {
        if (value != null) {
            encoder.encodeInt(value)
        } else {
            encoder.encodeNull()
        }
    }
}

// The whole list becomes null if any entry isn't a number: dropping one would shift the entries
// that are paired by index (e.g. PWM temps and duty cycles).
object IntOrDoubleListSerializer : KSerializer<List<Int>?> {
    private val listSerializer = ListSerializer(Int.serializer())

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun deserialize(decoder: Decoder): List<Int>? {
        val element = decoder.asJsonDecoder().decodeJsonElement() as? JsonArray ?: return null
        return element.map { it.toIntOrNull() ?: return null }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: List<Int>?) {
        if (value != null) {
            encoder.encodeSerializableValue(listSerializer, value)
        } else {
            encoder.encodeNull()
        }
    }
}

private fun Decoder.asJsonDecoder(): JsonDecoder = this as? JsonDecoder ?: throw SerializationException(
    "This serializer only works with JSON",
)

private fun JsonElement.toIntOrNull(): Int? {
    val primitive = this as? JsonPrimitive ?: return null
    return primitive.intOrNull
        ?: primitive.doubleOrNull?.takeIf { it.isFinite() }?.roundToInt()
}
