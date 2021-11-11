package com.weberbox.pifire.utils;

import com.weberbox.pifire.updater.objects.Version;

import timber.log.Timber;

public class VersionUtils {

    public static Boolean isFeatureSupported(String serverVersion, String requiredVersion) {
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
