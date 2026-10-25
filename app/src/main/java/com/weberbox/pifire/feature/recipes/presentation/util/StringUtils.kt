package com.weberbox.pifire.feature.recipes.presentation.util

internal fun List<String>.toBulletedList(): String {
    if (this.isNotEmpty()) {
        val separator = "\n • "
        return this.joinToString(separator, prefix = separator, postfix = "\n")
    } else {
        return String()
    }
}
