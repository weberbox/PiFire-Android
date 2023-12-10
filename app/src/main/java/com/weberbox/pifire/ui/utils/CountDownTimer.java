package com.weberbox.pifire.ui.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.weberbox.pifire.interfaces.TimerTickListener;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public abstract class CountDownTimer implements TimerTickListener {

    private static final int INTERVAL = 1000;
    private static final int MSG = 1;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private boolean isActive = false;

    private long startTime = 0;
    private long endTime = 0;
    private long pauseTime = 0;
    private long time;
    private long localTime;
    private long interval;
    private Handler handler;

    public CountDownTimer() {
        init(0, INTERVAL);
    }

    public CountDownTimer(long timeInMillis) {
        init(timeInMillis, INTERVAL);
    }

    public CountDownTimer(long timeInMillis, long intervalInMillis) {
        init(timeInMillis, intervalInMillis);
    }

    private void init(long time, long interval) {
        setTime(time);
        setInterval(interval);
        initCountDownTimer();
    }

    private void initCountDownTimer() {
        Timber.d("initCountDownTimer");

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG) {
                    if (localTime <= time) {
                        onDuration(getDuration(endTime - startTime));
                        onTimerTick(getCurrentSeconds(time - localTime));
                        onRemainingTime(formatTimeRemaining(time - localTime));
                        localTime += interval;
                        sendMessageDelayed(handler.obtainMessage(MSG), interval);
                    } else stopTimer();
                }
            }
        };
    }

    private void startTimer() {
        if (isRunning) {
            return;
        }

        Timber.d("Starting Timer");
        isRunning = true;
        isActive = true;
        isPaused = false;
        localTime = 0;
        handler.sendMessage(handler.obtainMessage(MSG));
    }

    public void stopTimer() {
        if (!isActive) {
            return;
        }

        Timber.d("Stopping Timer");
        isRunning = false;
        isActive = false;
        isPaused = false;
        handler.removeMessages(MSG);
        onFinished();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isActive() {
        return isActive;
    }

    public synchronized boolean isPaused() {
        return isPaused;
    }

    public void pauseTimer() {
        if (!isRunning | isPaused) {
            return;
        }

        Timber.d("Pausing Timer");
        isPaused = true;
        isRunning = false;
        handler.removeMessages(MSG);
    }

    public void resumeTimer() {
        if (isRunning | !isPaused) {
            return;
        }

        Timber.d("Resuming Timer");
        handler.sendMessage(handler.obtainMessage(MSG));
        isRunning = true;
        isPaused = false;
    }

    public void startTimer(long startSeconds, long endSeconds, long pauseSeconds) {
        long startTime = TimeUnit.SECONDS.toMillis(startSeconds);
        long endTime = TimeUnit.SECONDS.toMillis(endSeconds);
        long pauseTime = TimeUnit.SECONDS.toMillis(pauseSeconds);

        if (isRunning) {
            if (this.endTime != endTime) {
                isRunning = false;
                handler.removeMessages(MSG);
                startTimer(startSeconds, endSeconds, pauseSeconds);
            }
            return;
        }

        if (isPaused) {
            return;
        }

        Timber.d("Start Time %s", String.valueOf(startSeconds));
        Timber.d("End Time %s", String.valueOf(endTime));
        Timber.d("Pause Time %s", String.valueOf(pauseTime));

        if (endTime > System.currentTimeMillis() || pauseTime > 0) {
            long duration = endTime - System.currentTimeMillis();

            if (this.time <= 0) {
                if (duration < 0) {
                    duration *= -1;
                }
            }
            this.time = duration;
            this.startTime = startTime;
            this.endTime = endTime;
            this.pauseTime = pauseTime;

            startTimer();

        }
    }

    public void setTime(long timeInMillis) {
        if (isRunning) {
            return;
        }

        if (this.time <= 0) {
            if (timeInMillis < 0) {
                timeInMillis *= -1;
            }
        }
        this.time = timeInMillis;
    }

    private int getCurrentSeconds(long millisUntilFinished) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
    }

    @SuppressLint("DefaultLocale")
    public String formatTimeRemaining(long millisUntilFinished) {
        if (millisUntilFinished > 3600000) {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60);
        } else {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
        }
    }

    private int getDuration(long timeInMillis) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(timeInMillis);
    }

    private void setInterval(long intervalInMillis) {
        if (isRunning) {
            return;
        }

        if (this.interval <= 0) {
            if (intervalInMillis < 0) {
                intervalInMillis *= -1;
            }
        }
        this.interval = intervalInMillis;
    }
}
