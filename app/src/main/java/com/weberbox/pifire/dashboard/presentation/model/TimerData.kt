package com.weberbox.pifire.dashboard.presentation.model

import java.util.Locale

data class TimerData(
    val secondsRemaining: Long = 0,
    val totalSeconds: Long = 0,
    val totalDuration: Long = 0,
    val timerPaused: Boolean = false,
) {
    val remainingTime: String = formatTimeRemaining(secondsRemaining)
    private val progressPercentage: Int = secondsRemaining.toInt()
    private val progressMax: Int = totalDuration.toInt()

    override fun toString(): String =
        "TotalSeconds: $totalSeconds, progress: $progressPercentage, progressMax $progressMax, " +
                "timer paused: $timerPaused, remainingTime $remainingTime"

    private fun formatTimeRemaining(secondsRemaining: Long): String {
        val hours = secondsRemaining / 3600
        val minutes = (secondsRemaining % 3600) / 60
        val seconds = (secondsRemaining % 60)

        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}