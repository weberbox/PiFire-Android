package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

public record GPIOInOutRecord(String name, String pin) {

    public GPIOInOutRecord(@NonNull final String name, @NonNull final String pin) {
        this.name = name;
        this.pin = pin;
    }

    @Override
    @NonNull
    public String name() {
        return name;
    }

    @Override
    @NonNull
    public String pin() {
        return pin;
    }

}