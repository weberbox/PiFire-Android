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

    public void setPMode(@NonNull final String pMode) {
        mPMode = pMode;
    }

    @NonNull
    public String getAugerOn() {
        return mAugerOn;
    }

    public void setAugerOn(@NonNull final String augerOn) {
        mAugerOn = augerOn;
    }

    @NonNull
    public String getAugerOff() {
        return mAugerOff;
    }

    public void setAugerOff(@NonNull final String augerOff) {
        mAugerOff = augerOff;
    }
}
