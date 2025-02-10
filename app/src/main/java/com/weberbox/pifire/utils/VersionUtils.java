package com.weberbox.pifire.utils;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.enums.ServerSupport;
import com.weberbox.pifire.enums.VersionResults;
import com.weberbox.pifire.interfaces.ServerInfoCallback;
import com.weberbox.pifire.model.local.ServerInfo;
import com.weberbox.pifire.model.local.ServerInfo.VersionBuild;
import com.weberbox.pifire.utils.objects.Version;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class VersionUtils {

    private static final HashMap<String, Object> defaults = new HashMap<>();

    @SuppressWarnings("unused")
    public static boolean isSupported(String requiredVersion) {
        String serverVersion = Prefs.getString("prefs_server_version", "1.0.0");
        return isSupportedMinMax(serverVersion, requiredVersion, true);
    }

    public static boolean isSupportedBuild(String requiredVersion, String requiredBuild) {
        String serverVersion = Prefs.getString("prefs_server_version", "1.0.0");
        String serverBuild = Prefs.getString("prefs_server_build", "0");
        if (isSupportedMinMax(serverVersion, requiredVersion, true)) {
            return isSupportedMinMax(serverBuild, requiredBuild, true);
        }
        return false;
    }

    public static boolean checkFirstRun(Context context) {
        String prefsVersion = context.getString(R.string.prefs_installed_version);
        String prefsComplete = context.getString(R.string.prefs_setup_completed);
        boolean setupCompleted = Prefs.getBoolean(prefsComplete, false);
        int currentVersionCode = BuildConfig.VERSION_CODE;
        int savedVersionCode = Prefs.getInt(prefsVersion, -1);
        String serverAddress = Prefs.getString(context.getString(R.string.prefs_server_address));

        Prefs.putInt(prefsVersion, currentVersionCode);

        return !setupCompleted || savedVersionCode < AppConfig.FORCE_SETUP_VERSION ||
                serverAddress.isEmpty();
    }

    public static void checkSupportedServerVersion(ServerInfoCallback callback) {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(2000)
                .setMinimumFetchIntervalInSeconds(AppConfig.DEBUG ? 0 : 3600)
                .build());
        defaults.put(AppConfig.SERVER_SUPPORT_INFO, R.raw.server_info);
        firebaseRemoteConfig.setDefaultsAsync(defaults);

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String firebaseInfo = firebaseRemoteConfig.getString(AppConfig.SERVER_SUPPORT_INFO);
                ArrayList<ServerInfo> serverInfo = ServerInfo.parseJSON(firebaseInfo);
                for (ServerInfo info : serverInfo) {
                    if (info.getAppVersionCode() == BuildConfig.VERSION_CODE) {
                        VersionBuild minSupport = info.getMinServerInfo();
                        VersionBuild maxSupport = info.getMaxServerInfo();
                        if (checkMinVerBuild(minSupport)) {
                            if (checkMaxVerBuild(maxSupport)) {
                                callback.onServerInfo(ServerSupport.SUPPORTED, null, null);
                            } else {
                                callback.onServerInfo(ServerSupport.UNSUPPORTED_MAX,
                                        maxSupport.getVersion(), maxSupport.getBuild());
                            }
                        } else {
                            callback.onServerInfo(ServerSupport.UNSUPPORTED_MIN,
                                    minSupport.getVersion(), minSupport.getBuild());
                        }
                        return;
                    }
                }
                callback.onServerInfo(ServerSupport.UNTESTED, null, null);
            } else {
                callback.onServerInfo(ServerSupport.FAILED, null, null);
            }
        });
    }

    public static void getRawSupportedVersion(Activity activity, ServerInfoCallback callback) {
        ArrayList<ServerInfo> serverInfo = ServerInfo.parseJSON(
                FileUtils.getJSONString(activity, Constants.JSON_SERVER_INFO));
        if (serverInfo != null) {
            for (ServerInfo info : serverInfo) {
                if (info.getAppVersionCode() == BuildConfig.VERSION_CODE) {
                    VersionBuild minSupport = info.getMinServerInfo();
                    VersionBuild maxSupport = info.getMaxServerInfo();
                    if (checkMinVerBuild(minSupport)) {
                        if (checkMaxVerBuild(maxSupport)) {
                            callback.onServerInfo(ServerSupport.SUPPORTED, null, null);
                        } else {
                            callback.onServerInfo(ServerSupport.UNSUPPORTED_MAX,
                                    maxSupport.getVersion(), maxSupport.getBuild());
                        }
                    } else {
                        callback.onServerInfo(ServerSupport.UNSUPPORTED_MIN,
                                minSupport.getVersion(), minSupport.getBuild());
                    }
                }
            }
        } else {
            callback.onServerInfo(ServerSupport.FAILED, null, null);
        }
    }

    private static boolean checkMinVerBuild(VersionBuild versionBuild) {
        String minVersion = versionBuild.getVersion();
        String minBuild = versionBuild.getBuild();
        if (!minVersion.isBlank()) {
            String serverVersion = Prefs.getString("prefs_server_version", "1.0.0");
            switch (isSupportedResults(serverVersion, minVersion)) {
                case GREATER -> {
                    return true;
                }
                case LESS, ERROR -> {
                    return false;
                }
                case SAME -> {
                    if (!minBuild.isBlank()) {
                        return isSupportedBuildMin(minBuild);
                    } else {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private static boolean checkMaxVerBuild(VersionBuild versionBuild) {
        String maxVersion = versionBuild.getVersion();
        String maxBuild = versionBuild.getBuild();
        if (!maxVersion.isBlank()) {
            String serverVersion = Prefs.getString("prefs_server_version", "1.0.0");
            switch (isSupportedResults(serverVersion, maxVersion)) {
                case GREATER, ERROR -> {
                    return false;
                }
                case LESS -> {
                    return true;
                }
                case SAME -> {
                    if (!maxBuild.isBlank()) {
                        return isSupportedBuildMax(maxBuild);
                    } else {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private static VersionResults isSupportedResults(String currentVersion, String requiredVersion) {
        try {
            final Version storedVersion = new Version(currentVersion);
            final Version required = new Version(requiredVersion);
            if (storedVersion.compareTo(required) == 0) {
                return VersionResults.SAME;
            } else if (storedVersion.compareTo(required) > 0) {
                return VersionResults.GREATER;
            } else {
                return VersionResults.LESS;
            }
        } catch (Exception e) {
            Timber.w(e, "Failed comparing versions");
            return VersionResults.ERROR;
        }
    }

    private static boolean isSupportedBuildMax(String requiredVersion) {
        String serverBuild = Prefs.getString("prefs_server_build", "0");
        return isSupportedMinMax(serverBuild, requiredVersion, false);
    }

    private static boolean isSupportedBuildMin(String requiredVersion) {
        String serverBuild = Prefs.getString("prefs_server_build", "0");
        return isSupportedMinMax(serverBuild, requiredVersion, true);
    }

    private static boolean isSupportedMinMax(String currentVersion, String requiredVersion,
                                             boolean checkMin) {
        try {
            final Version storedVersion = new Version(currentVersion);
            final Version required = new Version(requiredVersion);
            if (checkMin) {
                return storedVersion.compareTo(required) >= 0;
            } else {
                return storedVersion.compareTo(required) <= 0;
            }
        } catch (Exception e) {
            Timber.w(e, "Failed comparing versions");
            return false;
        }
    }
}
