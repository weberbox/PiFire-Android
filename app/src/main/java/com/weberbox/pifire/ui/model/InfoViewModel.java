package com.weberbox.pifire.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoViewModel extends ViewModel {
    public static final String TAG = InfoViewModel.class.getSimpleName();

    private MutableLiveData<String> mInfoData;

    public InfoViewModel () {
        mInfoData = new MutableLiveData<>();
    }

    public LiveData<String> getInfoData() {
        return mInfoData;
    }

    public void setInfoData(String infoData) {
        mInfoData.postValue(infoData);
    }
}
