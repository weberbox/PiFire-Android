package com.weberbox.pifire.recycler.viewmodel;

import androidx.annotation.NonNull;

public class TempPickerViewModel {

    private String tempText;
    private String unitText;

    public TempPickerViewModel(@NonNull final String tempText, final String unitText) {
        setTempText(tempText);
        setUnitText(unitText);
    }

    @NonNull
    public String getTempText() {
        return tempText;
    }

    public void setTempText(@NonNull final String tempText) {
        this.tempText = tempText;
    }

    @NonNull
    public String getUnitText() {
        return unitText;
    }

    public void setUnitText(@NonNull final String unitText) {
        this.unitText = unitText;
    }
}
