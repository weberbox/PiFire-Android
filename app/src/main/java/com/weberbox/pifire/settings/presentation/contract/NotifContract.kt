package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server

class NotifContract {

    sealed class Event : ViewEvent {
        data class SetIFTTTEnabled(val enabled: Boolean) : Event()
        data class SetIFTTTAPIKey(val apiKey: String) : Event()
        data class SetPushOverEnabled(val enabled: Boolean) : Event()
        data class SetPushOverAPIKey(val apiKey: String) : Event()
        data class SetPushOverUserKeys(val userKeys: String) : Event()
        data class SetPushOverUrl(val url: String) : Event()
        data class SetPushBulletEnabled(val enabled: Boolean) : Event()
        data class SetPushBulletAPIKey(val apiKey: String) : Event()
        data class SetPushBulletURL(val url: String) : Event()
        data class SetInfluxDbEnabled(val enabled: Boolean) : Event()
        data class SetInfluxDbUrl(val url: String) : Event()
        data class SetInfluxDbToken(val token: String) : Event()
        data class SetInfluxDbOrg(val org: String) : Event()
        data class SetInfluxDbBucket(val bucket: String) : Event()
        data class SetMqttEnabled(val enabled: Boolean) : Event()
        data class SetMqttBroker(val broker: String) : Event()
        data class SetMqttTopic(val topic: String) : Event()
        data class SetMqttId(val id: String) : Event()
        data class SetMqttUsername(val username: String) : Event()
        data class SetMqttPassword(val password: String) : Event()
        data class SetMqttPort(val port: Int) : Event()
        data class SetMqttUpdateSec(val updateSec: Int) : Event()
        data class SetAppriseEnabled(val enabled: Boolean) : Event()
        data class UpdateAppriseLocation(val location: String) : Event()
        data class DeleteAppriseLocation(val location: String) : Event()
        data class SetOneSignalEnabled(val enabled: Boolean) : Event()
        data class SetOneSignalAccepted(val accepted: Boolean) : Event()
    }

    data class State(
        val serverData: Server,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()
        data object RequestPermission : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }
    }
}