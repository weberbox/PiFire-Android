package com.weberbox.pifire.dashboard.presentation.util

import com.weberbox.pifire.dashboard.presentation.model.TimerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

class CountDownTimer(private val timerScope: CoroutineScope) {
    private val _timerState = MutableStateFlow(TimerData())
    private var timerJob: Job? = null
    private var runningEndTime: Long? = null

    val timerState: StateFlow<TimerData> = _timerState.asStateFlow()

    /** Restarts only when [endEpochSeconds] changes, e.g. the timer was extended or restarted. */
    fun startTimer(endEpochSeconds: Long, totalDuration: Long) {
        if (timerJob?.isActive == true && runningEndTime == endEpochSeconds) return
        stopJob()
        runningEndTime = endEpochSeconds
        val totalSeconds = secondsUntil(endEpochSeconds)
        timerJob = timerScope.launch {
            // Read from the clock each tick so delays don't make the countdown drift
            while (true) {
                val remaining = secondsUntil(endEpochSeconds)
                _timerState.value = TimerData(
                    secondsRemaining = remaining,
                    totalSeconds = totalSeconds,
                    totalDuration = totalDuration,
                )
                if (remaining == 0L) break
                delay(1000.milliseconds)
            }
        }
    }

    fun cancelTimer() {
        stopJob()
        _timerState.value = TimerData()
    }

    fun pauseTimer(secondsRemaining: Long, totalSeconds: Long, totalDuration: Long) {
        stopJob()
        _timerState.value = TimerData(
            secondsRemaining = secondsRemaining,
            totalSeconds = totalSeconds,
            totalDuration = totalDuration,
            timerPaused = true,
        )
    }

    private fun stopJob() {
        timerJob?.cancel()
        timerJob = null
        runningEndTime = null
    }

    private fun secondsUntil(endEpochSeconds: Long): Long =
        (endEpochSeconds - Instant.now().epochSecond).coerceAtLeast(0)
}
