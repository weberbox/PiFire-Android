package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

public record TempPickerRecord(String tempText, String unitText) {

    public TempPickerRecord(@NonNull final String tempText, final String unitText) {
        this.tempText = tempText;
        this.unitText = unitText;
    }

    @Override
    @NonNull
    public String tempText() {
        return tempText;
    }

    @Override
    @NonNull
    public String unitText() {
        return unitText;
    }

}
