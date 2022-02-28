package com.weberbox.pifire.config;

import com.weberbox.pifire.BuildConfig;

@SuppressWarnings("ConstantConditions")
public class AppConfig {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean USE_ONESIGNAL = BuildConfig.IS_ONESIGNAL;
    public static final boolean IS_DEV_BUILD = BuildConfig.FLAVOR_type.equals("dev");
    public static final boolean IS_BETA = BuildConfig.IS_BETA;

    // Force Setup Version
    public static final int FORCE_SETUP_VERSION = 1040000;

    // Pellet Warning
    public static final int LOW_PELLET_WARNING = 40;

    // Recycler limited view
    public static final int RECYCLER_LIMIT = 3;

    // Temp Selector Temps F
    public static final int MIN_GRILL_TEMP_SET_F = 125;
    public static final int MAX_GRILL_TEMP_SET_F = 500;
    public static final int DEFAULT_GRILL_TEMP_SET_F = 225;
    public static final int MIN_PROBE_TEMP_SET_F = 40;
    public static final int MAX_PROBE_TEMP_SET_F = 300;
    public static final int DEFAULT_PROBE_TEMP_SET_F = 135;

    // Temp Selector Temps C
    public static final int MIN_GRILL_TEMP_SET_C = 50;
    public static final int MAX_GRILL_TEMP_SET_C = 260;
    public static final int DEFAULT_GRILL_TEMP_SET_C = 100;
    public static final int MIN_PROBE_TEMP_SET_C = 4;
    public static final int MAX_PROBE_TEMP_SET_C = 150;
    public static final int DEFAULT_PROBE_TEMP_SET_C = 55;
}
