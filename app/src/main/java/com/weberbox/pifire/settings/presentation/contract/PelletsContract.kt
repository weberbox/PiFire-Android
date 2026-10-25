package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.settings.presentation.model.SettingsData

class PelletsContract {

    sealed class Event : ViewEvent {
        data class SetWarningEnabled(val enabled: Boolean) : Event()
        data class SetPrimeIgnition(val enabled: Boolean) : Event()
        data class SetWarningTime(val time: Int) : Event()
        data class SetWarningLevel(val level: Int) : Event()
        data class SetFullLevel(val level: Int) : Event()
        data class SetEmptyLevel(val level: Int) : Event()
        data class SetAugerRate(val rate: Double) : Event()
    }

    data class State(
        val serverData: SettingsData.Server,
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