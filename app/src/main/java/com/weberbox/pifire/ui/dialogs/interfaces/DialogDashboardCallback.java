package com.weberbox.pifire.ui.dialogs.interfaces;

public interface DialogDashboardCallback {
    void onTempConfirmClicked(int type, String temp, boolean hold, boolean shutdown, boolean keepWarm);
    void onTempClearClicked(int type);
    void onTimerConfirmClicked(String hours, String minutes, boolean shutdown, boolean keepWarm);
}
