package com.weberbox.pifire.feature.setup.presentation.screen

import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import com.weberbox.pifire.feature.setup.presentation.contract.SetupContract
import com.weberbox.pifire.navigation.NavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val serverDataCache: ServerDataCache,
) : BaseViewModel<SetupContract.Event, SetupContract.State, SetupContract.Effect>() {

    override fun setInitialState() = SetupContract.State

    override fun handleEvents(event: SetupContract.Event) {
        when (event) {
            SetupContract.Event.Exit -> exitSetup()
        }
    }

    private fun exitSetup() {
        serverDataCache.clearServerData()
        setEffect {
            SetupContract.Effect.Navigation.NavRoute(
                route = NavGraph.LandingDest.Landing(false),
                popUpToRoute = NavGraph.ServerSetup,
            )
        }
    }
}
