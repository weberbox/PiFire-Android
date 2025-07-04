package com.weberbox.pifire.common.presentation.util

import android.webkit.URLUtil

fun <T>findLongestString(items: List<T>, trailingString: String = ""): String {
    if (items.isEmpty()) {
        return ""
    }

    var longestString = items[0].toString() + trailingString
    for (i in 1 until items.size) {
        if (items[i].toString().length > longestString.length) {
            longestString = items[i].toString() + trailingString
        }
    }
    return longestString
}

fun createUrl(url: String): String {
    return URLUtil.guessUrl(url).replace("/$".toRegex(), "")
}

fun String.isNotSecureUrl(): Boolean {
    return !startsWith("https://", ignoreCase = true)
}

fun String?.isNotNullOrBlank(): Boolean {
    return !isNullOrBlank()
}

fun getReasonPhrase(statusCode: Int): String {
    return when (statusCode) {
        (200) -> "OK"
        (201) -> "Created"
        (202) -> "Accepted"
        (203) -> "Non Authoritative Information"
        (204) -> "No Content"
        (205) -> "Reset Content"
        (206) -> "Partial Content"
        (207) -> "Partial Update OK"
        (300) -> "Multiple Choices"
        (301) -> "Moved Permanently"
        (302) -> "Moved Temporarily"
        (303) -> "See Other"
        (304) -> "Not Modified"
        (305) -> "Use Proxy"
        (307) -> "Temporary Redirect"
        (400) -> "Bad Request"
        (401) -> "Unauthorized"
        (402) -> "Payment Required"
        (403) -> "Forbidden"
        (404) -> "Not Found"
        (405) -> "Method Not Allowed"
        (406) -> "Not Acceptable"
        (407) -> "Proxy Authentication Required"
        (408) -> "Request Timeout"
        (409) -> "Conflict"
        (410) -> "Gone"
        (411) -> "Length Required"
        (412) -> "Precondition Failed"
        (413) -> "Request Entity Too Large"
        (414) -> "Request-URI Too Long"
        (415) -> "Unsupported Media Type"
        (416) -> "Requested Range Not Satisfiable"
        (417) -> "Expectation Failed"
        (418) -> "Re-authentication Required"
        (419) -> "Proxy Re-authentication Required"
        (422) -> "Unprocessable Entity"
        (423) -> "Locked"
        (424) -> "Failed Dependency"
        (500) -> "Server Error"
        (501) -> "Not Implemented"
        (502) -> "Bad Gateway"
        (503) -> "Service Unavailable"
        (504) -> "Gateway Timeout"
        (505) -> "HTTP Version Not Supported"
        (507) -> "Insufficient Storage"
        else -> "Unknown"
    }
}