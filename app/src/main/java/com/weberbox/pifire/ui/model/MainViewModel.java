package com.weberbox.pifire.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public static final String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<String> mDashData;
    private MutableLiveData<DataModel> mEventsData;
    private MutableLiveData<DataModel> mHistoryData;
    private MutableLiveData<DataModel> mPelletData;
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

    public LiveData<DataModel> getEventsData() {
        return mEventsData;
    }

    public void setEventsData(String eventsData, boolean newData) {
        mEventsData.postValue(new DataModel(eventsData, newData));
    }

    public LiveData<DataModel> getHistoryData() {
        return mHistoryData;
    }

    public void setHistoryData(String historyData, boolean newData) {
        mHistoryData.postValue(new DataModel(historyData, newData));
    }

    public LiveData<DataModel> getPelletData() {
        return mPelletData;
    }

    public void setPelletData(String pelletData, boolean newData) {
        mPelletData.postValue(new DataModel(pelletData, newData));
    }

    public LiveData<Boolean> getServerConnected() {
        return mServerConnected;
    }

    public void setServerConnected(Boolean enabled) {
        mServerConnected.postValue(enabled);
    }

}
