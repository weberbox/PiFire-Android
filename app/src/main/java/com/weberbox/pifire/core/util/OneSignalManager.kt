package com.weberbox.pifire.core.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.onesignal.OSDeviceState
import com.onesignal.OneSignal
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.config.Secrets
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class OneSignalManager @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val settingsRepo: SettingsRepo
) {

    init {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.ERROR, OneSignal.LOG_LEVEL.NONE)
        OneSignal.setRequiresUserPrivacyConsent(true)
        OneSignal.initWithContext(appContext)
        OneSignal.setAppId(Secrets.ONESIGNAL_APP_ID)
        initNotificationChannels(appContext)
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
                            settingsRepo.setOneSignalAppID(Secrets.ONESIGNAL_APP_ID)
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
        if (Secrets.ONESIGNAL_APP_ID.isNotBlank()) {
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

                OneSignalStatus.ONESIGNAL_NOTIFICATION_PERMISSION_DENIED -> {
                    Timber.d("Notification permission denied")
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!appContext.checkPermission(Manifest.permission.POST_NOTIFICATIONS)) {
                return OneSignalStatus.ONESIGNAL_NOTIFICATION_PERMISSION_DENIED
            }
        }

        val deviceState =
            OneSignal.getDeviceState() ?: return OneSignalStatus.ONESIGNAL_DEVICE_ERROR

        val deviceValidationStatus = validateDeviceState(deviceState)
        if (deviceValidationStatus != OneSignalStatus.ONESIGNAL_REGISTERED) {
            return deviceValidationStatus
        }

        val playerID = deviceState.userId ?: return OneSignalStatus.ONESIGNAL_NO_ID
        return validatePlayerRegistration(playerID)
    }

    private fun validateDeviceState(deviceState: OSDeviceState): OneSignalStatus {
        return when {
            deviceState.pushToken == null -> OneSignalStatus.ONESIGNAL_NULL_TOKEN
            !deviceState.isSubscribed -> OneSignalStatus.ONESIGNAL_NOT_SUBSCRIBED
            else -> OneSignalStatus.ONESIGNAL_REGISTERED
        }
    }

    private suspend fun validatePlayerRegistration(playerID: String): OneSignalStatus {
        val devicesHash = getDevicesHash()

        val deviceInfo = devicesHash[playerID] ?: return OneSignalStatus.ONESIGNAL_NOT_REGISTERED
        return if (deviceInfo.appVersion != BuildConfig.VERSION_NAME) {
            OneSignalStatus.ONESIGNAL_APP_UPDATED
        } else {
            OneSignalStatus.ONESIGNAL_REGISTERED
        }
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

            val channels = listOf(
                NotificationChannelConfig(
                    channelId = context.getString(R.string.notification_channel_base),
                    name = context.getString(R.string.notification_channel_base_name),
                    description = context.getString(R.string.notification_desc_base)
                ),
                NotificationChannelConfig(
                    channelId = context.getString(R.string.notification_channel_temp),
                    name = context.getString(R.string.notification_channel_temp_name),
                    description = context.getString(R.string.notification_desc_temp),
                    soundResourceId = R.raw.temp_achieved
                ),
                NotificationChannelConfig(
                    channelId = context.getString(R.string.notification_channel_timer),
                    name = context.getString(R.string.notification_channel_timer_name),
                    description = context.getString(R.string.notification_desc_timer),
                    soundResourceId = R.raw.timer_alarm,
                    useVibrationPattern = true
                ),
                NotificationChannelConfig(
                    channelId = context.getString(R.string.notification_channel_error),
                    name = context.getString(R.string.notification_channel_error_name),
                    description = context.getString(R.string.notification_desc_error),
                    soundResourceId = R.raw.grill_error,
                    useVibrationPattern = true
                ),
                NotificationChannelConfig(
                    channelId = context.getString(R.string.notification_channel_pellets),
                    name = context.getString(R.string.notification_channel_pellets_name),
                    description = context.getString(R.string.notification_desc_pellets),
                    soundResourceId = R.raw.pellet_alarm,
                    useVibrationPattern = true
                )
            )

            channels.forEach { config ->
                createNotificationChannel(
                    notificationManager = notificationManager,
                    config = config
                )
            }
        }
    }

    private data class NotificationChannelConfig(
        val channelId: String,
        val name: String,
        val description: String,
        val soundResourceId: Int? = null,
        val useVibrationPattern: Boolean = false
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        config: NotificationChannelConfig
    ) {
        val channel = NotificationChannel(
            config.channelId,
            config.name,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = config.description
            enableLights(true)
            setShowBadge(true)
            enableVibration(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC

            if (config.useVibrationPattern) {
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }

            config.soundResourceId?.let { resourceId ->
                val soundUri = buildNotificationUri(resourceId)
                setSound(soundUri, createAudioAttributes())
            }
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotificationUri(resourceId: Int) =
        (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                BuildConfig.APPLICATION_ID + "/" + resourceId).toUri()

    private fun createAudioAttributes() = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
}

enum class OneSignalStatus {
    ONESIGNAL_NO_ID,
    ONESIGNAL_NO_CONSENT,
    ONESIGNAL_NOT_REGISTERED,
    ONESIGNAL_APP_UPDATED,
    ONESIGNAL_DEVICE_ERROR,
    ONESIGNAL_NULL_TOKEN,
    ONESIGNAL_REGISTERED,
    ONESIGNAL_NOT_SUBSCRIBED,
    ONESIGNAL_NOTIFICATION_PERMISSION_DENIED,
}