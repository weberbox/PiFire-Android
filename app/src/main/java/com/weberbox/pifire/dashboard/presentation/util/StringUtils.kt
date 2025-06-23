package com.weberbox.pifire.dashboard.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R
import java.util.Locale

@Composable
internal fun formatTemp(temp: Int): String {
    return String.format(Locale.US, "%s%s", temp, "°")
}

@Composable
internal fun formatTempWithUnits(temp: Int, units: String): String {
    if (temp > 0) {
        return String.format(Locale.US, "%s%s%s", temp, "°", units)
    }
    return "--"
}

@Composable
internal fun formatSetTemp(temp: Int): String {
    if (temp > 0) {
        return String.format(Locale.US, "%s%s", temp, "°")
    }
    return "--"
}

@Composable
internal fun formatEta(eta: Int): String {
    if (eta > 0) {
        val hours = eta / 3600
        val minutes = (eta % 3600) / 60
        val seconds = eta % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%s %02d:%02d:%02d",
                stringResource(R.string.dash_state_eta), hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%s %02d:%02d",
                stringResource(R.string.dash_state_eta), minutes, seconds)
        }
    }
    return ""
}

@Composable
internal fun formatRemainingTime(remainingTime: Int): String {
    val hours = remainingTime / 3600
    val minutes = (remainingTime % 3600) / 60
    val seconds = remainingTime % 60

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%1d:%02d:%02d", hours, minutes, seconds)
    } else if (minutes > 0) {
        String.format(Locale.getDefault(), "%1d:%02d", minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%1ds", seconds)
    }
}