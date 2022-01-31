package com.weberbox.pifire.interfaces;

public interface DashboardCallback {
    void onTempConfirmClicked(int type, String temp, boolean hold, boolean shutdown);
    void onTempClearClicked(int type);
    void onTimerConfirmClicked(String hours, String minutes, Boolean shutdown);
}
