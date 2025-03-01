package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

public record PModeRecord(String pMode, String augerOn, String augerOff) {
    public PModeRecord(@NonNull final String pMode, @NonNull final String augerOn,
                       @NonNull final String augerOff) {
        this.pMode = pMode;
        this.augerOn = augerOn;
        this.augerOff = augerOff;
    }

    @Override
    @NonNull
    public String pMode() {
        return pMode;
    }

    @Override
    @NonNull
    public String augerOn() {
        return augerOn;
    }

    @Override
    @NonNull
    public String augerOff() {
        return augerOff;
    }

}
