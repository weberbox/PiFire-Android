package com.weberbox.pifire.utils;

import android.app.Activity;
import android.content.Context;

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

    public static void provideUserConsent(boolean accepted) {
        Timber.d("Non OneSignal Build");
    }

    public static void registerDevice(Context context, Socket socket, int registrationResult) {
        Timber.d("Non OneSignal Build");
    }

    public static int checkRegistration(Context context) {
        return Constants.ONESIGNAL_REGISTERED;
    }

    public static void checkOneSignalStatus(Activity activity, Socket socket) {
        Timber.d("Non OneSignal Build");
    }

}