package com.weberbox.pifire.common.presentation.base

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.weberbox.pifire.common.presentation.model.PickerWheelTextStyle
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

fun getPagerAnimationSpec(): FiniteAnimationSpec<Float> {
    return tween(300)
}

@Composable
fun outlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

@Composable
fun cardColorStops(): Array<Pair<Float, Color>> {
    return arrayOf(
        0.0f to MaterialTheme.colorScheme.surfaceContainer,
        1f to MaterialTheme.colorScheme.surfaceContainerLow
    )
}

@Composable
fun gradientBackground(): Brush {
    return Brush.verticalGradient(
        0f to MaterialTheme.colorScheme.background,
        1000f to MaterialTheme.colorScheme.surfaceContainer
    )
}

@Composable
fun radialGradientBackground(): Brush {
    return Brush.radialGradient(
        0f to MaterialTheme.colorScheme.surfaceContainer,
        3000f to MaterialTheme.colorScheme.background,
        radius = 800f
    )
}

@Composable
fun pickerSelectedTextStyle(
    fontSize: TextUnit = 40.sp
): PickerWheelTextStyle {
    return PickerWheelTextStyle(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = fontSize,
        fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
            ?: FontFamily.Companion.Default,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun pickerUnselectedTextStyle(
    fontSize: TextUnit = 20.sp
): PickerWheelTextStyle {
    return PickerWheelTextStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = fontSize,
        fontFamily = MaterialTheme.typography.titleSmall.fontFamily
            ?: FontFamily.Companion.Default,
        fontWeight = FontWeight.Normal
    )
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun hazeAppBarStyle(
    color: Color = MaterialTheme.colorScheme.background,
    noiseFactor: Float = 0f
): HazeStyle {
    return HazeMaterials.ultraThin(color).copy(noiseFactor = noiseFactor)
}