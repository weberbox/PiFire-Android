package com.weberbox.pifire.common.presentation.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.util.UiText
import kotlinx.serialization.json.Json

@Suppress("unused")
inline fun <reified T> serializationSaver(defaultValue: T): Saver<T, String> {
    val json = Json {
        encodeDefaults = true
        isLenient = true
        coerceInputValues = true
    }
    return object : Saver<T, String> {
        override fun SaverScope.save(value: T): String {
            return json.encodeToString(value)
        }

        override fun restore(value: String): T? {
            return try {
                json.decodeFromString<T>(value)
            } catch (_: Exception) {
                defaultValue
            }
        }
    }
}

object ErrorStatusSaver : Saver<MutableState<ErrorStatus>, Map<String, Any?>> {
    override fun SaverScope.save(value: MutableState<ErrorStatus>): Map<String, Any?> {
        return mapOf(
            "isError" to value.value.isError,
            "errorMsg" to value.value.errorMsg
        )
    }

    override fun restore(value: Map<String, Any?>): MutableState<ErrorStatus> {
        val isError = value["isError"] as Boolean
        val errorMsg = value["errorMsg"] as UiText?
        return mutableStateOf(ErrorStatus(isError = isError, errorMsg = errorMsg))
    }
}

object FieldInputSaver : Saver<MutableState<FieldInput>, Map<String, Any?>> {
    override fun SaverScope.save(value: MutableState<FieldInput>): Map<String, Any?> {
        return mapOf(
            "value" to value.value.value,
            "hasInteracted" to value.value.hasInteracted
        )
    }

    override fun restore(value: Map<String, Any?>): MutableState<FieldInput> {
        val input = value["value"] as String
        val interacted = value["hasInteracted"] as Boolean
        return mutableStateOf(FieldInput(value = input, hasInteracted = interacted))
    }
}

