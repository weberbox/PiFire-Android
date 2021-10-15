package com.weberbox.pifire.interfaces;

public interface TimerTickListener {
    void onDuration(int duration);
    void onRemainingTime(String timeRemaining);
    void onTimerTick(int secondsUntilFinished);
    void onFinished();
}
