package com.weberbox.pifire.recycler.viewmodel;

import androidx.annotation.NonNull;

public class TimePickerViewModel {

    private String mTimeText;

    public TimePickerViewModel(@NonNull final String timeText) {
        seTimeText(timeText);
    }

    @NonNull
    public String getTimeText() {
        return mTimeText;
    }

    public void seTimeText(@NonNull final String timeText) {
        this.mTimeText = timeText;
    }
}
