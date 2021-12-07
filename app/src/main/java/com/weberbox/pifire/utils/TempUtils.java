package com.weberbox.pifire.utils;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;

public class TempUtils {

    private final Context mContext;

    public TempUtils(Context context) {
        mContext = context;

    }

    public static String getTempUnit(Context context) {
        return Prefs.getString(context.getString(R.string.prefs_grill_units), "F");
    }

    public int getDefaultGrillTemp() {
        return isCelsius(mContext) ? AppConfig.DEFAULT_GRILL_TEMP_SET_C :
                AppConfig.DEFAULT_GRILL_TEMP_SET_F;
    }

    public int getMinGrillTemp() {
        return isCelsius(mContext) ? AppConfig.MIN_GRILL_TEMP_SET_C :
                AppConfig.MIN_GRILL_TEMP_SET_F;
    }

    public int getMaxGrillTemp() {
        return isCelsius(mContext) ? AppConfig.MAX_GRILL_TEMP_SET_C :
                AppConfig.MAX_GRILL_TEMP_SET_F;
    }

    public int getDefaultProbeTemp() {
        return isCelsius(mContext) ? AppConfig.DEFAULT_PROBE_TEMP_SET_C :
                AppConfig.DEFAULT_PROBE_TEMP_SET_F;
    }

    public int getMinProbeTemp() {
        return isCelsius(mContext) ? AppConfig.MIN_PROBE_TEMP_SET_C :
                AppConfig.MIN_PROBE_TEMP_SET_F;
    }

    public int getMaxProbeTemp() {
        return isCelsius(mContext) ? AppConfig.MAX_PROBE_TEMP_SET_C :
                AppConfig.MAX_PROBE_TEMP_SET_F;
    }

    private boolean isCelsius(Context context) {
        return Prefs.getString(context.getString(R.string.prefs_grill_units), "F")
                .equalsIgnoreCase("C");
    }
}
