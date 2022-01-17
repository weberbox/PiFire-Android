package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

public class TimePickerModel {

    private String timeText;

    public TimePickerModel(@NonNull final String timeText) {
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
