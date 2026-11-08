package com.weberbox.pifire.core.ui.base

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.weberbox.pifire.core.ui.model.PickerWheelTextStyle
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.materials.HazeMaterials

fun getPagerAnimationSpec(): FiniteAnimationSpec<Float> = tween(300)

@Composable
fun outlinedTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
)

@Composable
fun cardColorStops(): Array<Pair<Float, Color>> = arrayOf(
    0.0f to MaterialTheme.colorScheme.surfaceContainer,
    1f to MaterialTheme.colorScheme.surfaceContainerLow,
)

@Composable
fun gradientBackground(): Brush = Brush.verticalGradient(
    0f to MaterialTheme.colorScheme.background,
    1000f to MaterialTheme.colorScheme.surfaceContainer,
)

@Composable
fun radialGradientBackground(): Brush = Brush.radialGradient(
    0f to MaterialTheme.colorScheme.surfaceContainer,
    3000f to MaterialTheme.colorScheme.background,
    radius = 800f,
)

@Composable
fun pickerSelectedTextStyle(
    fontSize: TextUnit = 40.sp,
): PickerWheelTextStyle = PickerWheelTextStyle(
    color = MaterialTheme.colorScheme.onSurface,
    fontSize = fontSize,
    fontFamily = MaterialTheme.typography.headlineLarge.fontFamily
        ?: FontFamily.Default,
    fontWeight = FontWeight.Bold,
)

@Composable
fun pickerUnselectedTextStyle(
    fontSize: TextUnit = 20.sp,
): PickerWheelTextStyle = PickerWheelTextStyle(
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontSize = fontSize,
    fontFamily = MaterialTheme.typography.titleSmall.fontFamily
        ?: FontFamily.Default,
    fontWeight = FontWeight.Normal,
)

@Composable
fun hazeAppBarStyle(
    color: Color = MaterialTheme.colorScheme.background,
    noiseFactor: Float = 0f,
): HazeBlurStyle {
    val material = HazeMaterials.ultraThin(containerColor = color)
    // Presets compare by value, but a style built with then {} compares by identity: remember it so
    // the blur nodes don't re-resolve it on every recomposition.
    return remember(material, noiseFactor) { material.then { noiseFactor(factor = noiseFactor) } }
}
