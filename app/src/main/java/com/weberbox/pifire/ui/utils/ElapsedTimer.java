package com.weberbox.pifire.ui.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.weberbox.pifire.interfaces.ElapsedListener;

import java.util.Date;

import timber.log.Timber;


public abstract class ElapsedTimer implements ElapsedListener {

    private static final int INTERVAL = 1000;
    private static final int MSG = 2;
    private boolean isRunning = false;
    private Double startTime = 0.0;
    private Handler handler;

    public ElapsedTimer() {
        initElapsedTimer();
    }

    private void initElapsedTimer() {
        Timber.d("initElapsedTimer");

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG) {
                    onElapsedUpdate(timeElapsed(startTime));
                    sendMessageDelayed(handler.obtainMessage(MSG), INTERVAL);
                }
            }
        };
    }

    private void startElapsed() {
        if (isRunning) {
            return;
        }

        Timber.d("Starting Elapsed");
        isRunning = true;
        handler.sendMessage(handler.obtainMessage(MSG));
    }

    public void stopElapsed() {
        if (isRunning) {
            Timber.d("Stopping Elapsed");
            isRunning = false;
            handler.removeMessages(MSG);
        }
    }

    public void startElapsed(Double timeStamp) {
        if (isRunning) {
            return;
        }

        Double currentTime = Math.floor((double) new Date().getTime() / 1000);

        if (timeStamp < currentTime) {
            this.startTime = timeStamp;
            startElapsed();
        }
    }

    public static String timeElapsed(Double timeStamp) {
        Double currentTime = Math.floor((double) new Date().getTime() / 1000);
        Double timeElapsed = Math.floor(timeStamp);
        return formatDuration(currentTime - timeElapsed);
    }

    @SuppressLint("DefaultLocale")
    private static String formatDuration(Double totalSeconds) {
        int hours = (int) Math.floor(totalSeconds / 3600);
        int minutes = (int) Math.floor((totalSeconds % 3600) / 60);
        int seconds = (int) (totalSeconds % 60);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
