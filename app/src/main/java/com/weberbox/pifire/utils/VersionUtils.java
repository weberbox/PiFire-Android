package com.weberbox.pifire.utils;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.updater.objects.Version;

import timber.log.Timber;

public class VersionUtils {

    public static Boolean isSupported(String requiredVersion) {
        String serverVersion = Prefs.getString("prefs_server_version", "1.0.0");
        try {
            final Version storedVersion = new Version(serverVersion);
            final Version required = new Version(requiredVersion);
            return storedVersion.compareTo(required) >= 0;
        } catch (Exception e) {
            Timber.w(e, "Failed comparing versions");
            return false;
        }
    }
}
