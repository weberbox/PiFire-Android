package com.weberbox.pifire.feature.landing.presentation.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.R
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.util.CoroutinePoller
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.model.settings.SettingsData
import com.weberbox.pifire.core.network.UrlBuilder
import com.weberbox.pifire.core.network.headers.HeadersManager
import com.weberbox.pifire.core.repo.ConnectionRepo
import com.weberbox.pifire.core.repo.PrefsRepo
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.core.ui.util.createUrl
import com.weberbox.pifire.feature.landing.data.model.ServerUuidData
import com.weberbox.pifire.feature.landing.data.repo.LandingRepo
import com.weberbox.pifire.feature.landing.presentation.contract.LandingContract
import com.weberbox.pifire.feature.landing.presentation.model.ServerData
import com.weberbox.pifire.feature.landing.presentation.model.ServerData.Server
import com.weberbox.pifire.navigation.NavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val landingRepo: LandingRepo,
    private val settingsRepo: SettingsRepo,
    private val connectionRepo: ConnectionRepo,
    private val analytics: Analytics,
    private val sessionStateHolder: SessionStateHolder,
    private val headersManager: HeadersManager,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LandingContract.Event, LandingContract.State, LandingContract.Effect>() {
    private val pollerJobs = mutableListOf<Job>()
    private var servers: Map<String, SettingsData.Server> = emptyMap()
    private var pollingActive = false
    private var autoSelectServer by mutableStateOf(prefsRepo.get(Pref.landingAutoSelect))
    private var firstLaunch by mutableStateOf(true)
    private var requireServerBiometrics by mutableStateOf(true)

    init {
        firstLaunch = savedStateHandle.toRoute<NavGraph.LandingDest.Landing>().firstLaunch
        collectPrefsFlow()
        collectSettingsData()
    }

    override fun setInitialState() = LandingContract.State(
        serverData = ServerData(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
        biometricServerPrompt = false,
    )

    override fun handleEvents(event: LandingContract.Event) {
        when (event) {
            is LandingContract.Event.Back -> setEffect { LandingContract.Effect.Navigation.Back }

            is LandingContract.Event.DeleteServer -> deleteServer(event.uuid)

            is LandingContract.Event.SelectServer -> selectServer(event.uuid)

            is LandingContract.Event.VoiceSearch -> {
                analytics.logEvent(
                    name = AnalyticsEvent.VoiceSearch.name,
                )
            }

            is LandingContract.Event.StartPolling -> {
                pollingActive = true
                startServerPolling()
            }

            is LandingContract.Event.StopPolling -> {
                pollingActive = false
                stopServerPolling()
            }
        }
    }

    private fun collectPrefsFlow() {
        viewModelScope.launch {
            prefsRepo.prefFlow(Pref.landingAutoSelect).collect {
                autoSelectServer = it
            }
        }
        viewModelScope.launch {
            prefsRepo.prefFlow(Pref.biometricServerPrompt).collect {
                requireServerBiometrics = it
                setState {
                    copy(
                        biometricServerPrompt = it,
                    )
                }
            }
        }
    }

    private fun collectSettingsData() {
        viewModelScope.launch {
            settingsRepo.getSettingsFlow().collect {
                servers = it.serverMap
                if (pollingActive) startServerPolling()
                setState {
                    copy(
                        isInitialLoading = false,
                        serverData = ServerData(
                            servers = it.serverMap.entries.mapIndexed { index, server ->
                                Server(
                                    uuid = server.value.uuid,
                                    name = server.value.name,
                                    number = index + 1,
                                    address = server.value.address,
                                    online = false,
                                )
                            },
                        ),
                    )
                }
            }
        }
    }

    private fun stopServerPolling() {
        pollerJobs.forEach { job -> job.cancel() }
        pollerJobs.clear()
    }

    private fun startServerPolling() {
        stopServerPolling()
        servers.forEach { server ->
            val job = viewModelScope.launch {
                while (isActive) {
                    CoroutinePoller(
                        fetchData = {
                            landingRepo.getServerUuid(
                                url = buildUuidUrl(server.value.address),
                                headers = headersManager.buildServerHeaders(server.value),
                            )
                        },
                    ).poll(2000).collect { result ->
                        updateServerState(result, server.key)
                    }
                }
            }
            pollerJobs.add(job)
        }
    }

    private fun updateServerState(result: Result<ServerUuidData, DataError>, uuid: String) {
        when (result) {
            is Result.Success -> {
                // Another PiFire answering at this address is not this server
                val isSameServer = result.data.uuid == uuid
                setOnlineState(uuid = uuid, online = isSameServer)
                if (isSameServer && autoSelectServer && firstLaunch) {
                    firstLaunch = false
                    if (requireServerBiometrics) {
                        setEffect {
                            LandingContract.Effect.BiometricServerPrompt(
                                uuid = uuid,
                            )
                        }
                    } else {
                        viewModelScope.launch {
                            delay(500.milliseconds)
                            selectServer(uuid)
                        }
                    }
                }
            }

            is Result.Error -> {
                setOnlineState(uuid = uuid, online = false)
            }
        }
    }

    private fun setOnlineState(uuid: String?, online: Boolean) {
        val serverData = viewState.value.serverData
        val updatedServers = serverData.servers.map { server ->
            if (server.uuid == uuid && server.online != online) {
                server.copy(online = online)
            } else {
                server
            }
        }
        if (updatedServers != serverData.servers) {
            setState {
                copy(
                    serverData = serverData.copy(
                        servers = updatedServers,
                    ),
                )
            }
        }
    }

    private fun selectServer(uuid: String) {
        toggleLoading(true)
        viewModelScope.launch {
            settingsRepo.selectServer(uuid)?.let { server ->
                when (connectionRepo.connect(server)) {
                    false -> {
                        toggleLoading(false)
                        setEffect {
                            LandingContract.Effect.Notification(
                                text = UiText(R.string.alerter_socket_connection_failed),
                                error = true,
                            )
                        }
                    }

                    true -> {
                        toggleLoading(false)
                        sessionStateHolder.currentServerUuid = uuid
                        setEffect {
                            LandingContract.Effect.Navigation.NavRoute(
                                route = NavGraph.HomeDest,
                                popUpToRoute = NavGraph.LandingDest.Landing::class,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun deleteServer(uuid: String) {
        analytics.logEvent(
            name = AnalyticsEvent.ButtonClick.name,
            params = mapOf(
                AnalyticsEvent.Param.ButtonAction.key to "deleteServer",
            ),
        )
        viewModelScope.launch {
            settingsRepo.deleteServer(uuid)
        }
    }

    private fun toggleLoading(loading: Boolean) {
        setState { copy(isLoading = loading) }
    }

    private fun buildUuidUrl(address: String): String {
        val safeAddress = address.let {
            it.ifBlank { createUrl(Constants.BASE_URL) }
        }
        return UrlBuilder.from(safeAddress).apply {
            addPath("api")
            addPath("get")
            addPath("uuid")
        }.build()
    }
}
