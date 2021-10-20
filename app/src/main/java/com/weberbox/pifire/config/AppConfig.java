package com.weberbox.pifire.config;

import com.weberbox.pifire.BuildConfig;

@SuppressWarnings("ConstantConditions")
public class AppConfig {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean USE_FIREBASE = BuildConfig.IS_FIREBASE;
    public static final boolean IS_DEV_BUILD = BuildConfig.FLAVOR_type.equalsIgnoreCase("dev");
}
