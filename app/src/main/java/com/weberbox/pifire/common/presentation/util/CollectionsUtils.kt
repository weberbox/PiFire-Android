package com.weberbox.pifire.common.presentation.util

import java.util.Collections

fun <T> List<T>.toImmutableList(): List<T> = when (size) {
    0 -> emptyList()
    1 -> listOf(first())
    else -> Collections.unmodifiableList(toList())
}

fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> {
    return Collections.unmodifiableMap(this.toMap())
}