package com.weberbox.pifire.utils;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.dialogs.FirebaseTokenDialog;

import timber.log.Timber;

public class FirebaseUtils {

    public static void initFirebase(Context context) {
        FirebaseApp.initializeApp(context);
    }

    public static void getFirebaseToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.w(task.getException(), "Fetching FCM registration token failed");
                        return;
                    }
                    String token = task.getResult();
                    FirebaseTokenDialog dialog = new FirebaseTokenDialog(context, token);
                    dialog.showDialog();
                });
    }

    public static void toggleFirebaseSubscription(boolean subscribe) {
        if (!subscribe) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(BuildConfig.DEBUG ?
                    Constants.FIREBASE_TOPIC_GRILL_DEBUG :
                    Constants.FIREBASE_TOPIC_GRILL)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Timber.w(task.getException(), "Firebase unsubscribe failed");
                            return;
                        }
                        Timber.d("Firebase unsubscribe successful");
                    });
        } else {
            subscribeFirebase();
        }
    }

    public static void subscribeFirebase() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.DEBUG ?
                Constants.FIREBASE_TOPIC_GRILL_DEBUG :
                Constants.FIREBASE_TOPIC_GRILL)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.w(task.getException(), "Firebase subscribe failed");
                        return;
                    }
                    Timber.d("Firebase subscribe successful");
                });
    }

    @TargetApi(26)
    public static void initNotificationChannels(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String groupId = context.getString(R.string.notification_group_grill);
            CharSequence groupName = context.getString(R.string.notification_group_grill_name);
            notificationManager.createNotificationChannelGroup(
                    new NotificationChannelGroup(groupId, groupName));

            String channelIdBase = context.getString(R.string.notification_channel_base);
            CharSequence nameBase = context.getString(R.string.notification_channel_base_name);
            String descriptionBase = context.getString(R.string.notification_desc_base);
            int importanceBase = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel baseAlerts = new NotificationChannel(channelIdBase, nameBase,
                    importanceBase);
            baseAlerts.setDescription(descriptionBase);
            baseAlerts.enableLights(true);
            baseAlerts.setShowBadge(true);
            baseAlerts.enableVibration(true);
            baseAlerts.setGroup(groupId);
            baseAlerts.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(baseAlerts);

            String channelIdTemp = context.getString(R.string.notification_channel_temp);
            CharSequence nameTemp = context.getString(R.string.notification_channel_temp_name);
            String descriptionTemp = context.getString(R.string.notification_desc_temp);
            final Uri tempAlertUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.temp_achieved);
            int importanceTemp = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel tempAlerts = new NotificationChannel(channelIdTemp, nameTemp,
                    importanceTemp);
            tempAlerts.setDescription(descriptionTemp);
            tempAlerts.enableLights(true);
            tempAlerts.setShowBadge(true);
            tempAlerts.enableVibration(true);
            tempAlerts.setGroup(groupId);
            tempAlerts.setSound(tempAlertUri, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            tempAlerts.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(tempAlerts);

            String channelIdTimer = context.getString(R.string.notification_channel_timer);
            CharSequence nameTimer = context.getString(R.string.notification_channel_timer_name);
            String descriptionTimer = context.getString(R.string.notification_desc_timer);
            final Uri timerAlertUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.timer_alarm);
            int importanceTimer = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel timerAlerts = new NotificationChannel(channelIdTimer, nameTimer,
                    importanceTimer);
            timerAlerts.setDescription(descriptionTimer);
            timerAlerts.enableLights(true);
            timerAlerts.setShowBadge(true);
            timerAlerts.enableVibration(true);
            timerAlerts.setGroup(groupId);
            timerAlerts.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            timerAlerts.setSound(timerAlertUri, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            timerAlerts.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(timerAlerts);

            String channelIdError = context.getString(R.string.notification_channel_error);
            CharSequence nameError = context.getString(R.string.notification_channel_error_name);
            String descriptionError = context.getString(R.string.notification_desc_error);
            final Uri errorAlertUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.grill_error);
            int importanceError = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel errorAlerts = new NotificationChannel(channelIdError, nameError,
                    importanceError);
            errorAlerts.setDescription(descriptionError);
            errorAlerts.enableLights(true);
            errorAlerts.setShowBadge(true);
            errorAlerts.enableVibration(true);
            errorAlerts.setGroup(groupId);
            errorAlerts.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            errorAlerts.setSound(errorAlertUri, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            errorAlerts.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(errorAlerts);

        }
    }
}
