package com.weberbox.pifire.common.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.feature.FeatureSupport
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.common.presentation.util.UiText

class MainContract {

    sealed class Event : ViewEvent {
        data object StoreLatestDataState : Event()
    }

    data class State(
        val appTheme: AppTheme,
        val dynamicColor: Boolean,
        val featureSupport: FeatureSupport
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object CheckForAppUpdates : Effect()
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }

    }
}