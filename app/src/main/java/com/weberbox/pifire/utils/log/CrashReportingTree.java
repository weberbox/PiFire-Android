package com.weberbox.pifire.utils.log;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

@SuppressLint("LogNotTimber")
public class CrashReportingTree extends Timber.Tree {
    public static String TAG = "PiFire";

    private static final int CALL_STACK_INDEX = 6;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    private static boolean logDebugEvents;

    public CrashReportingTree(String tag, boolean logDebug) {
        TAG = tag;
        logDebugEvents = logDebug;
    }

    @Nullable
    String createStackElementTag(@NonNull StackTraceElement element) {
        String tag = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1);
    }

    final String getTag() {

        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            return TAG + " unknown class";
        }
        return createStackElementTag(stackTrace[CALL_STACK_INDEX]);
    }


    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {

        switch (priority) {
            case Log.VERBOSE:
                break;
            case Log.DEBUG:
                if (logDebugEvents) {
                    Log.d(TAG, getTag() + " / " + message);
                }
                break;

            case Log.INFO:
                Log.i(TAG, message, t);
                break;

            case Log.WARN:
                Log.w(TAG, getTag() + " / " + message, t);
                break;

            case Log.ERROR:
            case Log.ASSERT:
                Log.e(TAG, getTag() + " / " + message, t);
                break;
        }
    }
}
