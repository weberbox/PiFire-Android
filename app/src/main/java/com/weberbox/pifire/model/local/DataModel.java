package com.weberbox.pifire.model.local;

public class DataModel {

    private String liveData;
    private boolean newData = false;

    public DataModel(String liveData, boolean newData) {
        setLiveData(liveData);
        setIsNewData(newData);
    }

    public String getLiveData() {
        return liveData;
    }

    public void setLiveData(String liveData) {
        this.liveData = liveData;
    }

    public boolean getIsNewData() {
        return newData;
    }

    public void setIsNewData(final boolean newData) {
        this.newData = newData;
    }
}
