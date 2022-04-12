package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

public class SmartStartModel {

    private String range;
    private Integer startUp;
    private Integer augerOn;
    private Integer pMode;

    public SmartStartModel(@NonNull final String range, @NonNull final  Integer startUp,
                           @NonNull final Integer augerOn, @NonNull final Integer pMode) {
        setRange(range);
        setStartUp(startUp);
        setAugerOn(augerOn);
        setPMode(pMode);
    }

    @NonNull
    public String getRange() {
        return range;
    }

    public void setRange(@NonNull final String range) {
        this.range = range;
    }

    @NonNull
    public Integer getStartUp() {
        return startUp;
    }

    public void setStartUp(@NonNull final Integer startUp) {
        this.startUp = startUp;
    }

    @NonNull
    public Integer getPMode() {
        return pMode;
    }

    public void setPMode(@NonNull final Integer pMode) {
        this.pMode = pMode;
    }

    @NonNull
    public Integer getAugerOn() {
        return augerOn;
    }

    public void setAugerOn(@NonNull final Integer augerOn) {
        this.augerOn = augerOn;
    }

}
