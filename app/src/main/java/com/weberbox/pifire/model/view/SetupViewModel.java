package com.weberbox.pifire.model.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.weberbox.pifire.model.SingleLiveEvent;

public class SetupViewModel extends ViewModel {

    private final MutableLiveData<String> QRData;
    private final SingleLiveEvent<Void> fabClickEvent;

    public SetupViewModel() {
        QRData = new MutableLiveData<>();
        fabClickEvent = new SingleLiveEvent<>();
    }

    public LiveData<String> getQRData() {
        return QRData;
    }

    public void setQRData(String qrData) {
        QRData.setValue(qrData);
    }

    public void fabOnClick() {
        fabClickEvent.call();
    }

    public SingleLiveEvent<Void> getFabEvent() {
        return fabClickEvent;
    }
}
