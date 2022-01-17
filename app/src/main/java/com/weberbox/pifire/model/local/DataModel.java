package com.weberbox.pifire.model.local;

public class DataModel {

    private String mLiveData;
    private boolean mNewData = false;

    public DataModel(String liveData, boolean newData) {
        setLiveData(liveData);
        setIsNewData(newData);
    }

    public String getLiveData() {
        return mLiveData;
    }

    public void setLiveData(String liveData) {
        this.mLiveData = liveData;
    }

    public boolean getIsNewData() {
        return mNewData;
    }

    public void setIsNewData(final boolean newData) {
        this.mNewData = newData;
    }
}
