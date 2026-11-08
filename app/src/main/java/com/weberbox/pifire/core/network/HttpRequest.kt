package com.weberbox.pifire.core.network

import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.ui.util.getReasonPhrase
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Runs an HTTP request to the user's PiFire server and parses the body on Dispatchers.IO, mapping
 * every failure to an error the user can act on: offline, credentials (401), a proxy or login page
 * answering instead of PiFire, another HTTP status, or the network exception.
 */
suspend fun <T> httpRequest(
    request: suspend () -> Response<ResponseBody>,
    parse: suspend (String) -> Result<T, DataError>,
    isOnline: () -> Boolean = ::networkAvailable,
): Result<T, DataError> = withContext(Dispatchers.IO) {
    if (!isOnline()) return@withContext Result.Error(DataError.Network.NO_CONNECTION)
    try {
        val response = request()
        val code = response.code()
        when {
            response.isSuccessful ->
                response.body()?.use { body ->
                    val text = body.string()
                    proxyResponseError(
                        url = response.raw().request.url,
                        contentType = body.contentType()?.toString(),
                        body = text,
                    )?.let { error -> Result.Error(error) } ?: parse(text)
                } ?: Result.Error(DataError.Network.NULL_RESPONSE)

            code == 401 -> Result.Error(DataError.Network.AUTHENTICATION)

            else -> Result.Error(
                DataError.UiText(
                    R.string.data_error_http_unsuccessful,
                    uiTextArgsOf(code.toString(), getReasonPhrase(code)),
                ),
            )
        }.also { response.errorBody()?.close() }
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Result.Error(e.toNetworkError())
    }
}
