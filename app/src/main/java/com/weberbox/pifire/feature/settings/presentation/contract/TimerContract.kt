package com.weberbox.pifire.feature.settings.presentation.contract

import com.weberbox.pifire.core.model.settings.SettingsData
import com.weberbox.pifire.core.model.settings.SmartStart
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText

class TimerContract {

    sealed class Event : ViewEvent {
        data class SetShutdownDuration(val duration: Int) : Event()
        data class SetStartupDuration(val duration: Int) : Event()
        data class SetPrimeOnStartup(val amount: Int) : Event()
        data class SetStartupPWMDuty(val duty: Int) : Event()
        data class SetStartExitTemp(val temp: Int) : Event()
        data class SetAutoPowerOffEnabled(val enabled: Boolean) : Event()
        data class SetSmartStartEnabled(val enabled: Boolean) : Event()
        data class SetSmartStartExitTemp(val temp: Int) : Event()
        data class SetStartToMode(val mode: String) : Event()
        data class SetStartToModeTemp(val temp: Int) : Event()
        data class SetStartToHoldPrompt(val enabled: Boolean) : Event()
        data class DeleteSmartStartItem(val smartStartItems: List<SmartStart>) : Event()
        data class SetSmartStartItem(
            val index: Int,
            val temp: Int,
            val startUp: Int,
            val augerOn: Int,
            val pMode: Int,
            val smartStartItems: List<SmartStart>
        ) : Event()
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