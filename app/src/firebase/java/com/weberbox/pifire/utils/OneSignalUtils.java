package com.weberbox.pifire.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.PushConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.OneSignalDeviceInfo;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;
import timber.log.Timber;

public class OneSignalUtils {

    public static void initOneSignal(Context context) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.ERROR, OneSignal.LOG_LEVEL.NONE);

        OneSignal.setRequiresUserPrivacyConsent(true);
        OneSignal.initWithContext(context);
        OneSignal.setAppId(PushConfig.ONESIGNAL_APP_ID);
    }

    public static void promptForPushNotifications() {
        OneSignal.promptForPushNotifications();
    }

    public static void provideUserConsent(boolean accepted) {
        OneSignal.provideUserConsent(accepted);
    }

    public static void registerDevice(Context context, Socket socket, int registrationResult) {
        if (OneSignal.userProvidedPrivacyConsent()) {
            if (OneSignal.getDeviceState() != null) {
                if (OneSignal.getDeviceState().isSubscribed()) {
                    String playerID = OneSignal.getDeviceState().getUserId();
                    if (playerID != null) {
                        if (registrationResult == Constants.ONESIGNAL_NOT_REGISTERED) {
                            ServerControl.setOneSignalAppID(socket, PushConfig.ONESIGNAL_APP_ID,
                                    OneSignalUtils::processPostResponse);
                        }
                        registerOneSignalDevice(context, socket, playerID,
                                getDevice(context, playerID));
                    }
                }
            }
        }
    }

    private static void registerOneSignalDevice(Context context, Socket socket, String playerID,
                                                OneSignalDeviceInfo deviceInfo) {
        if (socket.connected()) {
            Map<String, OneSignalDeviceInfo> device = new HashMap<>();
            device.put(playerID, deviceInfo);
            ServerControl.registerOneSignalDevice(context, socket, device);
        }
    }

    public static void checkOneSignalStatus(Activity activity, Socket socket) {
        int status = OneSignalUtils.checkRegistration(activity);
        switch (status) {
            case Constants.ONESIGNAL_NO_ID:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_id_error_title,
                        R.string.settings_onesignal_id_error_message, false);
                Timber.d("Device has No ID");
                break;
            case Constants.ONESIGNAL_NO_CONSENT:
                if (!Prefs.getBoolean(activity.getString(
                        R.string.prefs_onesignal_consent_dismiss))) {
                    AlertUtils.createOneSignalConsentAlert(activity,
                            R.string.settings_onesignal_alert_consent_title,
                            R.string.settings_onesignal_alert_consent_message);
                }
                Timber.d("OneSignal does not have consent");
                break;
            case Constants.ONESIGNAL_NOT_REGISTERED:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_register_title,
                        R.string.settings_onesignal_register_message, false);
                registerDevice(activity, socket, status);
                break;
            case Constants.ONESIGNAL_APP_UPDATED:
                Timber.d("App updated re-registering with PiFire");
                registerDevice(activity, socket, status);
                break;
            case Constants.ONESIGNAL_DEVICE_ERROR:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_error_title,
                        R.string.settings_onesignal_error_message, true);
                Timber.d("Device Error");
                break;
            case Constants.ONESIGNAL_NULL_TOKEN:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_token_title,
                        R.string.settings_onesignal_token_message, true);
                Timber.d("Device Token Error");
                break;
            case Constants.ONESIGNAL_REGISTERED:
                Timber.d("Device already registered with PiFire");
                break;
            case Constants.ONESIGNAL_NOT_SUBSCRIBED:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_subscribe_title,
                        R.string.settings_onesignal_subscribe_message, true);
                Timber.d("Device is not subscribed");
                break;
        }
    }

    public static int checkRegistration(Context context) {
        if (!OneSignal.userProvidedPrivacyConsent()) {
            return Constants.ONESIGNAL_NO_CONSENT;
        }

        OSDeviceState deviceState = OneSignal.getDeviceState();
        if (deviceState == null) {
            return Constants.ONESIGNAL_DEVICE_ERROR;
        } else if (deviceState.getPushToken() == null) {
            return Constants.ONESIGNAL_NULL_TOKEN;
        } else if (!deviceState.isSubscribed()) {
            return Constants.ONESIGNAL_NOT_SUBSCRIBED;
        }

        String playerID = deviceState.getUserId();
        if (playerID == null) {
            return Constants.ONESIGNAL_NO_ID;
        }

        Map<String, OneSignalDeviceInfo> devicesHash = getDevicesHash(context);

        if (devicesHash == null || !devicesHash.containsKey(playerID)) {
            return Constants.ONESIGNAL_NOT_REGISTERED;
        }

        if (devicesHash.containsKey(playerID)) {
            for (Map.Entry<String, OneSignalDeviceInfo> device : devicesHash.entrySet()) {
                if (device.getKey().equals(playerID) &&
                        !device.getValue().getAppVersion().equals(BuildConfig.VERSION_NAME)) {
                    return Constants.ONESIGNAL_APP_UPDATED;
                }
            }
        }

        return Constants.ONESIGNAL_REGISTERED;
    }

    private static OneSignalDeviceInfo getDevice(Context context, String playerID) {
        Map<String, OneSignalDeviceInfo> devicesHash = getDevicesHash(context);
        OneSignalDeviceInfo existingDevice = devicesHash.get(playerID);

        if (existingDevice != null) {
            existingDevice.setAppVersion(BuildConfig.VERSION_NAME);
            return existingDevice;
        } else {
            OneSignalDeviceInfo newDevice = new OneSignalDeviceInfo();
            newDevice.setDeviceName(android.os.Build.MODEL);
            newDevice.setFriendlyName("");
            newDevice.setAppVersion(BuildConfig.VERSION_NAME);
            return newDevice;
        }
    }

    private static Map<String, OneSignalDeviceInfo> getDevicesHash(Context context) {
        String jsonDevices = Prefs.getString(context.getString(
                R.string.prefs_notif_onesignal_device_list));
        TypeToken<Map<String, OneSignalDeviceInfo>> token = new TypeToken<>() {
        };
        return new Gson().fromJson(jsonDevices, token.getType());
    }

    private static void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResult().equals("error")) {
            Timber.d("Could not complete request %s", result.getResponse().getMessage());
        }
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

            String channelIdPellets = context.getString(R.string.notification_channel_pellets);
            CharSequence namePellets = context.getString(R.string.notification_channel_pellets_name);
            String descriptionPellets = context.getString(R.string.notification_desc_pellets);
            final Uri pelletsAlertUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.pellet_alarm);
            int importancePellets = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel pelletAlerts = new NotificationChannel(channelIdPellets, namePellets,
                    importancePellets);
            pelletAlerts.setDescription(descriptionPellets);
            pelletAlerts.enableLights(true);
            pelletAlerts.setShowBadge(true);
            pelletAlerts.enableVibration(true);
            pelletAlerts.setGroup(groupId);
            pelletAlerts.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            pelletAlerts.setSound(pelletsAlertUri, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            pelletAlerts.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(pelletAlerts);

        }
    }
}
