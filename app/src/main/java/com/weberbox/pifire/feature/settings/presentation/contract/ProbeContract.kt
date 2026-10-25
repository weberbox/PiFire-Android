package com.weberbox.pifire.feature.settings.presentation.contract

import com.weberbox.pifire.core.network.model.PostDto.ProbesDto
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.settings.presentation.model.SettingsData.Server

class ProbeContract {

    sealed class Event : ViewEvent {
        data class UpdateProbe(val probeDto: ProbesDto) : Event()
        data class SetTempUnits(val units: String) : Event()
    }

    data class State(
        val serverData: Server,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data class NavRoute(
                val route: Any,
                val popUpToRoute: Any? = null,
                val inclusive: Boolean = true,
            ) : Navigation()
        }
    }
}