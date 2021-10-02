package com.weberbox.pifire.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoViewModel extends ViewModel {

    private MutableLiveData<DataModel> mInfoData;

    public InfoViewModel () {
        mInfoData = new MutableLiveData<>();
    }

    public LiveData<DataModel> getInfoData() {
        return mInfoData;
    }

    public void setInfoData(String infoData, boolean newData) {
        mInfoData.postValue(new DataModel(infoData, newData));
    }
}
