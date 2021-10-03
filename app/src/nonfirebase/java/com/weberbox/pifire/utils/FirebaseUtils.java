package com.weberbox.pifire.utils;

import android.content.Context;

import timber.log.Timber;

public class FirebaseUtils {
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    public static void getFirebaseToken(Context context) {
        Timber.d("Non Firebase Build");
    }

    public static void toggleFirebaseSubscription(boolean subscribe) {
        Timber.d("Non Firebase Build");
    }

    public static void subscribeFirebase() {
        Timber.d("Non Firebase Build");
    }
}