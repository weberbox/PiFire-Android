package com.weberbox.pifire.dashboard.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.BatteryUnknown
import androidx.compose.material.icons.outlined.Battery1Bar
import androidx.compose.material.icons.outlined.Battery2Bar
import androidx.compose.material.icons.outlined.Battery3Bar
import androidx.compose.material.icons.outlined.Battery4Bar
import androidx.compose.material.icons.outlined.Battery5Bar
import androidx.compose.material.icons.outlined.Battery6Bar
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.ui.graphics.vector.ImageVector

internal fun getBatteryIcon(percentage: Int): ImageVector {
    val clamped = percentage.coerceIn(0, 100)
    return when (clamped) {
        in 1..14 -> Icons.Outlined.Battery1Bar
        in 15..29 -> Icons.Outlined.Battery2Bar
        in 30..43 -> Icons.Outlined.Battery3Bar
        in 44..58 -> Icons.Outlined.Battery4Bar
        in 59..73 -> Icons.Outlined.Battery5Bar
        in 74..88 -> Icons.Outlined.Battery6Bar
        in 89..100 -> Icons.Outlined.BatteryFull
        else -> Icons.AutoMirrored.Outlined.BatteryUnknown
    }
}