package com.weberbox.pifire.utils;

import android.content.Context;

public class FirebaseUtils {
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    public static void getFirebaseToken(Context context) {
        Log.d(TAG, "Non Firebase Build");
    }

    public static void toggleFirebaseSubscription(boolean subscribe) {
        Log.d(TAG, "Non Firebase Build");
    }

    public static void subscribeFirebase() {
        Log.d(TAG, "Non Firebase Build");
    }
}