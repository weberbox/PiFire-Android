package com.weberbox.pifire.core.network

import com.weberbox.pifire.core.common.model.DataError
import org.junit.Assert.assertEquals
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
}
