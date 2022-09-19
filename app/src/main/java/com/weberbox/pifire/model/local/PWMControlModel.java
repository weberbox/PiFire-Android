package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

public class PWMControlModel {

    private Integer temp;
    private Integer dutyCycle;

    public PWMControlModel(@NonNull final Integer temp, @NonNull final Integer dutyCycle) {
        setTemp(temp);
        setDutyCycle(dutyCycle);
    }

    @NonNull
    public Integer getTemp() {
        return temp;
    }

    public void setTemp(@NonNull final Integer temp) {
        this.temp = temp;
    }

    @NonNull
    public Integer getDutyCycle() {
        return dutyCycle;
    }

    public void setDutyCycle(@NonNull final Integer dutyCycle) {
        this.dutyCycle = dutyCycle;
    }

}
