package com.weberbox.pifire.common.presentation.util

import android.util.Patterns
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.model.ErrorStatus

fun validateUrl(url: String): ErrorStatus {
    return when {
        url.trim().isEmpty() -> {
            ErrorStatus(true, UiText(R.string.error_text_blank))
        }

        !Patterns.WEB_URL.matcher(url).matches() -> {
            ErrorStatus(true, UiText(R.string.error_invalid_url))
        }

        else -> {
            ErrorStatus(false, null)
        }
    }
}

fun validateName(name: String): ErrorStatus {
    return when {
        name.trim().isEmpty() -> {
            ErrorStatus(true, UiText(R.string.error_text_blank))
        }

        else -> {
            ErrorStatus(false)
        }
    }
}

@Suppress("unused")
fun validateEmail(email: String): ErrorStatus {
    val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")
    return when {
        email.trim().isEmpty() -> {
            ErrorStatus(true, UiText(R.string.error_text_blank))
        }

        !email.trim().matches(emailPattern) -> {
            ErrorStatus(true, UiText(R.string.error_email_invalid))
        }

        else -> {
            ErrorStatus(false)
        }
    }
}
