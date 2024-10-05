package com.weberbox.pifire.config;

import com.weberbox.pifire.BuildConfig;

@SuppressWarnings({"ConstantConditions", "unused"})
public class AppConfig {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean IS_DEV_BUILD = BuildConfig.FLAVOR.equals("dev");
    public static final boolean IS_PLAY_BUILD = BuildConfig.FLAVOR.equals("playstore");

    // Force Setup Version
    public static final int FORCE_SETUP_VERSION = 1040000;

    // Pellet Warning
    public static final int LOW_PELLET_WARNING = 40;

    // Recycler limited view
    public static final int RECYCLER_LIMIT = 3;

    // Dash Recycler Portrait
    public static final int DASH_RECYCLER_PORT = 2;

    // Dash Recycler Landscape
    public static final int DASH_RECYCLER_LAND = 4;

    // Temp Selector Temps F
    public static final int MIN_GRILL_TEMP_SET_F = 125;
    public static final int MAX_GRILL_TEMP_SET_F = 600;
    public static final int DEFAULT_GRILL_TEMP_SET_F = 225;
    public static final int MIN_PROBE_TEMP_SET_F = 40;
    public static final int MAX_PROBE_TEMP_SET_F = 300;
    public static final int DEFAULT_PROBE_TEMP_SET_F = 135;

    // Temp Selector Temps C
    public static final int MIN_GRILL_TEMP_SET_C = 50;
    public static final int MAX_GRILL_TEMP_SET_C = 315;
    public static final int DEFAULT_GRILL_TEMP_SET_C = 100;
    public static final int MIN_PROBE_TEMP_SET_C = 4;
    public static final int MAX_PROBE_TEMP_SET_C = 150;
    public static final int DEFAULT_PROBE_TEMP_SET_C = 55;

    // Prime Amounts
    public static final int PRIME_MIN_GRAMS = 10;
    public static final int PRIME_MAX_GRAMS = 50;

    // In App Updates
    public static final String INAPP_FIREBASE_INFO = "play_update_info";
    public static final int PRIORITY_URGENT = 5;
    public static final int PRIORITY_HIGH = 4;
    public static final int PRIORITY_MEDIUM = 3;
    public static final int PRIORITY_LOW = 2;
    public static final int PRIORITY_MINIMAL = 1;
    public static final int PRIORITY_NONE = 0;

    // Server Version Support
    public static final String SERVER_SUPPORT_INFO = "supported_server_info";

}
