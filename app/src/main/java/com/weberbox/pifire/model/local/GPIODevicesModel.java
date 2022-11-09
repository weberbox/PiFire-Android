package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class GPIODevicesModel {

    private String name;
    private String function;
    private String pin;

    public GPIODevicesModel(@NonNull final String name, @NonNull final String function,
                            @NonNull final String pin) {
        setName(name);
        setFunction(function);
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
    public String getFunction() {
        return function;
    }

    public void setFunction(@NonNull final String function) {
        this.function = function;
    }

    @NonNull
    public String getPin() {
        return pin;
    }

    public void setPin(@NonNull final String pin) {
        this.pin = pin;
    }

}
