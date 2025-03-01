package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

public record GPIODevicesRecord(String name, String function, String pin) {

    public GPIODevicesRecord(@NonNull final String name, @NonNull final String function,
                             @NonNull final String pin) {
        this.name = name;
        this.function = function;
        this.pin = pin;
    }

    @Override
    @NonNull
    public String name() {
        return name;
    }

    @Override
    @NonNull
    public String function() {
        return function;
    }

    @Override
    @NonNull
    public String pin() {
        return pin;
    }

}
