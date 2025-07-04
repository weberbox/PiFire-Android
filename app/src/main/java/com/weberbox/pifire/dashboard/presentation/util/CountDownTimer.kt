package com.weberbox.pifire.dashboard.presentation.util

import com.weberbox.pifire.dashboard.presentation.model.TimerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class CountDownTimer(private val timerScope: CoroutineScope) {
    private var _timerState = MutableStateFlow(TimerData())
    private var timerJob: Job? = null

    val timerState: StateFlow<TimerData> = _timerState

    fun startTimer(totalSeconds: Long, totalDuration: Long) {
        if (timerJob == null) {
            timerJob = timerScope.launch {
                initTimer(totalSeconds, totalDuration)
                    .onStart {
                        _timerState.value = _timerState.value.copy(
                            totalSeconds = totalSeconds,
                            totalDuration = totalDuration
                        )
                    }
                    .collect {
                        _timerState.emit(it)
                    }
            }
        }
    }

    fun cancelTimer() {
        timerScope.launch {
            _timerState.emit(TimerData())
            timerJob?.cancel()
            timerJob = null
        }
    }

    fun pauseTimer(secondsRemaining: Long, totalSeconds: Long, totalDuration: Long) {
        timerScope.launch {
            _timerState.value = _timerState.value.copy(
                secondsRemaining = secondsRemaining,
                totalSeconds = totalSeconds,
                totalDuration = totalDuration,
                timerPaused = true
            )
            timerJob?.cancel()
            timerJob = null
        }
    }

    private fun initTimer(totalSeconds: Long, totalDuration: Long): Flow<TimerData> =
        (totalSeconds - 1 downTo 0).asFlow()
            .onEach {
                delay(1000)
            }
            .conflate()
            .transform { remainingSeconds: Long ->
                emit(
                    TimerData(
                        totalSeconds = totalSeconds,
                        totalDuration = totalDuration,
                        secondsRemaining = remainingSeconds
                    )
                )
            }
}