package com.weberbox.pifire.recycler.viewmodel;

import androidx.annotation.NonNull;

public class TimePickerViewModel {

    private String timeText;

    public TimePickerViewModel(@NonNull final String timeText) {
        seTimeText(timeText);
    }

    @NonNull
    public String getTimeText() {
        return timeText;
    }

    public void seTimeText(@NonNull final String timeText) {
        this.timeText = timeText;
    }
}
