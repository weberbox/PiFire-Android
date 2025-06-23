package com.weberbox.pifire

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.contract.MainContract
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.core.singleton.Prefs
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
    }

    override fun setInitialState() = MainContract.State(
        appTheme = AppTheme.System,
        dynamicColor = false
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
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.appTheme).collect { appTheme ->
                setState {
                    copy(
                        appTheme = appTheme
                    )
                }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.dynamicColor).collect { dynamicColor ->
                setState {
                    copy(
                        dynamicColor = dynamicColor
                    )
                }
            }
        }
    }

    private fun storeLatestDataState() {
        viewModelScope.launch {
            val pelletsData = sessionStateHolder.pelletsDataState.value
            val dashData = sessionStateHolder.dashDataState.value
            val eventsData = sessionStateHolder.eventsDataState.value
            val recipesData = sessionStateHolder.recipesDataState.value
            val settingsData = sessionStateHolder.settingsDataState.value

            pelletsData?.also {
                pelletsRepo.updatePelletsData(pelletsData)
            }
            dashData?.also {
                dashRepo.updateDashData(dashData)
            }
            eventsData?.also {
                eventsRepo.updateEventsData(eventsData)
            }
            recipesData?.also {
                recipesRepo.updateRecipesData(recipesData)
            }
            settingsData?.also {
                settingsRepo.updateServerSettings(settingsData)
            }
        }
    }
}