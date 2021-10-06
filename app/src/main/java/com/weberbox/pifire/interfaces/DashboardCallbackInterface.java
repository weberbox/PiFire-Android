package com.weberbox.pifire.interfaces;

public interface DashboardCallbackInterface {
    void onModeActionClicked(int mode);
    void onTempConfirmClicked(int type, String temp, boolean hold, boolean shutdown);
    void onTempClearClicked(int type);
    void onTimerActionClicked(int type);
    void onTimerConfirmClicked(String hours, String minutes);
}
