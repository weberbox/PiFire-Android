package com.weberbox.pifire.interfaces;

import com.weberbox.pifire.model.local.DashProbeModel.DashProbe;

public interface DashProbeCallback {
    void onProbeClick(DashProbe probe);
    void onProbeLongClick(DashProbe probe);
}
