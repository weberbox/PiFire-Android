package com.weberbox.pifire.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.weberbox.pifire.application.App.Companion.getAppContext
import com.weberbox.pifire.core.common.model.DataError
import okhttp3.HttpUrl
import timber.log.Timber
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.PortUnreachableException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import javax.net.ssl.SSLPeerUnverifiedException

fun networkAvailable(): Boolean {
    val connectivityManager =
        getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw)
    return actNw != null && (
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        )
}

/**
 * Maps a failed HTTP request to an error that tells the user what to fix on their server,
 * instead of showing raw exception text.
 */
fun Throwable.toNetworkError(): DataError.Network {
    Timber.d("Request failed: ${this::class.java.simpleName}")
    return when {
        causes().any {
            it is SSLPeerUnverifiedException || it is CertificateException
        } -> DataError.Network.CERTIFICATE

        this is UnknownHostException -> DataError.Network.UNKNOWN_HOST

        this is ConnectException ||
            this is NoRouteToHostException ||
            this is PortUnreachableException -> DataError.Network.UNREACHABLE_HOST

        // SocketTimeoutException and OkHttp call timeouts are both InterruptedIOException
        this is InterruptedIOException -> DataError.Network.SOCKET_TIMEOUT

        else -> DataError.Network.SERVER_ERROR
    }
}

/**
 * The error for a successful response that came from something in front of PiFire rather than
 * PiFire itself, or null if it looks like PiFire's. OkHttp follows redirects, so a proxy login page
 * arrives as a 200 with HTML, which would otherwise surface as a JSON error. [url] is the final URL after
 * redirects; [body] may be empty when it can't be read (e.g. a stream).
 */
fun proxyResponseError(
    url: HttpUrl,
    contentType: String?,
    body: String,
): DataError.Network? = when {
    url.host.endsWith(CLOUDFLARE_ACCESS_HOST) ||
        url.encodedPath.startsWith(CLOUDFLARE_ACCESS_PATH) -> DataError.Network.CLOUDFLARE_ACCESS

    contentType?.contains("html", ignoreCase = true) == true ||
        body.trimStart().startsWith("<") -> DataError.Network.NOT_PIFIRE

    else -> null
}

/** Errors a reconnect can't get past until the user changes something on their side. */
fun DataError.blocksConnection(): Boolean = this == DataError.Network.NOT_PIFIRE ||
    this == DataError.Network.CLOUDFLARE_ACCESS ||
    this == DataError.Network.AUTHENTICATION

private const val CLOUDFLARE_ACCESS_HOST = ".cloudflareaccess.com"
private const val CLOUDFLARE_ACCESS_PATH = "/cdn-cgi/access/"

private fun Throwable.causes(): Sequence<Throwable> =
    generateSequence(this) { it.cause.takeIf { cause -> cause !== it } }
