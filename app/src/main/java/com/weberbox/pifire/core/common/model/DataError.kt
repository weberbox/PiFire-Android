@file:Suppress("DEPRECATION_ERROR")

package com.weberbox.pifire.core.common.model

import com.weberbox.pifire.core.ui.util.UiTextArgList

sealed interface DataError : Error {
    enum class Network : DataError {
        NO_CONNECTION,
        SOCKET_ERROR,
        SOCKET_TIMEOUT,
        SERVER_ERROR,
        AUTHENTICATION,
        NULL_RESPONSE,
        CERTIFICATE,
        UNKNOWN_HOST,
        UNREACHABLE_HOST,
        UNKNOWN
    }

    enum class Local : DataError {
        JSON_ERROR,
        NO_CACHED_DATA,
        UNKNOWN
    }

    data class Server(val data: String) : DataError
    data class UiText(val resId: Int, val args: UiTextArgList) : DataError
}