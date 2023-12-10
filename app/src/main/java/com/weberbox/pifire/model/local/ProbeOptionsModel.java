package com.weberbox.pifire.model.local;

public class ProbeOptionsModel {

    private boolean shutdown;
    private boolean keepWarm;

    public ProbeOptionsModel(final boolean shutdown, final boolean keepWarm) {
        setShutdown(shutdown);
        setKeepWarm(keepWarm);
    }

    public boolean getShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public boolean getKeepWarm() {
        return keepWarm;
    }

    public void setKeepWarm(boolean keepWarm) {
        this.keepWarm = keepWarm;
    }
}
