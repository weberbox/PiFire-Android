package com.weberbox.pifire.core.ui.util

import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result

object AsUiText {

    fun DataError.asUiText(): UiText = when (this) {
        DataError.Local.JSON_ERROR -> UiText(
            R.string.data_error_json_parsing,
        )

        DataError.Local.NO_CACHED_DATA -> UiText(
            R.string.data_error_no_cached_data,
        )

        DataError.Local.UNKNOWN -> UiText(
            R.string.data_error_unknown,
        )

        DataError.Network.SOCKET_ERROR -> UiText(
            R.string.data_error_socket_error,
        )

        DataError.Network.SOCKET_TIMEOUT -> UiText(
            R.string.data_error_socket_timeout,
        )

        DataError.Network.NO_CONNECTION -> UiText(
            R.string.data_error_offline,
        )

        DataError.Network.SERVER_ERROR -> UiText(
            R.string.data_error_http_on_failure,
        )

        DataError.Network.AUTHENTICATION -> UiText(
            R.string.data_error_http_auth_error,
        )

        DataError.Network.NULL_RESPONSE -> UiText(
            R.string.data_error_http_response_null,
        )

        DataError.Network.CERTIFICATE -> UiText(
            R.string.data_error_http_certificate_error,
        )

        DataError.Network.UNKNOWN_HOST -> UiText(
            R.string.data_error_unknown_host,
        )

        DataError.Network.UNREACHABLE_HOST -> UiText(
            R.string.data_error_unreachable_host,
        )

        DataError.Network.NOT_PIFIRE -> UiText(
            R.string.data_error_not_pifire,
        )

        DataError.Network.CLOUDFLARE_ACCESS -> UiText(
            R.string.data_error_cloudflare_access,
        )

        DataError.Network.UNKNOWN -> UiText(
            R.string.data_error_unknown,
        )

        is DataError.Server -> UiText(data)

        is DataError.UiText -> UiText(resId, args)
    }

    @Suppress("unused")
    fun Result.Error<*, DataError>.asErrorUiText(): UiText = error.asUiText()
}
