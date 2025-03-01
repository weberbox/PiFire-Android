package com.weberbox.pifire.utils.log;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class DebugLogTree extends Timber.DebugTree {

    private final String prefix;

    public DebugLogTree(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        // Workaround for devices that do not show lower priority logs
        if (Build.MANUFACTURER.equals("HUAWEI") || Build.MANUFACTURER.equals("samsung")) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
                priority = Log.ERROR;
        }
        super.log(priority, prefix + "." + tag, message, t);
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("(%s:%s) - %s",
                element.getFileName(),
                element.getLineNumber(),
                element.getMethodName());
    }
}
