package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class GPIOInOutModel {

    private String name;
    private String pin;

    public GPIOInOutModel(@NonNull final String name, @NonNull final String pin) {
        setName(name);
        setPin(pin);
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull final String name) {
        this.name = name;
    }

    @NonNull
    public String getPin() {
        return pin;
    }

    public void setPin(@NonNull final String pin) {
        this.pin = pin;
    }

}