package com.weberbox.pifire.application

import com.weberbox.pifire.core.remoteconfig.FeatureSupport
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.model.AppTheme
import com.weberbox.pifire.core.ui.util.UiText

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
            data class NavRoute(
                val route: Any,
                val popUpToRoute: Any? = null,
                val inclusive: Boolean = true,
            ) : Navigation()
        }

    }
}