package com.weberbox.pifire.common.data.util

import java.net.URLEncoder

@Suppress("unused")
class UrlBuilder private constructor(private val baseUrl: String) {
    private val paths = mutableListOf<String>()
    private val queryParams = mutableMapOf<String, String>()

    fun addPath(path: String) = apply {
        paths.add(path)
    }

    fun addQueryParam(key: String, value: String) = apply {
        queryParams[key] = value
    }

    fun addQueryParams(params: Map<String, String>) = apply {
        queryParams.putAll(params)
    }

    fun build(): String {
        val fullPath = paths.joinToString("/") { it.trim('/') }
        val baseWithPath = "$baseUrl/$fullPath".trimEnd('/')

        val queryString = if (queryParams.isNotEmpty()) {
            queryParams.entries.joinToString("&") {
                "${encode(it.key)}=${encode(it.value)}"
            }
        } else {
            null
        }

        return if (queryString != null) "$baseWithPath?$queryString" else baseWithPath
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, Charsets.UTF_8.name())

    companion object {
        fun from(baseUrl: String) = UrlBuilder(baseUrl)
    }
}