package com.weberbox.pifire.interfaces;

public interface DashboardCallback {
    void onTempConfirmClicked(int type, String temp, boolean hold, boolean shutdown, boolean keepWarm);
    void onTempClearClicked(int type);
    void onTimerConfirmClicked(String hours, String minutes, boolean shutdown, boolean keepWarm);
}
