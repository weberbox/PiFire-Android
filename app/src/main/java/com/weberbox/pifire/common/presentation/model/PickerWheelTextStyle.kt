package com.weberbox.pifire.common.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class PickerWheelTextStyle(
    val color: Color = Color(0xFF404040),
    val fontSize: TextUnit = 24.sp,
    val fontFamily: FontFamily = FontFamily.Companion.Default,
    val fontWeight: FontWeight = FontWeight.Companion.Normal
) {
    fun toTextStyle(): TextStyle {
        return TextStyle(
            color = color,
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight
        )
    }
}