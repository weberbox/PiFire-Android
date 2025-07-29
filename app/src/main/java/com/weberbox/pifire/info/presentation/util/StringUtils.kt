package com.weberbox.pifire.info.presentation.util

import java.util.Locale

internal fun formatBytes(bytes: Long?): String {
    return bytes?.let {
        val gb = it.toDouble() / (1024 * 1024 * 1024)
        val mb = it.toDouble() / (1024 * 1024)
        "%.2f GB (%.0f MB)".format(gb, mb)
    } ?: "Unknown"
}

internal fun formatPercentage(value: Double?): String {
    return when {
        value == null -> "0"
        value % 1.0 == 0.0 -> value.toInt().toString()
        else -> String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
    }
}