package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

public class PModeModel {
    private String pMode;
    private String augerOn;
    private String augerOff;

    public PModeModel(@NonNull final String pMode, @NonNull final  String augerOn,
                      @NonNull final String augerOff) {
        setPMode(pMode);
        setAugerOn(augerOn);
        setAugerOff(augerOff);
    }

    @NonNull
    public String getPMode() {
        return pMode;
    }

    public void setPMode(@NonNull final String pMode) {
        this.pMode = pMode;
    }

    @NonNull
    public String getAugerOn() {
        return augerOn;
    }

    public void setAugerOn(@NonNull final String augerOn) {
        this.augerOn = augerOn;
    }

    @NonNull
    public String getAugerOff() {
        return augerOff;
    }

    public void setAugerOff(@NonNull final String augerOff) {
        this.augerOff = augerOff;
    }
}
