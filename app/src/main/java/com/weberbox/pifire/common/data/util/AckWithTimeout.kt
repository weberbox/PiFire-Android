package com.weberbox.pifire.common.data.util

import io.socket.client.Ack
import java.util.Timer
import java.util.TimerTask

class AckWithTimeout(
    private var onSuccess: (args: Array<out Any?>) -> Unit,
    private var onTimeout: () -> Unit,
    private val timeoutInMillis: Long = 10000
) : Ack {
    private var called = false

    private val timer: Timer = Timer().apply {
        schedule(object : TimerTask() {
            override fun run() {
                if (called) return
                called = true

                onTimeout()
            }
        }, timeoutInMillis)
    }

    override fun call(vararg args: Any) {
        if (called) return
        called = true
        timer.cancel()

        onSuccess(args)
    }
}