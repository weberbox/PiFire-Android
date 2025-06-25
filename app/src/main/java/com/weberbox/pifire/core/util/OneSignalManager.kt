package com.weberbox.pifire.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.onesignal.OneSignal
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.config.PushConfig
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class OneSignalManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settingsRepo: SettingsRepo
) {

    init {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.ERROR, OneSignal.LOG_LEVEL.NONE)
        OneSignal.setRequiresUserPrivacyConsent(true)
        OneSignal.initWithContext(appContext.applicationContext)
        OneSignal.setAppId(PushConfig.ONESIGNAL_APP_ID)
        initNotificationChannels(appContext.applicationContext)
    }

    fun promptForPushNotifications() {
        OneSignal.promptForPushNotifications()
    }

    fun provideUserConsent(accepted: Boolean) {
        OneSignal.provideUserConsent(accepted)
    }

    fun getUserConsent(): Boolean {
        return OneSignal.userProvidedPrivacyConsent()
    }

    private suspend fun registerDevice(registrationResult: OneSignalStatus) {
        if (OneSignal.userProvidedPrivacyConsent()) {
            OneSignal.getDeviceState()?.also { state ->
                if (state.isSubscribed) {
                    state.userId?.also { playerID ->
                        if (registrationResult == OneSignalStatus.ONESIGNAL_NOT_REGISTERED) {
                            settingsRepo.setOneSignalAppID(PushConfig.ONESIGNAL_APP_ID)
                        }
                        registerOneSignalDevice(playerID, getDevice(playerID))
                    }
                }
            }
        }
    }

    private suspend fun registerOneSignalDevice(
        playerID: String,
        deviceInfo: OneSignalDeviceInfo
    ) {
        val device = mapOf(Pair(playerID, deviceInfo))
        when (val result = settingsRepo.registerOneSignalDevice(device)) {
            is Result.Error -> {
                Timber.d("Failed to register with PiFire: ${result.error}")
            }

            is Result.Success -> {
                settingsRepo.getSettings()
            }
        }
    }

    suspend fun checkOneSignalStatus(): OneSignalStatus {
        if (PushConfig.ONESIGNAL_APP_ID.isNotBlank()) {
            when (val status = checkRegistration()) {
                OneSignalStatus.ONESIGNAL_NO_ID -> {
                    Timber.d("Device has No ID")
                    return status
                }

                OneSignalStatus.ONESIGNAL_NO_CONSENT -> {
                    Timber.d("OneSignal does not have consent")
                    return status
                }

                OneSignalStatus.ONESIGNAL_NOT_REGISTERED -> {
                    registerDevice(status)
                    return status
                }

                OneSignalStatus.ONESIGNAL_APP_UPDATED -> {
                    Timber.d("App updated re-registering with PiFire")
                    registerDevice(status)
                    return status
                }

                OneSignalStatus.ONESIGNAL_DEVICE_ERROR -> {
                    Timber.d("Device Error")
                    return status
                }

                OneSignalStatus.ONESIGNAL_NULL_TOKEN -> {
                    Timber.d("Device Token Error")
                    return status
                }

                OneSignalStatus.ONESIGNAL_REGISTERED -> {
                    Timber.d("Device already registered with PiFire")
                    return status
                }

                OneSignalStatus.ONESIGNAL_NOT_SUBSCRIBED -> {
                    Timber.d("Device is not subscribed")
                    return status
                }
            }
        }
        return OneSignalStatus.ONESIGNAL_NO_CONSENT
    }

    private suspend fun checkRegistration(): OneSignalStatus {
        if (!getUserConsent()) {
            return OneSignalStatus.ONESIGNAL_NO_CONSENT
        }

        val deviceState = OneSignal.getDeviceState()
        if (deviceState == null) {
            return OneSignalStatus.ONESIGNAL_DEVICE_ERROR
        } else if (deviceState.pushToken == null) {
            return OneSignalStatus.ONESIGNAL_NULL_TOKEN
        } else if (!deviceState.isSubscribed) {
            return OneSignalStatus.ONESIGNAL_NOT_SUBSCRIBED
        }

        val playerID = deviceState.userId ?: return OneSignalStatus.ONESIGNAL_NO_ID

        val devicesHash = getDevicesHash()

        if (!devicesHash.containsKey(playerID)) {
            return OneSignalStatus.ONESIGNAL_NOT_REGISTERED
        }

        for ((key, value) in devicesHash) {
            if (key == playerID && value.appVersion != BuildConfig.VERSION_NAME) {
                return OneSignalStatus.ONESIGNAL_APP_UPDATED
            }
        }

        return OneSignalStatus.ONESIGNAL_REGISTERED
    }

    private suspend fun getDevice(playerID: String): OneSignalDeviceInfo {
        val devicesHash = getDevicesHash()
        val existingDevice = devicesHash[playerID]

        return if (existingDevice != null) {
            OneSignalDeviceInfo(
                appVersion = BuildConfig.VERSION_NAME
            )
        } else {
            OneSignalDeviceInfo(
                deviceName = Build.MODEL,
                friendlyName = "",
                appVersion = BuildConfig.VERSION_NAME
            )
        }
    }

    private suspend fun getDevicesHash(): Map<String, OneSignalDeviceInfo> {
        return settingsRepo.getCurrentServer()?.settings?.onesignalDevices ?: emptyMap()
    }

    private fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelIdBase = context.getString(R.string.notification_channel_base)
            val nameBase: CharSequence = context.getString(R.string.notification_channel_base_name)
            val descriptionBase = context.getString(R.string.notification_desc_base)

            val baseAlerts = NotificationChannel(
                channelIdBase, nameBase,
                NotificationManager.IMPORTANCE_HIGH
            )
            baseAlerts.description = descriptionBase
            baseAlerts.enableLights(true)
            baseAlerts.setShowBadge(true)
            baseAlerts.enableVibration(true)
            baseAlerts.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(baseAlerts)

            val channelIdTemp = context.getString(R.string.notification_channel_temp)
            val nameTemp: CharSequence = context.getString(R.string.notification_channel_temp_name)
            val descriptionTemp = context.getString(R.string.notification_desc_temp)
            val tempAlertUri = (ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.temp_achieved).toUri()
            val importanceTemp = NotificationManager.IMPORTANCE_HIGH

            val tempAlerts = NotificationChannel(
                channelIdTemp, nameTemp,
                importanceTemp
            )
            tempAlerts.description = descriptionTemp
            tempAlerts.enableLights(true)
            tempAlerts.setShowBadge(true)
            tempAlerts.enableVibration(true)
            tempAlerts.setSound(
                tempAlertUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            tempAlerts.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(tempAlerts)

            val channelIdTimer = context.getString(R.string.notification_channel_timer)
            val nameTimer: CharSequence =
                context.getString(R.string.notification_channel_timer_name)
            val descriptionTimer = context.getString(R.string.notification_desc_timer)
            val timerAlertUri = (ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.timer_alarm).toUri()
            val importanceTimer = NotificationManager.IMPORTANCE_HIGH

            val timerAlerts = NotificationChannel(
                channelIdTimer, nameTimer,
                importanceTimer
            )
            timerAlerts.description = descriptionTimer
            timerAlerts.enableLights(true)
            timerAlerts.setShowBadge(true)
            timerAlerts.enableVibration(true)
            timerAlerts.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            timerAlerts.setSound(
                timerAlertUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            timerAlerts.lockscreenVisibility =
                NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(timerAlerts)

            val channelIdError = context.getString(R.string.notification_channel_error)
            val nameError: CharSequence =
                context.getString(R.string.notification_channel_error_name)
            val descriptionError = context.getString(R.string.notification_desc_error)
            val errorAlertUri = (ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.grill_error).toUri()
            val importanceError = NotificationManager.IMPORTANCE_HIGH

            val errorAlerts = NotificationChannel(
                channelIdError, nameError,
                importanceError
            )
            errorAlerts.description = descriptionError
            errorAlerts.enableLights(true)
            errorAlerts.setShowBadge(true)
            errorAlerts.enableVibration(true)
            errorAlerts.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            errorAlerts.setSound(
                errorAlertUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            errorAlerts.lockscreenVisibility =
                NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(errorAlerts)

            val channelIdPellets = context.getString(R.string.notification_channel_pellets)
            val namePellets: CharSequence =
                context.getString(R.string.notification_channel_pellets_name)
            val descriptionPellets = context.getString(R.string.notification_desc_pellets)
            val pelletsAlertUri = (ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.pellet_alarm).toUri()
            val importancePellets = NotificationManager.IMPORTANCE_HIGH

            val pelletAlerts = NotificationChannel(
                channelIdPellets, namePellets,
                importancePellets
            )
            pelletAlerts.description = descriptionPellets
            pelletAlerts.enableLights(true)
            pelletAlerts.setShowBadge(true)
            pelletAlerts.enableVibration(true)
            pelletAlerts.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            pelletAlerts.setSound(
                pelletsAlertUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            pelletAlerts.lockscreenVisibility =
                NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(pelletAlerts)
        }
    }
}

enum class OneSignalStatus {
    ONESIGNAL_NO_ID,
    ONESIGNAL_NO_CONSENT,
    ONESIGNAL_NOT_REGISTERED,
    ONESIGNAL_APP_UPDATED,
    ONESIGNAL_DEVICE_ERROR,
    ONESIGNAL_NULL_TOKEN,
    ONESIGNAL_REGISTERED,
    ONESIGNAL_NOT_SUBSCRIBED
}