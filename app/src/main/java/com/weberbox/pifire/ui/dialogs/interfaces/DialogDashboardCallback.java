package com.weberbox.pifire.ui.dialogs.interfaces;

import com.weberbox.pifire.model.remote.DashDataModel.NotifyData;

import java.util.ArrayList;

public interface DialogDashboardCallback {
    void onTempConfirmClicked(ArrayList<NotifyData> notifyData, String temp, boolean hold);
    void onTempClearClicked(ArrayList<NotifyData> notifyData);
    void onTimerConfirmClicked(String hours, String minutes, boolean shutdown, boolean keepWarm);
}
