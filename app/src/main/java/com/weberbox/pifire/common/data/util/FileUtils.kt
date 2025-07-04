package com.weberbox.pifire.common.data.util

import android.content.Context
import androidx.annotation.RawRes
import com.weberbox.pifire.application.App.Companion.getApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

suspend fun readRawJsonFile(
    @RawRes rawResId: Int,
    context: Context = getApp().applicationContext
): String = withContext(Dispatchers.IO) {
    val inputStream = context.resources.openRawResource(rawResId)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val json = reader.use { it.readText() }
    json
}