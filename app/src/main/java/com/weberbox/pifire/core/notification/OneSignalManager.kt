package com.weberbox.pifire.core.notification

import android.Manifest
import android.content.Context
import android.os.Build
import com.onesignal.OSDeviceState
import com.onesignal.OneSignal
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.core.annotations.FutureDeprecation
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.OneSignalPush.OneSignalDeviceInfo
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.ui.util.checkPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@FutureDeprecation(
    comments = "OneSignal v4 is being deprecated for v5 which will cause current implementation to be unusable without API Auth. " +
        "This is not possible due to the requests being sent from individual PiFire servers and the API key cannot be publicly shared.",
    action = "Will need to find another provider or switch to a custom implementation using Firebase or ???",
)
class OneSignalManager @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val settingsRepo: SettingsRepo,
) {

    init {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.ERROR, OneSignal.LOG_LEVEL.NONE)
        OneSignal.setRequiresUserPrivacyConsent(true)
        OneSignal.initWithContext(appContext)
        OneSignal.setAppId(BuildConfig.ONESIGNAL_APP_ID)
    }

    fun provideUserConsent(accepted: Boolean) {
        OneSignal.provideUserConsent(accepted)
    }

    fun getUserConsent(): Boolean = OneSignal.userProvidedPrivacyConsent()

    /** This install's OneSignal id, if it has one. */
    suspend fun playerId(): String? = withContext(Dispatchers.IO) { OneSignal.getDeviceState()?.userId }

    private suspend fun registerDevice(registrationResult: OneSignalStatus) {
        if (OneSignal.userProvidedPrivacyConsent()) {
            OneSignal.getDeviceState()?.also { state ->
                if (state.isSubscribed) {
                    state.userId?.also { playerID ->
                        if (registrationResult == OneSignalStatus.ONESIGNAL_NOT_REGISTERED) {
                            settingsRepo.setOneSignalAppID(BuildConfig.ONESIGNAL_APP_ID)
                        }
                        registerOneSignalDevice(
                            playerID = playerID,
                            deviceInfo = getDevice(playerID),
                        )
                    }
                }
            }
        }
    }

    private suspend fun registerOneSignalDevice(
        playerID: String,
        deviceInfo: OneSignalDeviceInfo,
    ) {
        val device = mapOf(playerID to deviceInfo)
        when (val result = settingsRepo.registerOneSignalDevice(device)) {
            is Result.Error -> {
                Timber.d("Failed to register with PiFire: ${result.error}")
            }

            is Result.Success -> {
                settingsRepo.getSettings()
            }
        }
    }

    // The SDK and the registration call block, so this runs on IO and callers can stay on Main
    suspend fun checkOneSignalStatus(): OneSignalStatus = withContext(Dispatchers.IO) { checkStatus() }

    private suspend fun checkStatus(): OneSignalStatus {
        if (BuildConfig.ONESIGNAL_APP_ID.isNotBlank()) {
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
                    Timber.d("App registering with PiFire")
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

    private fun validateDeviceState(deviceState: OSDeviceState): OneSignalStatus = when {
        deviceState.pushToken == null -> OneSignalStatus.ONESIGNAL_NULL_TOKEN
        !deviceState.isSubscribed -> OneSignalStatus.ONESIGNAL_NOT_SUBSCRIBED
        else -> OneSignalStatus.ONESIGNAL_REGISTERED
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

        return OneSignalDeviceInfo(
            deviceName = Build.MODEL,
            friendlyName = existingDevice?.friendlyName ?: "",
            appVersion = BuildConfig.VERSION_NAME,
        )
    }

    private suspend fun getDevicesHash(): Map<String, OneSignalDeviceInfo> = settingsRepo.getCurrentServer()?.settings?.onesignalDevices ?: emptyMap()
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
