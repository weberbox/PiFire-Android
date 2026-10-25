package com.weberbox.pifire.feature.setup.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState

class SetupContract {

    sealed class Event : ViewEvent {
        /** Leave setup, cancelled or finished, back to the server list. */
        data object Exit : Event()
    }

    data object State : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
            data class NavRoute(
                val route: Any,
                val popUpToRoute: Any? = null,
                val inclusive: Boolean = true,
            ) : Navigation()
        }
    }
}
