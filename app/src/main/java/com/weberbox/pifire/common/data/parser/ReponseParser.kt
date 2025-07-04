package com.weberbox.pifire.common.data.parser

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.ResponseDto
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import timber.log.Timber

suspend inline fun <reified I, O> parseResponse(
    response: String,
    json: Json,
    mapper: Mapper<I, O>
): Result<O, DataError> {
    try {
        val inputDto = json.decodeFromString<I>(response)
        val mappedData = mapper.map(inputDto)

        return Result.Success(mappedData)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Timber.e(e, "Parsing Exception")
    }
    return Result.Error(DataError.Local.JSON_ERROR)
}

suspend fun parsePostResponse(
    response: String,
    json: Json,
): Result<Unit, DataError> {
    try {
        val responseDto = json.decodeFromString<ResponseDto>(response)
        return if (responseDto.result?.equals("OK", ignoreCase = true) == true) {
            Result.Success(Unit)
        } else {
            responseDto.message?.let { message ->
                Result.Error(DataError.Server(message))
            } ?: Result.Error(DataError.Network.UNKNOWN)
        }
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Timber.e(e, "Parsing Exception")
    }
    return Result.Error(DataError.Local.JSON_ERROR)
}

suspend inline fun <reified I, O> parseGetResponse(
    response: String,
    json: Json,
    mapper: Mapper<I, O>,
    logException: Boolean = true
): Result<O, DataError> {
    try {
        val responseDto = json.decodeFromString<ResponseDto>(response)
        return if (responseDto.result?.equals("OK", ignoreCase = true) == true) {
            responseDto.data?.let { data ->
                val inputDto = json.decodeFromJsonElement<I>(data)
                Result.Success(mapper.map(inputDto))
            } ?: Result.Error(DataError.Network.NULL_RESPONSE)
        } else {
            responseDto.message?.let { message ->
                Result.Error(DataError.Server(message))
            } ?: Result.Error(DataError.Network.UNKNOWN)
        }
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        if (logException)
            Timber.e(e, "Parsing Exception")
        else
            Timber.d(e, "Parsing Exception")
    }
    return Result.Error(DataError.Local.JSON_ERROR)
}