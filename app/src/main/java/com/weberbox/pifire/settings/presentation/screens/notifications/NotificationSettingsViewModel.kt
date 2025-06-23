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
): BaseViewModel<NotifContract.Event, NotifContract.State, NotifContract.Effect>() {

    init {
        collectServerData()
        checkOneSignalStatus()
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

    private fun setOneSignalEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (enabled) {
                checkOneSignalStatus()
                oneSignalManager.promptForPushNotifications()
            }
            handleResult(settingsRepo.setOneSignalEnabled(enabled))
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
                settingsRepo.updateServerSettings(result.data)
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

    private fun checkOneSignalStatus() {
        viewModelScope.launch {
            when (oneSignalManager.checkOneSignalStatus()) {
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