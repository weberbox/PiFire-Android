package com.weberbox.pifire.common.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.palette.graphics.Palette

private fun convertByteArrayToBitmap(
    imageData: ByteArray?
): Bitmap? {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData?.size ?: 0)
}

private fun extractColorsFromBitmap(
    bitmap: Bitmap,
    defaultColor: Color
): ColorSwatches {
    val maximumColorCount = 10
    return ColorSwatches(
        vibrant = parseColorSwatch(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().vibrantSwatch, defaultColor
        ),
        darkVibrant = parseColorSwatch(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().darkVibrantSwatch, defaultColor
        ),
        onDarkVibrant = parseBodyColor(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().darkVibrantSwatch?.bodyTextColor, defaultColor
        ),
        lightVibrant = parseColorSwatch(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().lightVibrantSwatch, defaultColor
        ),
        domainSwatch = parseColorSwatch(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().dominantSwatch, defaultColor
        ),
        mutedSwatch = parseColorSwatch(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().mutedSwatch, defaultColor
        ),
        lightMuted = parseColorSwatch(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().lightMutedSwatch, defaultColor
        ),
        darkMuted = parseColorSwatch(
            color = Palette.from(bitmap).maximumColorCount(maximumColorCount)
                .generate().darkMutedSwatch, defaultColor
        )
    )
}

private fun parseColorSwatch(
    color: Palette.Swatch?,
    defaultColor: Color
): Color {
    return if (color != null) {
        val rgbColor = color.rgb
        Color(
            red = rgbColor.red,
            green = rgbColor.green,
            blue = rgbColor.blue,
            alpha = rgbColor.alpha
        )
    } else {
        defaultColor
    }
}

private fun parseBodyColor(
    color: Int?,
    defaultColor: Color
): Color {
    return if (color != null) {
        Color(
            red = color.red,
            green = color.green,
            blue = color.blue,
            alpha = color.alpha
        )
    } else {
        defaultColor
    }
}

private fun getSwatchColor(
    swatchType: SwatchType,
    colors: ColorSwatches
): Color {
    return when (swatchType) {
        SwatchType.Vibrant -> colors.vibrant
        SwatchType.DarkVibrant -> colors.darkVibrant
        SwatchType.OnDarkVibrant -> colors.onDarkVibrant
        SwatchType.LightVibrant -> colors.lightVibrant
        SwatchType.DomainSwatch -> colors.domainSwatch
        SwatchType.MutedSwatch -> colors.mutedSwatch
        SwatchType.LightMuted -> colors.lightMuted
        SwatchType.DarkMuted -> colors.darkMuted
    }
}

private data class ColorSwatches(
    val vibrant: Color,
    val darkVibrant: Color,
    val onDarkVibrant: Color,
    val lightVibrant: Color,
    val domainSwatch: Color,
    val mutedSwatch: Color,
    val lightMuted: Color,
    val darkMuted: Color
)

enum class SwatchType {
    Vibrant,
    DarkVibrant,
    OnDarkVibrant,
    LightVibrant,
    DomainSwatch,
    MutedSwatch,
    LightMuted,
    DarkMuted
}

@Composable
fun rememberPaletteColorState(
    startingColor: Color = Color.Transparent,
    endingColor: Color = Color.Transparent,
    swatchType: SwatchType = SwatchType.DarkVibrant
): MutableState<PaletteState> {
    return remember { mutableStateOf(PaletteState(startingColor, endingColor, swatchType)) }
}

class PaletteState internal constructor(
    private val starting: Color,
    private val ending: Color,
    private val swatchType: SwatchType
) {
    val startingColorState: MutableState<Color> = mutableStateOf(starting)
    val endingColorState: MutableState<Color> = mutableStateOf(ending)
    val leftToRight: MutableState<Boolean> = mutableStateOf(false)

    fun updateStartingColor(imageData: ByteArray?) {
        val bitmap = convertByteArrayToBitmap(imageData)
        leftToRight.value = true
        bitmap?.let {
            val colors = extractColorsFromBitmap(it, starting)
            startingColorState.value = getSwatchColor(swatchType, colors)
        }
    }

    @Suppress("unused")
    fun updateEndingColor(imageData: ByteArray?) {
        val bitmap = convertByteArrayToBitmap(imageData)
        leftToRight.value = false
        bitmap?.let {
            val colors = extractColorsFromBitmap(it, ending)
            endingColorState.value = getSwatchColor(swatchType, colors)
        }
    }

    val startingColor: Color
        get() = startingColorState.value

    val endingColor: Color
        get() = endingColorState.value

    val brushColor: List<Color>
        get() = if (leftToRight.value)
            listOf(startingColor.copy(alpha = 0.5f), endingColor)
        else
            listOf(startingColor, endingColor.copy(alpha = 0.5f))
}