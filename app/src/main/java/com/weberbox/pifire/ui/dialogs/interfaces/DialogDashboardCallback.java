package com.weberbox.pifire.ui.dialogs.interfaces;

import com.weberbox.pifire.model.local.DashProbeModel.DashProbe;
import com.weberbox.pifire.model.local.ProbeOptionsModel;

public interface DialogDashboardCallback {
    void onTempConfirmClicked(DashProbe probe, ProbeOptionsModel probeOptionsModel, String temp, boolean hold);
    void onTempClearClicked(DashProbe probe);
    void onTimerConfirmClicked(String hours, String minutes, boolean shutdown, boolean keepWarm);
}
