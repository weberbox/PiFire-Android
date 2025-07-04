package com.weberbox.pifire.common.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CoroutinePoller<T>(
    private val dispatcher: CoroutineDispatcher,
    private val fetchData: suspend () -> T
) {
    var job: Job? = null

    fun poll(delay: Long) = callbackFlow<T> {
        job = launch(dispatcher) {
            while (isActive) {
                val data = fetchData()
                trySend(data)
                delay(delay)
            }
        }
        awaitClose {
            cancel()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}