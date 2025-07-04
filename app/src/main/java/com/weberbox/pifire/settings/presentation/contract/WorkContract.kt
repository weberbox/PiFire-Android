package com.weberbox.pifire.settings.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server

class WorkContract {

    sealed class Event : ViewEvent {
        data class SetAugerOnTime(val time: Int) : Event()
        data class SetAugerOffTime(val time: Int) : Event()
        data class SetPMode(val mode: Int) : Event()
        data class SetSPlusEnabled(val enabled: Boolean) : Event()
        data class SetFanRampEnabled(val enabled: Boolean) : Event()
        data class SetFanRampDutyCycle(val dutyCycle: Int) : Event()
        data class SetFanOnTime(val time: Int) : Event()
        data class SetFanOffTime(val time: Int) : Event()
        data class SetMinTemp(val temp: Int) : Event()
        data class SetMaxTemp(val temp: Int) : Event()
        data class SetLidOpenDetectEnabled(val enabled: Boolean) : Event()
        data class SetLidOpenThresh(val thresh: Int) : Event()
        data class SetLidOpenPauseTime(val time: Int) : Event()
        data class SetKeepWarmEnabled(val enabled: Boolean) : Event()
        data class SetKeepWarmTemp(val temp: Int) : Event()
        data class SetCntrlrSelected(val selected: String) : Event()
        data class SetCntrlrPidPb(val pb: Double) : Event()
        data class SetCntrlrPidTd(val td: Double) : Event()
        data class SetCntrlrPidTi(val ti: Double) : Event()
        data class SetCntrlrPidCenter(val center: Double) : Event()
        data class SetCntrlrPidAcPb(val pb: Double) : Event()
        data class SetCntrlrPidAcTd(val td: Double) : Event()
        data class SetCntrlrPidAcTi(val ti: Double) : Event()
        data class SetCntrlrPidAcStable(val stable: Int) : Event()
        data class SetCntrlrPidAcCenter(val center: Double) : Event()
        data class SetCntrlrPidSpPb(val pb: Double) : Event()
        data class SetCntrlrPidSpTd(val td: Double) : Event()
        data class SetCntrlrPidSpTi(val ti: Double) : Event()
        data class SetCntrlrPidSpStable(val stable: Int) : Event()
        data class SetCntrlrPidSpCenter(val center: Double) : Event()
        data class SetCntrlrPidSpTau(val tau: Int) : Event()
        data class SetCntrlrPidSpTheta(val theta: Int) : Event()
        data class SetCntrlrTime(val time: Int) : Event()
        data class SetCntrlruMin(val uMin: Double) : Event()
        data class SetCntrlruMax(val uMax: Double) : Event()
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