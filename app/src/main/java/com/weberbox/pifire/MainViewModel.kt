package com.weberbox.pifire

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.contract.MainContract
import com.weberbox.pifire.common.presentation.feature.FeatureSupport
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.dashboard.data.repo.DashRepo
import com.weberbox.pifire.events.data.repo.EventsRepo
import com.weberbox.pifire.pellets.data.repo.PelletsRepo
import com.weberbox.pifire.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val savedStateHandle: SavedStateHandle,
    private val socketManager: SocketManager,
    private val pelletsRepo: PelletsRepo,
    private val settingsRepo: SettingsRepo,
    private val dashRepo: DashRepo,
    private val eventsRepo: EventsRepo,
    private val recipesRepo: RecipesRepo,
    private val prefs: Prefs
) : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    init {
        checkForAppUpdates()
        collectPrefsFlow()
        collectSessionState()
        checkProcessKilled()
    }

    override fun setInitialState() = MainContract.State(
        appTheme = AppTheme.System,
        dynamicColor = false,
        featureSupport = FeatureSupport()
    )

    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            MainContract.Event.StoreLatestDataState -> storeLatestDataState()
        }
    }

    private fun checkForAppUpdates() {
        setEffect {
            MainContract.Effect.CheckForAppUpdates
        }
    }

    private fun collectPrefsFlow() {
        collectAndUpdateState(Pref.appTheme) { copy(appTheme = it) }
        collectAndUpdateState(Pref.dynamicColor) { copy(dynamicColor = it) }
    }

    private fun collectSessionState() {
        viewModelScope.launch {
            sessionStateHolder.settingsDataState.collect { settingsData ->
                settingsData?.also { data ->
                    setState {
                        copy(
                            featureSupport = FeatureSupport(
                                currentVersion = data.settings.serverVersion,
                                currentBuild = data.settings.serverBuild
                            )
                        )
                    }
                }
            }
        }
    }

    private fun <T> collectAndUpdateState(
        pref: Pref<T>,
        update: MainContract.State.(T) -> MainContract.State
    ) {
        viewModelScope.launch {
            prefs.collectPrefsFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }

    private fun checkProcessKilled() {
        savedStateHandle.get<Boolean>("restored")?.also { restored ->
            // If restored exists that should mean the process was killed by the system so we
            // need to signOut as the socket will be dead and uiState will be incorrect
            // we could restart the socket but it is probably better to just sign back in
            if (restored) signOut()
        }
        savedStateHandle["restored"] = true
    }

    private fun signOut() {
        socketManager.disconnect()
        sessionStateHolder.clearSessionState()
        setEffect {
            MainContract.Effect.Navigation.NavRoute(
                route = NavGraph.LandingDest.Landing(false),
                popUp = true
            )
        }
    }

    private fun storeLatestDataState() {
        viewModelScope.launch {
            sessionStateHolder.pelletsDataState.value?.also { pelletsData ->
                pelletsRepo.updatePelletsData(pelletsData)
            }
            sessionStateHolder.dashDataState.value?.also { dashData ->
                dashRepo.updateDashData(dashData)
            }
            sessionStateHolder.eventsDataState.value?.also { eventsData ->
                eventsRepo.updateEventsData(eventsData)
            }
            sessionStateHolder.recipesDataState.value?.also { recipesData ->
                recipesRepo.updateRecipesData(recipesData)
            }
            sessionStateHolder.settingsDataState.value?.also { settingsData ->
                settingsRepo.updateServerSettings(settingsData)
            }
        }
    }
}