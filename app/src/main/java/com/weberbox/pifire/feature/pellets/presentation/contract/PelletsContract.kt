package com.weberbox.pifire.feature.pellets.presentation.contract

import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.pellets.presentation.model.PelletsEvent

class PelletsContract {

    sealed class Event : ViewEvent {
        data object Refresh: Event()
        data object AddProfileDialog : Event()
        data object AddWoodDialog : Event()
        data object AddBrandDialog : Event()
        data object BrandsViewAll : Event()
        data object WoodsViewAll : Event()
        data object ProfilesViewAll : Event()
        data object LogsViewAll : Event()
        data class SendEvent(val event: PelletsEvent): Event()
        data class CurrentDialog(val currentPelletId: String) : Event()
        data class DeleteBrandDialog(val brand: String) : Event()
        data class DeleteWoodDialog(val wood: String) : Event()
        data class DeleteProfileDialog(val profile: PelletProfile) : Event()
        data class EditProfileDialog(val profile: PelletProfile) : Event()
        data class DeleteLogDialog(val log: PelletLog) : Event()
    }

    data class State(
        val pellets: Pellets,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
        val isDataError: Boolean,
        val isRefreshing: Boolean,
        val isConnected: Boolean,
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