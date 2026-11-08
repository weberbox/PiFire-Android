package com.weberbox.pifire.core.network

import com.weberbox.pifire.core.common.model.DataError
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

class NetworkErrorTest {

    @Test
    fun `untrusted or mismatched certificates map to CERTIFICATE`() {
        val untrusted = SSLHandshakeException("handshake").apply {
            initCause(CertificateException("Trust anchor for certification path not found"))
        }
        assertEquals(DataError.Network.CERTIFICATE, untrusted.toNetworkError())
        assertEquals(DataError.Network.CERTIFICATE, SSLPeerUnverifiedException("hostname").toNetworkError())
    }

    @Test
    fun `a TLS failure without a certificate cause is a server error`() {
        assertEquals(DataError.Network.SERVER_ERROR, SSLHandshakeException("protocol").toNetworkError())
    }

    @Test
    fun `host, connection and timeout failures map to their own errors`() {
        assertEquals(DataError.Network.UNKNOWN_HOST, UnknownHostException("pifire.local").toNetworkError())
        assertEquals(DataError.Network.UNREACHABLE_HOST, ConnectException("refused").toNetworkError())
        assertEquals(DataError.Network.SOCKET_TIMEOUT, SocketTimeoutException("timeout").toNetworkError())
        assertEquals(DataError.Network.SERVER_ERROR, IOException("other").toNetworkError())
    }

    @Test
    fun `PiFire's JSON is not a proxy response`() {
        val url = "https://pifire.example.com/api/get/versions".toHttpUrl()
        assertNull(proxyResponseError(url = url, contentType = "application/json", body = """{"result":"OK"}"""))
        assertNull(proxyResponseError(url = url, contentType = "text/event-stream", body = ""))
    }

    @Test
    fun `an HTML page or a Cloudflare Access redirect is`() {
        val pifire = "https://pifire.example.com/api/get/versions".toHttpUrl()
        assertEquals(
            DataError.Network.NOT_PIFIRE,
            proxyResponseError(url = pifire, contentType = "text/html; charset=utf-8", body = ""),
        )
        assertEquals(
            DataError.Network.NOT_PIFIRE,
            proxyResponseError(url = pifire, contentType = null, body = "\n  <!DOCTYPE html>"),
        )
        assertEquals(
            DataError.Network.CLOUDFLARE_ACCESS,
            proxyResponseError(
                url = "https://team.cloudflareaccess.com/cdn-cgi/access/login/pifire.example.com".toHttpUrl(),
                contentType = "text/html",
                body = "<html></html>",
            ),
        )
        assertEquals(
            DataError.Network.CLOUDFLARE_ACCESS,
            proxyResponseError(
                url = "https://pifire.example.com/cdn-cgi/access/login".toHttpUrl(),
                contentType = "text/html",
                body = "",
            ),
        )
    }
}
