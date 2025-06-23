package com.weberbox.pifire.pellets.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.pellets.presentation.model.PelletsEvent

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
    }
}