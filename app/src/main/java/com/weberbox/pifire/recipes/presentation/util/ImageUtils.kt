package com.weberbox.pifire.recipes.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Suppress("unused")
internal fun ByteArray.toImageBitmap(): ImageBitmap = toAndroidBitmap().asImageBitmap()

internal fun ByteArray.toAndroidBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size)
}

internal fun decodeBase64Image(encodedString: String): ByteArray? {
    return try {
        Base64.decode(encodedString, Base64.DEFAULT)
    } catch (_: IllegalArgumentException) {
        null
    }
}

@Suppress("unused")
@Composable
internal fun coinImageBuilder(
    data: Any?,
    fallbackImage: Any,
    crossfade: Boolean = true
): ImageRequest {
    return ImageRequest.Builder(LocalContext.current)
        .data(data ?: fallbackImage)
        .crossfade(crossfade)
        .build()
}

@Composable
internal fun coinImageBuilder(
    data: Any?,
    crossfade: Boolean = true
): ImageRequest {
    return ImageRequest.Builder(LocalContext.current)
        .data(data)
        .crossfade(crossfade)
        .build()
}