package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

public class SmartStartModel {

    private Integer temp;
    private Integer startUp;
    private Integer augerOn;
    private Integer pMode;

    public SmartStartModel(@NonNull final Integer temp, @NonNull final  Integer startUp,
                           @NonNull final Integer augerOn, @NonNull final Integer pMode) {
        setTemp(temp);
        setStartUp(startUp);
        setAugerOn(augerOn);
        setPMode(pMode);
    }

    @NonNull
    public Integer getTemp() {
        return temp;
    }

    public void setTemp(@NonNull final Integer temp) {
        this.temp = temp;
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
