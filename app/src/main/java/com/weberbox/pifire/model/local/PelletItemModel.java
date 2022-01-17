package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

public class PelletItemModel {

    private String pelletItem;
    private String pelletItemId;

    public PelletItemModel(@NonNull final String pelletItem, @NonNull final String pelletId) {
        setPelletItem(pelletItem);
        setPelletItemId(pelletId);
    }

    @NonNull
    public String getPelletItem() {
        return pelletItem;
    }

    public void setPelletItem(@NonNull final String pelletItem) {
        this.pelletItem = pelletItem;
    }

    @NonNull
    public String getPelletItemId() {
        return pelletItemId;
    }

    public void setPelletItemId(@NonNull final String pelletItemId) {
        this.pelletItemId = pelletItemId;
    }

}
