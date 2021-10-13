package com.weberbox.pifire.recycler.viewmodel;

import androidx.annotation.NonNull;

public class PModeViewModel {
    private String mPMode;
    private String mAugerOn;
    private String mAugerOff;

    public PModeViewModel(@NonNull final String pMode, @NonNull final  String augerOn, @NonNull final String augerOff) {
        mPMode = pMode;
        mAugerOn = augerOn;
        mAugerOff = augerOff;
    }

    @NonNull
    public String getPMode() {
        return mPMode;
    }

    @SuppressWarnings("unused")
    public void setPMode(@NonNull final String pMode) {
        this.mPMode = pMode;
    }

    @NonNull
    public String getAugerOn() {
        return mAugerOn;
    }

    @SuppressWarnings("unused")
    public void setAugerOn(@NonNull final String augerOn) {
        this.mAugerOn = augerOn;
    }

    @NonNull
    public String getAugerOff() {
        return mAugerOff;
    }

    @SuppressWarnings("unused")
    public void setAugerOff(@NonNull final String augerOff) {
        this.mAugerOff = augerOff;
    }
}
