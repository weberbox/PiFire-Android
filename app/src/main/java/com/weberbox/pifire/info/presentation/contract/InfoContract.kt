package com.weberbox.pifire.info.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.info.presentation.model.Licenses

class InfoContract {

    sealed class Event : ViewEvent {
        data object Refresh: Event()
        data object PipModulesViewAll: Event()
        data object BackButtonClicked : Event()
    }

    data class State(
        val info: Info,
        val licenseData: Licenses,
        val isInfoLoading: Boolean,
        val isLicencesLoading: Boolean,
        val isRefreshing: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object LicenseDetails : Navigation()
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }
    }
}