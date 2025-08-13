package com.weberbox.pifire.common.data.util

import android.content.Context
import com.weberbox.pifire.application.App.Companion.getApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun readJsonFromAssets(
    fileName: String,
    context: Context = getApp().applicationContext,
): String = withContext(Dispatchers.IO) {
    context.assets.open(fileName).bufferedReader().use { it.readText() }
}