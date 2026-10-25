package com.weberbox.pifire.application

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.remoteconfig.FeatureSupport
import com.weberbox.pifire.core.remoteconfig.RemoteConfigRepository
import com.weberbox.pifire.core.repo.ConnectionRepo
import com.weberbox.pifire.core.repo.PrefsRepo
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.model.AppTheme
import com.weberbox.pifire.feature.dashboard.data.repo.DashRepo
import com.weberbox.pifire.feature.events.data.repo.EventsRepo
import com.weberbox.pifire.feature.pellets.data.repo.PelletsRepo
import com.weberbox.pifire.feature.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.navigation.NavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val sessionStateHolder: SessionStateHolder,
    private val savedStateHandle: SavedStateHandle,
    private val connectionRepo: ConnectionRepo,
    private val pelletsRepo: PelletsRepo,
    private val settingsRepo: SettingsRepo,
    private val dashRepo: DashRepo,
    private val eventsRepo: EventsRepo,
    private val recipesRepo: RecipesRepo,
    private val prefsRepo: PrefsRepo
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
        // Feature config is fetched once; settings emit on every server update, so only rebuild
        // FeatureSupport when the server version or build changes.
        val serverVersion = sessionStateHolder.settingsDataState
            .filterNotNull()
            .map { data -> data.settings.serverVersion to data.settings.serverBuild }
            .distinctUntilChanged()
        val featureMap = flow { emit(remoteConfigRepository.fetchFeatureSupportConfig()) }

        viewModelScope.launch {
            combine(serverVersion, featureMap) { (version, build), features ->
                FeatureSupport(
                    currentVersion = version,
                    currentBuild = build,
                    featureMap = features,
                )
            }.collect { featureSupport ->
                setState { copy(featureSupport = featureSupport) }
            }
        }
    }

    private fun <T> collectAndUpdateState(
        pref: Pref<T>,
        update: MainContract.State.(T) -> MainContract.State
    ) {
        viewModelScope.launch {
            prefsRepo.prefFlow(pref).collect { value ->
                setState { update(value) }
            }
        }
    }

    private fun checkProcessKilled() {
        savedStateHandle.get<Boolean>("restored")?.also { restored ->
            // If restored exists that should mean the process was killed by the system so we
            // need to signOut as the socket will be dead and uiState will be incorrect
            // we could restart the socket, but it is probably better to just sign back in
            if (restored) signOut()
        }
        savedStateHandle["restored"] = true
    }

    private fun signOut() {
        connectionRepo.disconnect()
        sessionStateHolder.clearSessionState()
        setEffect {
            MainContract.Effect.Navigation.NavRoute(
                route = NavGraph.LandingDest.Landing(false),
                popUpToRoute = NavGraph.HomeDest.DashHome
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