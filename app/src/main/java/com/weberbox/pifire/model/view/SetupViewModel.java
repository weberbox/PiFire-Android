package com.weberbox.pifire.model.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.weberbox.pifire.model.SingleLiveEvent;

public class SetupViewModel extends ViewModel {

    private final MutableLiveData<String> serverAddress;
    private final SingleLiveEvent<Void> fabClickEvent;

    public SetupViewModel() {
        serverAddress = new MutableLiveData<>();
        fabClickEvent = new SingleLiveEvent<>();
    }

    public LiveData<String> getAddress() {
        return serverAddress;
    }

    public void setAddress(String address) {
        serverAddress.setValue(address);
    }

    public void fabOnClick() {
        fabClickEvent.call();
    }

    public SingleLiveEvent<Void> getFabEvent() {
        return fabClickEvent;
    }
}
