package com.weberbox.pifire.model.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SetupViewModel extends ViewModel {

    private final MutableLiveData<String> mQRData;
    private final MutableLiveData<FloatingActionButton> mSetupFab;

    public SetupViewModel() {
        mQRData = new MutableLiveData<>();
        mSetupFab = new MutableLiveData<>();
    }

    public void setFab(final FloatingActionButton fab) {
        mSetupFab.setValue(fab);
    }

    public LiveData<FloatingActionButton> getFab() {
        return mSetupFab;
    }

    public LiveData<String> getQRData() {
        return mQRData;
    }

    public void setQRData(String qrData) {
        mQRData.setValue(qrData);
    }
}
