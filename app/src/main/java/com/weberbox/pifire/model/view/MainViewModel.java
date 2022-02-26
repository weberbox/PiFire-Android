package com.weberbox.pifire.model.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.discord.panels.PanelState;
import com.weberbox.pifire.model.local.DataModel;
import com.weberbox.pifire.model.remote.DashDataModel;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<DashDataModel> dashData;
    private final MutableLiveData<DataModel> eventsData;
    private final MutableLiveData<DataModel> historyData;
    private final MutableLiveData<DataModel> pelletData;
    private final MutableLiveData<DataModel> infoData;
    private final MutableLiveData<PanelState> startPanelState;
    private final MutableLiveData<PanelState> endPanelState;
    private final MutableLiveData<Boolean> serverConnected;

    public MainViewModel () {
        serverConnected = new MutableLiveData<>();
        dashData = new MutableLiveData<>();
        eventsData = new MutableLiveData<>();
        historyData = new MutableLiveData<>();
        pelletData = new MutableLiveData<>();
        infoData = new MutableLiveData<>();
        startPanelState = new MutableLiveData<>();
        endPanelState = new MutableLiveData<>();
    }

    public LiveData<DashDataModel> getDashData() {
        return dashData;
    }

    public void setDashData(DashDataModel dashData) {
        this.dashData.postValue(dashData);
    }

    public LiveData<DataModel> getEventsData() {
        return eventsData;
    }

    public void setEventsData(String eventsData, boolean newData) {
        this.eventsData.postValue(new DataModel(eventsData, newData));
    }

    public LiveData<DataModel> getHistoryData() {
        return historyData;
    }

    public void setHistoryData(String historyData, boolean newData) {
        this.historyData.postValue(new DataModel(historyData, newData));
    }

    public LiveData<DataModel> getPelletData() {
        return pelletData;
    }

    public void setPelletData(String pelletData, boolean newData) {
        this.pelletData.postValue(new DataModel(pelletData, newData));
    }

    public LiveData<Boolean> getServerConnected() {
        return serverConnected;
    }

    public void setServerConnected(Boolean connected) {
        serverConnected.postValue(connected);
    }

    public LiveData<DataModel> getInfoData() {
        return infoData;
    }

    public void setInfoData(String infoData, boolean newData) {
        this.infoData.postValue(new DataModel(infoData, newData));
    }

    public LiveData<PanelState> getStartPanelStateChange() {
        return startPanelState;
    }

    public void setStartPanelStateChange(PanelState state) {
        startPanelState.postValue(state);
    }

    public LiveData<PanelState> getEndPanelStateChange() {
        return endPanelState;
    }

    public void setEndPanelStateChange(PanelState state) {
        endPanelState.postValue(state);
    }

}
