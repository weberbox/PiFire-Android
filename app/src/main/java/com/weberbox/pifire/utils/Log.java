package com.weberbox.pifire.utils;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;

public class Log {

    public static void e(String tag, String message) {
        if (Prefs.getBoolean(PiFireApplication.getRes().getString(R.string.prefs_debug_logging), false))
            android.util.Log.e(tag, message);
    }

    public static void d(String tag, String message) {
        if (Prefs.getBoolean(PiFireApplication.getRes().getString(R.string.prefs_debug_logging), false))
            android.util.Log.d(tag, message);
    }

    public static void i(String tag, String message) {
        if (Prefs.getBoolean(PiFireApplication.getRes().getString(R.string.prefs_debug_logging), false))
            android.util.Log.i(tag, message);
    }

    public static void w(String tag, String message) {
        if (Prefs.getBoolean(PiFireApplication.getRes().getString(R.string.prefs_debug_logging), false))
            android.util.Log.w(tag, message);
    }

    public static void w(String tag, String message, Exception e) {
        if (Prefs.getBoolean(PiFireApplication.getRes().getString(R.string.prefs_debug_logging), false))
            android.util.Log.w(tag, message, e);
    }

}
