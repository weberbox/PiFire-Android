package com.weberbox.pifire.core.network

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

class HttpRequestTest {

    private val parseBody: suspend (String) -> Result<String, DataError> = { Result.Success("parsed:$it") }

    private suspend fun request(
        online: Boolean = true,
        request: suspend () -> Response<ResponseBody>,
    ): Result<String, DataError> = httpRequest(request = request, parse = parseBody, isOnline = { online })

    @Test
    fun `a successful response parses its body`() = runTest {
        assertEquals(Result.Success<String, DataError>("parsed:{}"), request { Response.success("{}".toResponseBody()) })
    }

    @Test
    fun `an HTML page instead of PiFire is not a JSON error`() = runTest {
        val loginPage = "<!DOCTYPE html><html>Sign in</html>".toResponseBody("text/html".toMediaType())
        assertEquals(
            Result.Error<String, DataError>(DataError.Network.NOT_PIFIRE),
            request { Response.success(loginPage) },
        )
    }

    @Test
    fun `a redirect to Cloudflare Access names it`() = runTest {
        val raw = okhttp3.Response.Builder()
            .request(Request.Builder().url("https://team.cloudflareaccess.com/cdn-cgi/access/login/pifire.example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        assertEquals(
            Result.Error<String, DataError>(DataError.Network.CLOUDFLARE_ACCESS),
            request { Response.success("<html></html>".toResponseBody(), raw) },
        )
    }

    @Test
    fun `offline skips the request`() = runTest {
        var requested = false
        val result = request(online = false) {
            requested = true
            Response.success("{}".toResponseBody())
        }
        assertEquals(Result.Error<String, DataError>(DataError.Network.NO_CONNECTION), result)
        assertEquals(false, requested)
    }

    @Test
    fun `401 is an authentication error and other statuses name the code`() = runTest {
        assertEquals(
            Result.Error<String, DataError>(DataError.Network.AUTHENTICATION),
            request { Response.error<ResponseBody>(401, "".toResponseBody()) },
        )
        val serverError = request { Response.error<ResponseBody>(500, "".toResponseBody()) }
        assertTrue((serverError as Result.Error<*, *>).error is DataError.UiText)
    }

    @Test
    fun `network exceptions map to their own errors`() = runTest {
        assertEquals(
            Result.Error<String, DataError>(DataError.Network.UNKNOWN_HOST),
            request { throw UnknownHostException("pifire.local") },
        )
    }
}
