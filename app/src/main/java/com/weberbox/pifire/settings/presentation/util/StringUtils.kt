package com.weberbox.pifire.settings.presentation.util

internal fun <T> arrayToHashMap(keys: Array<String>, values: Array<T>): HashMap<String, T> {
    if (keys.size != values.size) {
        throw IllegalArgumentException("Arrays must have the same size")
    }

    val hashMap = HashMap<String, T>()
    for (i in keys.indices) {
        hashMap[keys[i]] = values[i]
    }
    return hashMap
}