package com.weberbox.pifire.model.local;

public class DataModel {

    private final String liveData;
    private final boolean newData;

    public DataModel(String liveData, boolean newData) {
        this.liveData = liveData;
        this.newData = newData;
    }

    public String getLiveData() {
        return liveData;
    }

    public boolean getIsNewData() {
        return newData;
    }

}
