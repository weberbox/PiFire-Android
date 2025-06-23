package com.weberbox.pifire.common.presentation.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import com.weberbox.pifire.events.presentation.model.EventsData.Events
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("unused")
class SessionStateHolder {

    var currentServerUuid by mutableStateOf("")

    private val _isConnectedState = MutableStateFlow(false)
    val isConnectedState = _isConnectedState.asStateFlow()

    private val _pelletsDataState = MutableStateFlow<Pellets?>(null)
    val pelletsDataState: StateFlow<Pellets?> = _pelletsDataState.asStateFlow()

    private val _dashDataState = MutableStateFlow<Dash?>(null)
    val dashDataState: StateFlow<Dash?> = _dashDataState.asStateFlow()

    private val _eventsDataState = MutableStateFlow<Events?>(null)
    val eventsDataState: StateFlow<Events?> = _eventsDataState.asStateFlow()

    private val _settingsDataState = MutableStateFlow<Server?>(null)
    val settingsDataState: StateFlow<Server?> = _settingsDataState.asStateFlow()

    private val _recipesDataState = MutableStateFlow<Recipes?>(null)
    val recipesDataState: StateFlow<Recipes?> = _recipesDataState.asStateFlow()

    private val _infoDataState = MutableStateFlow<Info?>(null)
    val infoDataState: StateFlow<Info?> = _infoDataState.asStateFlow()

    fun clearSessionState() {
        currentServerUuid = ""
        _isConnectedState.value = false
        _pelletsDataState.value = null
        _dashDataState.value = null
        _eventsDataState.value = null
        _settingsDataState.value = null
        _recipesDataState.value = null
        _infoDataState.value = null
    }

    suspend fun setConnectedState(connected: Boolean) {
        _isConnectedState.emit(connected)
    }

    fun tryEmitConnectedState(connected: Boolean) {
        _isConnectedState.tryEmit(connected)
    }

    suspend fun setPelletsData(pellets: Pellets) {
        _pelletsDataState.emit(pellets)
    }

    fun tryEmitPelletsData(pellets: Pellets) {
        _pelletsDataState.tryEmit(pellets)
    }

    suspend fun setDashData(dash: Dash) {
        _dashDataState.emit(dash)
    }

    fun tryEmitDashData(dash: Dash) {
        _dashDataState.tryEmit(dash)
    }

    suspend fun setEventsData(events: Events) {
        _eventsDataState.emit(events)
    }

    fun tryEmitEventsData(events: Events) {
        _eventsDataState.tryEmit(events)
    }

    suspend fun setSettingsData(server: Server) {
        _settingsDataState.emit(server)
    }

    fun tryEmitSettingsData(server: Server) {
        _settingsDataState.tryEmit(server)
    }

    suspend fun setRecipesData(recipes: Recipes) {
        _recipesDataState.emit(recipes)
    }

    fun tryEmitRecipesData(recipes: Recipes) {
        _recipesDataState.tryEmit(recipes)
    }

    suspend fun setInfoData(info: Info) {
        _infoDataState.emit(info)
    }

    fun tryEmitInfoData(info: Info) {
        _infoDataState.tryEmit(info)
    }
}