package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.data.model.PostDto.ProbesDto
import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server

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
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }
    }
}