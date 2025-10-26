package com.weberbox.pifire.common.data.util

import android.content.Context

fun getAppLanguage(context: Context): String {
    val config = context.resources.configuration
    val locale = config.locales[0]
    return locale.toLanguageTag()
}