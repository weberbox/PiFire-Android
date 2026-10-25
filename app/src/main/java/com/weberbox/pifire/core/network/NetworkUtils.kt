package com.weberbox.pifire.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.weberbox.pifire.application.App.Companion.getAppContext
import com.weberbox.pifire.core.common.model.DataError
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
    return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
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

private fun Throwable.causes(): Sequence<Throwable> =
    generateSequence(this) { it.cause.takeIf { cause -> cause !== it } }
