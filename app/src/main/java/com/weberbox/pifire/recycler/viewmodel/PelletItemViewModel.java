package com.weberbox.pifire.recycler.viewmodel;

import androidx.annotation.NonNull;

public class PelletItemViewModel {

    private String mPelletItem;
    private String mPelletItemId;

    public PelletItemViewModel(@NonNull final String pelletItem, @NonNull final String pelletId) {
        setPelletItem(pelletItem);
        setPelletItemId(pelletId);
    }

    @NonNull
    public String getPelletItem() {
        return mPelletItem;
    }

    public void setPelletItem(@NonNull final String pelletItem) {
        this.mPelletItem = pelletItem;
    }

    @NonNull
    public String getPelletItemId() {
        return mPelletItemId;
    }

    public void setPelletItemId(@NonNull final String pelletItemId) {
        this.mPelletItemId = pelletItemId;
    }

}
