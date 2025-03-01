package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

public record SmartStartRecord(Integer temp, Integer startUp, Integer augerOn, Integer pMode) {

    public SmartStartRecord(@NonNull final Integer temp, @NonNull final Integer startUp,
                            @NonNull final Integer augerOn, @NonNull final Integer pMode) {
        this.temp = temp;
        this.startUp = startUp;
        this.augerOn = augerOn;
        this.pMode = pMode;
    }

    @Override
    @NonNull
    public Integer temp() {
        return temp;
    }

    @Override
    @NonNull
    public Integer startUp() {
        return startUp;
    }

    @Override
    @NonNull
    public Integer pMode() {
        return pMode;
    }

    @Override
    @NonNull
    public Integer augerOn() {
        return augerOn;
    }

}
