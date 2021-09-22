package com.weberbox.pifire.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public static final String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<String> mDashData;
    private MutableLiveData<String> mEventsData;
    private MutableLiveData<String> mHistoryData;
    private MutableLiveData<String> mPelletData;
    private MutableLiveData<Boolean> mServerConnected;

    public MainViewModel () {
        mServerConnected = new MutableLiveData<>();
        mDashData = new MutableLiveData<>();
        mEventsData = new MutableLiveData<>();
        mHistoryData = new MutableLiveData<>();
        mPelletData = new MutableLiveData<>();
    }

    public LiveData<String> getDashData() {
        return mDashData;
    }

    public void setDashData(String dashData) {
        mDashData.postValue(dashData);
    }

    public LiveData<String> getEventsData() {
        return mEventsData;
    }

    public void setEventsData(String eventsData) {
        mEventsData.postValue(eventsData);
    }

    public LiveData<String> getHistoryData() {
        return mHistoryData;
    }

    public void setHistoryData(String historyData) {
        mHistoryData.postValue(historyData);
    }

    public LiveData<String> getPelletData() {
        return mPelletData;
    }

    public void setPelletData(String pelletData) {
        mPelletData.postValue(pelletData);
    }

    public LiveData<Boolean> getServerConnected() {
        return mServerConnected;
    }

    public void setServerConnected(Boolean enabled) {
        mServerConnected.postValue(enabled);
    }

}
