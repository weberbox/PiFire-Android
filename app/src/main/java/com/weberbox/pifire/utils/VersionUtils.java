package com.weberbox.pifire.utils;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
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


    public static boolean checkFirstRun(Context context) {
        String prefsKey = context.getString(R.string.prefs_installed_version);
        int currentVersionCode = BuildConfig.VERSION_CODE;
        int savedVersionCode = Prefs.getInt(prefsKey, -1);
        String serverAddress = Prefs.getString(context.getString(R.string.prefs_server_address));
        boolean firstRun = false;

        if (savedVersionCode == -1) {
            firstRun = true;
        } else if (savedVersionCode < AppConfig.FORCE_SETUP_VERSION) {
            firstRun = true;
        } else if (serverAddress.isEmpty()) {
            firstRun = true;
        }

        Prefs.putInt(prefsKey, currentVersionCode);

        return firstRun;
    }
}
