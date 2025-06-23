package com.weberbox.pifire.landing.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.util.CoroutinePoller
import com.weberbox.pifire.common.data.util.UrlBuilder
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.createUrl
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.landing.data.repo.LandingRepo
import com.weberbox.pifire.landing.presentation.contract.LandingContract
import com.weberbox.pifire.landing.presentation.model.ServerData
import com.weberbox.pifire.landing.presentation.model.ServerData.Server
import com.weberbox.pifire.landing.presentation.model.ServerUuidData
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.data.util.HeadersManager
import com.weberbox.pifire.settings.presentation.model.SettingsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val prefs: Prefs,
    private val landingRepo: LandingRepo,
    private val settingsRepo: SettingsRepo,
    private val socketManager: SocketManager,
    private val sessionStateHolder: SessionStateHolder,
    private val headersManager: HeadersManager,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<LandingContract.Event, LandingContract.State, LandingContract.Effect>() {
    private var pollerJobs = mutableListOf<Job>()
    private var autoSelectServer by mutableStateOf(prefs.get(Pref.landingAutoSelect))
    private var firstLaunch by mutableStateOf(true)

    init {
        firstLaunch = savedStateHandle.toRoute<NavGraph.LandingDest.Landing>().firstLaunch
        collectPrefsFlow()
        collectSettingsData()
    }

    override fun setInitialState() = LandingContract.State(
        serverData = ServerData(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: LandingContract.Event) {
        when (event) {
            is LandingContract.Event.Back -> setEffect { LandingContract.Effect.Navigation.Back }
            is LandingContract.Event.DeleteServer -> deleteServer(event.uuid)
            is LandingContract.Event.SelectServer -> selectServer(event.uuid)
        }
    }

    private fun collectPrefsFlow() {
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.landingAutoSelect).collect {
                autoSelectServer = it
            }
        }
    }

    private fun collectSettingsData() {
        viewModelScope.launch {
            settingsRepo.getSettingsFlow().collect {
                startServerPolling(it.serverMap)
                setState {
                    copy(
                        isInitialLoading = false,
                        serverData = ServerData(
                            servers = it.serverMap.entries.mapIndexed { index, server ->
                                Server(
                                    uuid = server.value.uuid,
                                    name = server.value.name.takeIf { name -> name.isNotEmpty() }
                                        ?: "Smoker ${index + 1}",
                                    address = server.value.address,
                                    online = false
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    private fun startServerPolling(servers: Map<String, SettingsData.Server>) {
        if (pollerJobs.isNotEmpty()) {
            pollerJobs.forEach { job ->
                job.cancel()
            }
        }
        servers.forEach { server ->
            val job = viewModelScope.launch {
                while (isActive) {
                    CoroutinePoller(
                        dispatcher = Dispatchers.IO,
                        fetchData = {
                            landingRepo.getServerUuid(
                                url = buildUuidUrl(server.value.address),
                                headers = headersManager.buildServerHeaders(server.value)
                            )
                        }
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
                val resultUuid = result.data.uuid
                setOnlineState(resultUuid, true)
                if (autoSelectServer && firstLaunch) {
                    if (resultUuid.isNotBlank()) {
                        viewModelScope.launch {
                            delay(500)
                            selectServer(uuid)
                        }
                    }
                }
            }

            is Result.Error -> {
                setOnlineState(uuid, false)
            }
        }
    }

    private fun setOnlineState(uuid: String?, online: Boolean) {
        val serverData = viewState.value.serverData
        val updatedServers = serverData.servers.map { server ->
            if (server.uuid == uuid && server.online != online) {
                server.copy(online = online)
            } else server
        }
        if (updatedServers != serverData.servers) {
            setState {
                copy(
                    serverData = serverData.copy(
                        servers = updatedServers
                    )
                )
            }
        }
    }

    private fun selectServer(uuid: String) {
        toggleLoading(true)
        viewModelScope.launch {
            settingsRepo.selectServer(uuid)?.let { server ->
                when (socketManager.initSocket(server)) {
                    false -> {
                        toggleLoading(false)
                        setEffect {
                            LandingContract.Effect.Notification(
                                text = UiText(R.string.socket_failed_connection),
                                error = true
                            )
                        }
                    }

                    true -> {
                        toggleLoading(false)
                        sessionStateHolder.currentServerUuid = uuid
                        setEffect {
                            LandingContract.Effect.Navigation.NavRoute(
                                route = NavGraph.HomeDest,
                                popUp = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun deleteServer(uuid: String) {
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