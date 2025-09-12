package com.weberbox.pifire.settings.presentation.screens.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.domain.Location
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.core.util.OneSignalManager
import com.weberbox.pifire.core.util.OneSignalStatus
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.NotifContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val oneSignalManager: OneSignalManager,
    private val settingsRepo: SettingsRepo
) : BaseViewModel<NotifContract.Event, NotifContract.State, NotifContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = NotifContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: NotifContract.Event) {
        toggleLoading(true)
        when (event) {
            is NotifContract.Event.SetAppriseEnabled -> setAppriseEnabled(event.enabled)
            is NotifContract.Event.UpdateAppriseLocation -> updateAppriseLocation(event.location)
            is NotifContract.Event.DeleteAppriseLocation -> deleteAppriseLocation(event.location)
            is NotifContract.Event.SetIFTTTAPIKey -> setIFTTTAPIKey(event.apiKey)
            is NotifContract.Event.SetIFTTTEnabled -> setIFTTTEnabled(event.enabled)
            is NotifContract.Event.SetInfluxDbBucket -> setInfluxDBBucket(event.bucket)
            is NotifContract.Event.SetInfluxDbEnabled -> setInfluxDBEnabled(event.enabled)
            is NotifContract.Event.SetInfluxDbOrg -> setInfluxDBOrg(event.org)
            is NotifContract.Event.SetInfluxDbToken -> setInfluxDBToken(event.token)
            is NotifContract.Event.SetInfluxDbUrl -> setInfluxDBUrl(event.url)
            is NotifContract.Event.SetMqttBroker -> setMqttBroker(event.broker)
            is NotifContract.Event.SetMqttEnabled -> setMqttEnabled(event.enabled)
            is NotifContract.Event.SetMqttId -> setMqttId(event.id)
            is NotifContract.Event.SetMqttPassword -> setMqttPassword(event.password)
            is NotifContract.Event.SetMqttPort -> setMqttPort(event.port)
            is NotifContract.Event.SetMqttTopic -> setMqttTopic(event.topic)
            is NotifContract.Event.SetMqttUpdateSec -> setMqttUpdateSec(event.updateSec)
            is NotifContract.Event.SetMqttUsername -> setMqttUsername(event.username)
            is NotifContract.Event.SetOneSignalAccepted -> onSignalAccepted = event.accepted
            is NotifContract.Event.SetOneSignalEnabled -> setOneSignalEnabled(event.enabled)
            is NotifContract.Event.SetPushBulletAPIKey -> setPushBulletAPIKey(event.apiKey)
            is NotifContract.Event.SetPushBulletEnabled -> setPushBulletEnabled(event.enabled)
            is NotifContract.Event.SetPushBulletURL -> setPushBulletURL(event.url)
            is NotifContract.Event.SetPushOverAPIKey -> setPushOverAPIKey(event.apiKey)
            is NotifContract.Event.SetPushOverEnabled -> setPushOverEnabled(event.enabled)
            is NotifContract.Event.SetPushOverUrl -> setPushOverUrl(event.url)
            is NotifContract.Event.SetPushOverUserKeys -> setPushOverUserKeys(event.userKeys)
            is NotifContract.Event.SetWLEDEnabled -> setWLEDEnabled(event.enabled)
            is NotifContract.Event.SetWLedAddress -> setWLEDAddress(event.address)
            is NotifContract.Event.SetWLedDuration -> setWLEDDuration(event.duration)
            is NotifContract.Event.SetWLedEventGrill -> setWLEDEventGrill(event.event)
            is NotifContract.Event.SetWLedEventPellets -> setWLEDEventPellets(event.event)
            is NotifContract.Event.SetWLedEventRecipe -> setWLEDEventRecipe(event.event)
            is NotifContract.Event.SetWLedEventTemp -> setWLEDEventTemp(event.event)
            is NotifContract.Event.SetWLedEventTimer -> setWLEDEventTimer(event.event)
            is NotifContract.Event.SetWLedModeHold -> setWLedModeHold(event.mode)
            is NotifContract.Event.SetWLedModePrime -> setWLedModePrime(event.mode)
            is NotifContract.Event.SetWLedModeReignite -> setWLedModeReignite(event.mode)
            is NotifContract.Event.SetWLedModeShutdown -> setWLedModeShutdown(event.mode)
            is NotifContract.Event.SetWLedModeSmoke -> setWLedModeSmoke(event.mode)
            is NotifContract.Event.SetWLedModeStartup -> setWLedModeStartup(event.mode)
            is NotifContract.Event.SetWLedModeStop -> setWLedModeStop(event.mode)
            is NotifContract.Event.SetWLedUseProfiles -> setWLedUseProfiles(event.useProfiles)
            is NotifContract.Event.SetWLEDNightMode -> setWLEDNightMode(event.enabled)
            is NotifContract.Event.SetWLedCookingColor -> setWLedCookingColor(event.color)
            is NotifContract.Event.SetWLedIdleBrightness -> setWLedIdleBrightness(event.brightness)
            is NotifContract.Event.SetWLedLedCount -> setWLedLedCount(event.count)
            is NotifContract.Event.SetWLedProfileBooting -> setWLedProfileBooting(event.profile)
            is NotifContract.Event.SetWLedProfileCooking -> setWLedProfileCooking(event.profile)
            is NotifContract.Event.SetWLedProfileCooling -> setWLedProfileCooldown(event.profile)
            is NotifContract.Event.SetWLedProfileFault -> setWLedProfileFault(event.profile)
            is NotifContract.Event.SetWLedProfileIdle -> setWLedProfileIdle(event.profile)
            is NotifContract.Event.SetWLedProfileNight -> setWLedProfileNight(event.profile)
            is NotifContract.Event.SetWLedProfileOvershoot -> setWLedProfileOvershoot(event.profile)
            is NotifContract.Event.SetWLedProfilePellets -> setWLedProfilePellets(event.profile)
            is NotifContract.Event.SetWLedProfilePreheat -> setWLedProfilePreheat(event.profile)
            is NotifContract.Event.SetWLedProfileProbe -> setWLedProfileProbe(event.profile)
            is NotifContract.Event.SetWLedProfileTarget -> setWLedProfileTarget(event.profile)
            is NotifContract.Event.SetWLedProfileTimer -> setWLedProfileTimer(event.profile)
            is NotifContract.Event.SetWLedUseSuggestedProfiles -> setWLedUseSuggestedProfiles(event.useProfiles)
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            settingsRepo.getCurrentServerFlow().collect { server ->
                setState {
                    copy(
                        serverData = server,
                        isInitialLoading = false
                    )
                }
                if (server.settings.onesignalEnabled) {
                    checkOneSignalStatus()
                }
            }
        }
    }

    var acceptedState = mutableStateOf(onSignalAccepted)
        private set

    private var onSignalAccepted
        get() = oneSignalManager.getUserConsent()
        set(value) {
            toggleLoading(false)
            acceptedState.value = value
            oneSignalManager.provideUserConsent(value)
        }

    private fun setIFTTTEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setIFTTTEnabled(enabled))
        }
    }

    private fun setIFTTTAPIKey(apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setIFTTTAPIKey(apiKey))
        }
    }

    private fun setPushOverEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPushOverEnabled(enabled))
        }
    }

    private fun setPushOverAPIKey(apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPushOverAPIKey(apiKey))
        }
    }

    private fun setPushOverUserKeys(userKeys: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPushOverUserKeys(userKeys))
        }
    }

    private fun setPushOverUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPushOverUrl(url))
        }
    }

    private fun setPushBulletEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPushBulletEnabled(enabled))
        }
    }

    private fun setPushBulletAPIKey(apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPushBulletAPIKey(apiKey))
        }
    }

    private fun setPushBulletURL(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setPushBulletURL(url))
        }
    }

    private fun setInfluxDBEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setInfluxDBEnabled(enabled))
        }
    }

    private fun setInfluxDBUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setInfluxDBUrl(url))
        }
    }

    private fun setInfluxDBToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setInfluxDBToken(token))
        }
    }

    private fun setInfluxDBOrg(org: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setInfluxDBOrg(org))
        }
    }

    private fun setInfluxDBBucket(bucket: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setInfluxDBBucket(bucket))
        }
    }

    private fun setMqttEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttEnabled(enabled))
        }
    }

    private fun setMqttBroker(broker: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttBroker(broker))
        }
    }

    private fun setMqttTopic(topic: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttTopic(topic))
        }
    }

    private fun setMqttId(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttId(id))
        }
    }

    private fun setMqttUsername(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttUsername(username))
        }
    }

    private fun setMqttPassword(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttPassword(password))
        }
    }

    private fun setMqttPort(port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttPort(port))
        }
    }

    private fun setMqttUpdateSec(updateSec: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setMqttUpdateSec(updateSec))
        }
    }

    private fun setAppriseEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setAppriseEnabled(enabled))
        }
    }

    private fun updateAppriseLocation(location: Location) {
        val locations = viewState.value.serverData.settings.appriseLocations
        val updatedLocations = locations.plus(location)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setAppriseLocations(updatedLocations))
        }
    }

    private fun deleteAppriseLocation(location: Location) {
        val locations = viewState.value.serverData.settings.appriseLocations
        val updatedLocations = locations.filterNot { it == location }
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setAppriseLocations(updatedLocations))
        }
    }

    private fun setWLEDEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLEDEnabled(enabled))
        }
    }

    private fun setWLEDAddress(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLEDAddress(address))
        }
    }

    private fun setWLEDDuration(duration: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLEDDuration(duration))
        }
    }

    private fun setWLedModeHold(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedModeHold(mode))
        }
    }

    private fun setWLedModePrime(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedModePrime(mode))
        }
    }

    private fun setWLedModeReignite(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedModeReignite(mode))
        }
    }

    private fun setWLedModeShutdown(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedModeShutdown(mode))
        }
    }

    private fun setWLedModeSmoke(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedModeSmoke(mode))
        }
    }

    private fun setWLedModeStartup(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedModeStartup(mode))
        }
    }

    private fun setWLedModeStop(mode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedModeStop(mode))
        }
    }

    private fun setWLedUseProfiles(useProfiles: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedUseProfiles(useProfiles))
        }
    }

    private fun setWLedUseSuggestedProfiles(useProfiles: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedUseSuggestedProfiles(useProfiles))
        }
    }

    private fun setWLEDNightMode(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedNightMode(enabled))
        }
    }

    private fun setWLedCookingColor(color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedCookingColor(color))
        }
    }

    private fun setWLedIdleBrightness(brightness: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedIdleBrightness(brightness))
        }
    }

    private fun setWLedLedCount(count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedLedCount(count))
        }
    }

    private fun setWLedProfileBooting(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileBooting(profile))
        }
    }

    private fun setWLedProfileCooking(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileCooking(profile))
        }
    }

    private fun setWLedProfileCooldown(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileCooldown(profile))
        }
    }

    private fun setWLedProfileFault(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileFault(profile))
        }
    }

    private fun setWLedProfileIdle(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileIdle(profile))
        }
    }

    private fun setWLedProfileNight(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileNight(profile))
        }
    }

    private fun setWLedProfileOvershoot(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileOvershoot(profile))
        }
    }

    private fun setWLedProfilePellets(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfilePellets(profile))
        }
    }

    private fun setWLedProfilePreheat(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfilePreheat(profile))
        }
    }

    private fun setWLedProfileProbe(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileProbe(profile))
        }
    }

    private fun setWLedProfileTarget(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileTarget(profile))
        }
    }

    private fun setWLedProfileTimer(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedProfileTimer(profile))
        }
    }

    private fun setWLEDEventGrill(event: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedEventGrill(event))
        }
    }

    private fun setWLEDEventPellets(event: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedEventPellets(event))
        }
    }

    private fun setWLEDEventRecipe(event: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedEventRecipe(event))
        }
    }

    private fun setWLEDEventTemp(event: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedEventTemp(event))
        }
    }

    private fun setWLEDEventTimer(event: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setWLedEventTimer(event))
        }
    }

    private fun setOneSignalEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setOneSignalEnabled(enabled))
            if (enabled) checkOneSignalStatus()
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        NotifContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true
                        )
                    }
                }
            }

            is Result.Success -> {
                withContext(Dispatchers.Main) {
                    setState {
                        copy(
                            serverData = result.data,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun checkOneSignalStatus() {
        val result = oneSignalManager.checkOneSignalStatus()
        withContext(Dispatchers.Main) {
            when (result) {
                OneSignalStatus.ONESIGNAL_REGISTERED -> {}
                OneSignalStatus.ONESIGNAL_APP_UPDATED -> {}
                OneSignalStatus.ONESIGNAL_NO_ID -> {
                    setEffect {
                        NotifContract.Effect.Notification(
                            text = UiText(R.string.settings_onesignal_id_error_message),
                            error = true
                        )
                    }
                }

                OneSignalStatus.ONESIGNAL_NO_CONSENT -> {
                    setEffect {
                        NotifContract.Effect.Notification(
                            text = UiText(R.string.settings_onesignal_alert_consent_message),
                            error = true
                        )
                    }
                }

                OneSignalStatus.ONESIGNAL_NOT_REGISTERED -> {
                    setEffect {
                        NotifContract.Effect.Notification(
                            text = UiText(R.string.settings_onesignal_register_message),
                            error = true
                        )
                    }
                }

                OneSignalStatus.ONESIGNAL_DEVICE_ERROR -> {
                    setEffect {
                        NotifContract.Effect.Notification(
                            text = UiText(R.string.settings_onesignal_error_message),
                            error = true
                        )
                    }
                }

                OneSignalStatus.ONESIGNAL_NULL_TOKEN -> {
                    setEffect {
                        NotifContract.Effect.Notification(
                            text = UiText(R.string.settings_onesignal_token_message),
                            error = true
                        )
                    }
                }

                OneSignalStatus.ONESIGNAL_NOT_SUBSCRIBED -> {
                    setEffect {
                        NotifContract.Effect.Notification(
                            text = UiText(R.string.settings_onesignal_subscribe_message),
                            error = true
                        )
                    }
                }

                OneSignalStatus.ONESIGNAL_NOTIFICATION_PERMISSION_DENIED -> {
                    setEffect {
                        NotifContract.Effect.RequestPermission
                    }
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        setState {
            copy(
                isLoading = loading
            )
        }
    }
}