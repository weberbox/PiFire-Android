package com.weberbox.pifire.ui.dialogs.interfaces;

import org.jetbrains.annotations.NotNull;

public interface DialogProbeCallback {
    void onProbeUpdated(int position, @NotNull String probeName, @NotNull String probeProfile);
}
