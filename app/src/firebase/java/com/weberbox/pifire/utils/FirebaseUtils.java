package com.weberbox.pifire.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.dialogs.FirebaseTokenDialog;

public class FirebaseUtils {
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    public static void disableCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
    }

    public static void getFirebaseToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed",
                                    task.getException());
                            return;
                        }
                        String token = task.getResult();
                        FirebaseTokenDialog dialog = new FirebaseTokenDialog(context, token);
                        dialog.showDialog();
                        Log.d(TAG, "Firebase Token: " + token);
                    }
                });
    }

    public static void toggleFirebaseSubscription(boolean subscribe) {
        if (!subscribe) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FIREBASE_TOPIC_GRILL)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, task.isSuccessful() ?
                                    "Firebase unsubscribe successful"
                                    : "Firebase unsubscribe failed " + task.getException());
                        }
                    });
        } else {
            subscribeFirebase();
        }
    }

    public static void subscribeFirebase() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_TOPIC_GRILL)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, task.isSuccessful() ?
                                "Firebase subscribe successful" :
                                "Firebase subscribe failed " + task.getException());
                    }
                });
    }
}
