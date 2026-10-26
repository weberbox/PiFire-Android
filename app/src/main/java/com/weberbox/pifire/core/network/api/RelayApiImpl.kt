package com.weberbox.pifire.core.network.api

import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.andThen
import com.weberbox.pifire.core.common.model.map
import com.weberbox.pifire.core.network.model.push.RelayRegisterDto
import com.weberbox.pifire.core.network.model.push.RelayResponseDto
import com.weberbox.pifire.core.network.model.push.RelaySendDto
import com.weberbox.pifire.core.network.networkAvailable
import com.weberbox.pifire.core.network.toNetworkError
import com.weberbox.pifire.core.ui.util.getReasonPhrase
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url
import javax.inject.Inject

class RelayApiImpl @Inject constructor(
    retrofit: Retrofit,
    private val json: Json,
) : RelayApi {

    override val isConfigured: Boolean = BuildConfig.PUSH_RELAY_URL.isNotBlank()

    override val baseUrl: String = BuildConfig.PUSH_RELAY_URL.trimEnd('/')

    private val service = retrofit.create(RelayService::class.java)
    private val registerUrl = "$baseUrl/v1/register"
    private val sendUrl = "$baseUrl/v1/send"

    override suspend fun register(fcmToken: String): Result<String, DataError> = call {
        service.post(
            url = registerUrl,
            body = encode(serializer = RelayRegisterDto.serializer(), value = RelayRegisterDto(fcmToken = fcmToken)),
        )
    }.andThen { response ->
        response.pushKey?.let { Result.Success(it) } ?: Result.Error(DataError.Network.NULL_RESPONSE)
    }

    override suspend fun updateToken(pushKey: String, fcmToken: String): Result<Unit, DataError> = call {
        service.put(
            url = registerUrl,
            body = encode(
                serializer = RelayRegisterDto.serializer(),
                value = RelayRegisterDto(fcmToken = fcmToken, pushKey = pushKey),
            ),
        )
    }.map { }

    override suspend fun send(alert: RelaySendDto): Result<Unit, DataError> = call {
        service.post(url = sendUrl, body = encode(serializer = RelaySendDto.serializer(), value = alert))
    }.map { }

    private fun <T> encode(serializer: KSerializer<T>, value: T): RequestBody = json
        .encodeToString(serializer = serializer, value = value)
        .toRequestBody("application/json".toMediaType())

    // Like httpRequest, but reads the relay's error code from failed responses too.
    private suspend fun call(request: suspend () -> Response<ResponseBody>): Result<RelayResponseDto, DataError> = withContext(Dispatchers.IO) {
        if (!networkAvailable()) return@withContext Result.Error(DataError.Network.NO_CONNECTION)
        try {
            val response = request()
            val text = (if (response.isSuccessful) response.body() else response.errorBody())?.use { it.string() }
            val body = text?.let { decodeOrNull(it) }
            when {
                response.isSuccessful && body?.ok == true -> Result.Success(body)

                body?.error != null -> Result.Error(DataError.Server(body.error))

                else -> Result.Error(
                    DataError.UiText(
                        resId = R.string.data_error_push_relay_http,
                        args = uiTextArgsOf(response.code().toString(), getReasonPhrase(response.code())),
                    ),
                )
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Result.Error(e.toNetworkError())
        }
    }

    private fun decodeOrNull(text: String): RelayResponseDto? = try {
        json.decodeFromString(deserializer = RelayResponseDto.serializer(), string = text)
    } catch (_: IllegalArgumentException) {
        null
    }

    internal interface RelayService {
        @POST
        suspend fun post(@Url url: String, @Body body: RequestBody): Response<ResponseBody>

        @PUT
        suspend fun put(@Url url: String, @Body body: RequestBody): Response<ResponseBody>
    }
}
