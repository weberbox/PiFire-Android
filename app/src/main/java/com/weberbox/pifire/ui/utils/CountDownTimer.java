package com.weberbox.pifire.ui.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.weberbox.pifire.interfaces.TimerTickListener;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public abstract class CountDownTimer implements TimerTickListener {

    private static final int INTERVAL = 1000;
    private static final int MSG = 1;

    private boolean mIsRunning = false;
    private boolean mIsPaused = false;
    private boolean mIsActive = false;

    private long mStartTime = 0;
    private long mEndTime = 0;
    private long mPauseTime = 0;
    private long mTime;
    private long mLocalTime;
    private long mInterval;
    private Handler mHandler;

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

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG) {
                    if (mLocalTime <= mTime) {
                        onDuration(getDuration(mEndTime - mStartTime));
                        onTimerTick(getCurrentSeconds(mTime - mLocalTime));
                        onRemainingTime(formatTimeRemaining(mTime - mLocalTime));
                        mLocalTime += mInterval;
                        sendMessageDelayed(mHandler.obtainMessage(MSG), mInterval);
                    } else stopTimer();
                }
            }
        };
    }

    public void startTimer() {
        if (mIsRunning) {
            return;
        }

        Timber.d("Starting Timer");
        mIsRunning = true;
        mIsActive = true;
        mIsPaused = false;
        mLocalTime = 0;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    public void stopTimer() {
        Timber.d("Stopping Timer");
        mIsRunning = false;
        mIsActive = false;
        mHandler.removeMessages(MSG);
        onFinished();
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public synchronized boolean isPaused() {
        return mIsPaused;
    }

    public void pauseTimer() {
        Timber.d("Pausing Timer");
        mIsPaused = true;
        mIsRunning = false;
        mHandler.removeMessages(MSG);
    }

    public void resumeTimer() {
        Timber.d("Resuming Timer");
        if (!mIsRunning) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
        }
        mIsPaused = false;
    }

    public void startTimer(long startSeconds, long endSeconds, long pauseSeconds) {
        long startTime = TimeUnit.SECONDS.toMillis(startSeconds);
        long endTime = TimeUnit.SECONDS.toMillis(endSeconds);
        long pauseTime = TimeUnit.SECONDS.toMillis(pauseSeconds);

        if (mIsRunning) {
            if (mStartTime != startTime && mEndTime != endTime) {
                mIsRunning = false;
                mHandler.removeMessages(MSG);
                startTimer(startSeconds, endSeconds, pauseSeconds);
            }
            return;
        }

        Timber.d("Start Time %d", startTime);
        Timber.d("End Time %d", endTime);
        Timber.d("Pause Time %d", pauseTime);

        if (endTime > System.currentTimeMillis()) {
            long duration = endTime - System.currentTimeMillis();

            if (this.mTime <= 0) {
                if (duration < 0) {
                    duration *= -1;
                }
            }
            this.mTime = duration;
            this.mStartTime = startTime;
            this.mEndTime = endTime;
            this.mPauseTime = pauseTime;

            startTimer();

        }
    }

    public void setTime(long timeInMillis) {
        if (mIsRunning) {
            return;
        }

        if (this.mTime <= 0) {
            if (timeInMillis < 0) {
                timeInMillis *= -1;
            }
        }
        this.mTime = timeInMillis;
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

    public int getDuration(long timeInMillis) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(timeInMillis);
    }

    public void setInterval(long intervalInMillis) {
        if (mIsRunning) {
            return;
        }

        if (this.mInterval <= 0) {
            if (intervalInMillis < 0) {
                intervalInMillis *= -1;
            }
        }
        this.mInterval = intervalInMillis;
    }
}
