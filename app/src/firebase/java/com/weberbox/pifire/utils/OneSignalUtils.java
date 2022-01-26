package com.weberbox.pifire.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.PushConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.interfaces.SettingsCallback;
import com.weberbox.pifire.model.remote.SettingsResponseModel.OneSignalDeviceInfo;

import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class OneSignalUtils {

    public static void initOneSignal(Context context) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.ERROR, OneSignal.LOG_LEVEL.NONE);

        OneSignal.setRequiresUserPrivacyConsent(true);
        OneSignal.initWithContext(context);
        OneSignal.setAppId(PushConfig.ONESIGNAL_APP_ID);
    }

    @SuppressWarnings("unused")
    public static void setSubscriptionObserver(Socket socket) {
        OneSignal.addSubscriptionObserver(stateChanges -> {
            Timber.d("OneSignal State Changed %s", stateChanges);
            if (!stateChanges.getFrom().isSubscribed() &&
                    stateChanges.getTo().isSubscribed()) {
                registerPlayerID(socket, stateChanges.getTo().getUserId(), true);

            } else if (stateChanges.getFrom().isSubscribed() &&
                    !stateChanges.getTo().isSubscribed()) {
                registerPlayerID(socket, stateChanges.getFrom().getUserId(), false);
            }
        });
    }

    private static void registerPlayerID(Socket socket, String playerID,
                                         boolean subscribed) {
        if (playerID != null && socket.connected()) {
            if (subscribed) {
                GrillControl.setOneSignalAppID(socket, PushConfig.ONESIGNAL_APP_ID);
                GrillControl.addOneSignalDevice(socket, playerID, getDevice());
            } else {
                GrillControl.removeOneSignalDevice(socket, playerID);
            }
        }
    }

    public static void provideUserConsent(boolean accepted) {
        OneSignal.provideUserConsent(accepted);
    }

    public static void registerDevice(Context context, Socket socket) {
        if (OneSignal.userProvidedPrivacyConsent()) {
            if (OneSignal.getDeviceState() != null) {
                String playerID = OneSignal.getDeviceState().getUserId();
                if (playerID != null) {
                    Map<String, OneSignalDeviceInfo> devicesHash = getDevicesHash(context);
                    if (devicesHash != null) {
                        for (Map.Entry<String, OneSignalDeviceInfo> device :
                                devicesHash.entrySet()) {
                            if (device.getKey().equals(playerID) &&
                                    device.getValue().getAppVersion().equals(
                                            BuildConfig.VERSION_NAME)) {
                                Timber.d("Device already registered with PiFire");
                                return;
                            }
                        }
                    }

                    GrillControl.setOneSignalAppID(socket, PushConfig.ONESIGNAL_APP_ID);
                    addOneSignalDevice(context, socket, playerID, getDevice());
                }
            }
        }
    }

    private static void addOneSignalDevice(Context context, Socket socket, String playerID,
                                           OneSignalDeviceInfo deviceInfo) {
        if (socket.connected()) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.NOTIF_ACTION,
                            ServerConstants.NOTIF_ONESIGNAL_ADD, true,
                            ServerConstants.NOTIF_ONESIGNAL_PLAYER_ID, playerID,
                            ServerConstants.NOTIF_ONESIGNAL_DEVICE_NAME,
                            deviceInfo.getDeviceName(),
                            ServerConstants.NOTIF_ONESIGNAL_APP_VERSION,
                            deviceInfo.getAppVersion()),
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null) {
                            if (args[0].toString().equalsIgnoreCase("success")) {
                                new SettingsUtils(context, settingsCallback)
                                        .requestSettingsData(socket);
                            } else {
                                Timber.d("Failed to register with Pifire");
                            }
                        }
                    });
        }
    }

    public static void checkOneSignalStatus(Activity activity, Socket socket) {
        int status = OneSignalUtils.checkRegistration(activity);
        switch (status) {
            case Constants.ONESIGNAL_NO_ID:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_id_error_title,
                        R.string.settings_onesignal_id_error_message);
                break;
            case Constants.ONESIGNAL_NO_CONSENT:
                if (!Prefs.getBoolean(activity.getString(
                        R.string.prefs_onesignal_consent_dismiss))) {
                    AlertUtils.createOneSignalConsentAlert(activity,
                            R.string.settings_onesignal_alert_consent_title,
                            R.string.settings_onesignal_alert_consent_message);
                }
                break;
            case Constants.ONESIGNAL_NOT_REGISTERED:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_register_title,
                        R.string.settings_onesignal_register_message);
                registerDevice(activity, socket);
                break;
            case Constants.ONESIGNAL_DEVICE_ERROR:
                AlertUtils.createOneSignalAlert(activity,
                        R.string.settings_onesignal_error_title,
                        R.string.settings_onesignal_error_message);
                break;
            case Constants.ONESIGNAL_REGISTERED:
                Timber.d("Device already registered with PiFire");
                break;
        }
    }

    public static int checkRegistration(Context context) {
        if (!OneSignal.userProvidedPrivacyConsent()) {
            return Constants.ONESIGNAL_NO_CONSENT;
        }
        if (OneSignal.getDeviceState() == null) return Constants.ONESIGNAL_DEVICE_ERROR;
        String playerID = OneSignal.getDeviceState().getUserId();
        if (playerID == null) return Constants.ONESIGNAL_NO_ID;

        Map<String, OneSignalDeviceInfo> devicesHash = getDevicesHash(context);

        if (devicesHash == null || !devicesHash.containsKey(playerID)) {
            return Constants.ONESIGNAL_NOT_REGISTERED;
        }
        return Constants.ONESIGNAL_REGISTERED;
    }

    private static OneSignalDeviceInfo getDevice() {
        OneSignalDeviceInfo device = new OneSignalDeviceInfo();
        device.setDeviceName(android.os.Build.MODEL);
        device.setAppVersion(BuildConfig.VERSION_NAME);
        return device;
    }

    private static Map<String, OneSignalDeviceInfo> getDevicesHash(Context context) {
        String jsonDevices = Prefs.getString(context.getString(
                R.string.prefs_notif_onesignal_device_list));
        TypeToken<Map<String, OneSignalDeviceInfo>> token = new TypeToken<>() {
        };
        return new Gson().fromJson(jsonDevices, token.getType());
    }

    private static final SettingsCallback settingsCallback = result -> {
        if (!result) Timber.d("Update Settings Failed");
    };

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
