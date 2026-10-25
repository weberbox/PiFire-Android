package com.weberbox.pifire.core.common.util

/** An item with a list key that is unique within its list. */
data class KeyedItem<T>(val key: String, val value: T)

/**
 * Keys each item by [id], numbering repeats (`id#1`, `id#2`) so a server list with duplicate
 * entries can't crash a LazyColumn, while keys stay stable across updates.
 */
fun <T> List<T>.withUniqueKeys(id: (T) -> Any): List<KeyedItem<T>> {
    val seen = mutableMapOf<String, Int>()
    return map { item ->
        val base = id(item).toString()
        val count = seen.getOrDefault(base, 0)
        seen[base] = count + 1
        KeyedItem(key = if (count == 0) base else "$base#$count", value = item)
    }
}
