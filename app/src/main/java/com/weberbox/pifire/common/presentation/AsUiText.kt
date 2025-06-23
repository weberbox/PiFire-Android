package com.weberbox.pifire.common.presentation

import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.util.UiText

object AsUiText {

    fun DataError.asUiText(): UiText {
        return when (this) {
            DataError.Local.JSON_ERROR -> UiText(
                R.string.data_error_json_parsing
            )

            DataError.Local.SERIALIZATION -> UiText(
                R.string.data_error_serialization
            )

            DataError.Local.INDEX_ERROR -> UiText(
                R.string.data_error_indexing
            )

            DataError.Local.NO_CACHED_DATA -> UiText(
                R.string.data_error_no_cached_data
            )

            DataError.Local.UNKNOWN -> UiText(
                R.string.data_error_unknown
            )

            DataError.Network.SOCKET_ERROR -> UiText(
                R.string.data_error_socket_error
            )

            DataError.Network.SOCKET_TIMEOUT -> UiText(
                R.string.data_error_socket_timeout
            )

            DataError.Network.NO_CONNECTION -> UiText(
                R.string.data_error_offline
            )

            DataError.Network.SERVER_ERROR -> UiText(
                R.string.data_error_http_on_failure
            )

            DataError.Network.URI_ERROR -> UiText(
                R.string.data_error_uri_error
            )

            DataError.Network.AUTHENTICATION -> UiText(
                R.string.data_error_http_certificate_error
            )

            DataError.Network.NULL_RESPONSE -> UiText(
                R.string.data_error_http_response_null
            )

            DataError.Network.UNSUCCESSFUL -> UiText(
                R.string.data_error_http_unsuccessful
            )

            DataError.Network.CERTIFICATE -> UiText(
                R.string.data_error_http_auth_error
            )

            DataError.Network.UNKNOWN -> UiText(
                R.string.data_error_unknown
            )

            is DataError.Server -> UiText(data)

            is DataError.UiText -> UiText(resId, args)

        }
    }

    @Suppress("unused")
    fun Result.Error<*, DataError>.asErrorUiText(): UiText {
        return error.asUiText()
    }
}