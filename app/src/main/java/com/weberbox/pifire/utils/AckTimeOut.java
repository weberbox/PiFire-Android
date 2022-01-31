package com.weberbox.pifire.utils;

import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Ack;

@SuppressWarnings("unused")
public class AckTimeOut implements Ack {

    private Timer timer;
    private long timeOut = 0;
    private boolean called = false;

    public AckTimeOut() {
        this.timeOut = 5000;
        startTimer();
    }

    public AckTimeOut(long timeoutAfter) {
        if (timeoutAfter <= 0)
            return;
        this.timeOut = timeoutAfter;
        startTimer();
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callback("timeout");
            }
        }, timeOut);
    }

    public void resetTimer() {
        if (timer != null) {
            timer.cancel();
            startTimer();
        }
    }

    public void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }

    void callback(Object... args) {
        if (called) return;
        called = true;
        cancelTimer();
        call(args);
    }

    @Override
    public void call(Object... args) {

    }
}
