package com.weberbox.pifire.dashboard.presentation.util

import com.weberbox.pifire.dashboard.presentation.model.ElapsedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.time.Instant

class ElapsedTimer(private val timerScope: CoroutineScope) {

    private var _elapsedState = MutableStateFlow(ElapsedData())
    private var timerJob: Job? = null

    val elapsedState: StateFlow<ElapsedData> = _elapsedState

    fun startElapsed(startTime: Double) {
        if (timerJob == null) {
            timerJob = timerScope.launch {
                initTimer(startTime.toInt())
                    .onCompletion {
                        _elapsedState.emit(ElapsedData())
                    }
                    .conflate()
                    .collect {
                        _elapsedState.emit(it)
                    }
            }
        }
    }

    fun cancelElapsed() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun initTimer(startTime: Int): Flow<ElapsedData> = flow {
        var timeElapsed = Instant.now().epochSecond.toInt() - startTime
        while (true) {
            emit(ElapsedData(startTime = startTime, timeElapsed = timeElapsed))
            timeElapsed = Instant.now().epochSecond.toInt() - startTime
            delay(1000)
        }
    }
}