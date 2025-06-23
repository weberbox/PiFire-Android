package com.weberbox.pifire.dashboard.presentation.model

import java.util.Locale

data class ElapsedData(
    val startTime: Int = 0,
    val timeElapsed: Int = 0
) {
    val elapsedTime = formatDuration(timeElapsed)

    override fun toString(): String = "Start time: $startTime, elapsedTime: $elapsedTime"

    private fun formatDuration(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = (totalSeconds % 60)

        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}