package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

public record PelletItemRecord(String pelletItem, String pelletType) {

    public PelletItemRecord(@NonNull final String pelletItem, @NonNull final String pelletType) {
        this.pelletItem = pelletItem;
        this.pelletType = pelletType;
    }

    @Override
    @NonNull
    public String pelletItem() {
        return pelletItem;
    }

    @Override
    @NonNull
    public String pelletType() {
        return pelletType;
    }

}
