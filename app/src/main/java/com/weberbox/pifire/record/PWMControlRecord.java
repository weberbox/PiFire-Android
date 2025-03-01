package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

public record PWMControlRecord(Integer temp, Integer dutyCycle) {

    public PWMControlRecord(@NonNull final Integer temp, @NonNull final Integer dutyCycle) {
        this.temp = temp;
        this.dutyCycle = dutyCycle;
    }

    @Override
    @NonNull
    public Integer temp() {
        return temp;
    }

    @Override
    @NonNull
    public Integer dutyCycle() {
        return dutyCycle;
    }

}
