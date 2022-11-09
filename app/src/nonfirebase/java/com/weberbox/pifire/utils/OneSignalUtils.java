package com.weberbox.pifire.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationManagerCompat;

import com.weberbox.pifire.constants.Constants;

import io.socket.client.Socket;
import timber.log.Timber;

public class OneSignalUtils {

    public static void initOneSignal(Context context) {
        Timber.d("Non OneSignal Build");
    }

    public static void initNotificationChannels(Context context) {
        Timber.d("Non OneSignal Build");
    }

    public static void promptForPushNotifications() {
        Timber.d("Non OneSignal Build");
    }

    public static void provideUserConsent(boolean accepted) {
        Timber.d("Non OneSignal Build");
    }

    public static void registerDevice(Context context, Socket socket, int registrationResult) {
        Timber.d("Non OneSignal Build");
    }

    @SuppressWarnings("unused")
    public static boolean checkNotificationPermissionStatus(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 33 ||
                context.getApplicationInfo().targetSdkVersion < 33) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            return manager.areNotificationsEnabled();
        }

        return context.checkSelfPermission("android.permission.POST_NOTIFICATIONS") ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static int checkRegistration(Context context) {
        return Constants.ONESIGNAL_REGISTERED;
    }

    public static void checkOneSignalStatus(Activity activity, Socket socket) {
        Timber.d("Non OneSignal Build");
    }

}