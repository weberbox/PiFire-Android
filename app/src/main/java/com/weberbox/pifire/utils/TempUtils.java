package com.weberbox.pifire.utils;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;

public class TempUtils {

    private final Context context;

    public TempUtils(Context context) {
        this.context = context;
    }

    public static String getTempUnit(Context context) {
        return Prefs.getString(context.getString(R.string.prefs_grill_units), "F");
    }

    public int getDefaultGrillTemp() {
        return isFahrenheit() ? AppConfig.DEFAULT_GRILL_TEMP_SET_F :
                AppConfig.DEFAULT_GRILL_TEMP_SET_C;
    }

    public int getMinGrillTemp() {
        return isFahrenheit() ? AppConfig.MIN_GRILL_TEMP_SET_F :
                AppConfig.MIN_GRILL_TEMP_SET_C;
    }

    public int getMaxGrillTemp() {
        return isFahrenheit() ? AppConfig.MAX_GRILL_TEMP_SET_F :
                AppConfig.MAX_GRILL_TEMP_SET_C;
    }

    public int getDefaultProbeTemp() {
        return isFahrenheit() ? AppConfig.DEFAULT_PROBE_TEMP_SET_F :
                AppConfig.DEFAULT_PROBE_TEMP_SET_C;
    }

    public int getMinProbeTemp() {
        return isFahrenheit() ? AppConfig.MIN_PROBE_TEMP_SET_F :
                AppConfig.MIN_PROBE_TEMP_SET_C;
    }

    public int getMaxProbeTemp() {
        return isFahrenheit() ? AppConfig.MAX_PROBE_TEMP_SET_F :
                AppConfig.MAX_PROBE_TEMP_SET_C;
    }

    public boolean isFahrenheit() {
        return Prefs.getString(context.getString(R.string.prefs_grill_units), "F")
                .equalsIgnoreCase("F");
    }

    public Integer cleanTempString(String temp) {
        return Integer.parseInt(temp.replaceAll(context.getString(R.string.regex_numbers), ""));
    }
}
