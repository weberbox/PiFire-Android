package com.weberbox.pifire.interfaces;

import com.weberbox.pifire.enums.ServerSupport;

public interface ServerInfoCallback {
    void onServerInfo(ServerSupport result, String version, String build);
}
