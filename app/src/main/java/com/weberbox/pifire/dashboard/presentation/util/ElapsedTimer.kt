package com.weberbox.pifire.dashboard.presentation.util

import com.weberbox.pifire.dashboard.presentation.model.ElapsedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

class ElapsedTimer(private val timerScope: CoroutineScope) {
    private val _elapsedState = MutableStateFlow(ElapsedData())
    private var timerJob: Job? = null
    private var runningStartTime: Int? = null

    val elapsedState: StateFlow<ElapsedData> = _elapsedState.asStateFlow()

    /** Restarts only when [startTime] changes, e.g. the grill was started again. */
    fun startElapsed(startTime: Double) {
        val start = startTime.toInt()
        if (timerJob?.isActive == true && runningStartTime == start) return
        timerJob?.cancel()
        runningStartTime = start
        timerJob = timerScope.launch {
            while (true) {
                _elapsedState.value = ElapsedData(
                    startTime = start,
                    timeElapsed = Instant.now().epochSecond.toInt() - start,
                )
                delay(1000.milliseconds)
            }
        }
    }

    fun cancelElapsed() {
        timerJob?.cancel()
        timerJob = null
        runningStartTime = null
        _elapsedState.value = ElapsedData()
    }
}
