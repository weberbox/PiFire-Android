package com.weberbox.pifire.feature.settings.presentation.screen.notifications

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.repo.PushRepo
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.settings.presentation.contract.AppPushContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppPushViewModel @Inject constructor(
    private val pushRepo: PushRepo,
    private val settingsRepo: SettingsRepo,
) : BaseViewModel<AppPushContract.Event, AppPushContract.State, AppPushContract.Effect>() {

    init {
        loadThisPhone()
        loadDevices()
    }

    override fun setInitialState() = AppPushContract.State(
        devices = emptyList(),
        isThisPhoneOn = false,
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
    )

    override fun handleEvents(event: AppPushContract.Event) {
        when (event) {
            is AppPushContract.Event.Refresh -> loadDevices()

            is AppPushContract.Event.DeleteDevice -> {
                // Removing this phone is turning it off, or Home would register it again on the next start.
                if (viewState.value.devices.any { it.id == event.id && it.isThisPhone }) turnOffThisPhone() else deleteDevice(event.id)
            }

            is AppPushContract.Event.SetThisPhoneOn -> if (event.isOn) turnOnThisPhone() else turnOffThisPhone()
        }
    }

    private fun loadDevices() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = pushRepo.getServerDevices()) {
                is Result.Success -> setState {
                    copy(
                        devices = result.data,
                        isInitialLoading = false,
                        isLoading = false,
                        isDataError = false,
                    )
                }

                is Result.Error -> {
                    // A failed refresh keeps the list on screen; only a failed first load is an error page.
                    setState {
                        copy(
                            isDataError = isInitialLoading,
                            isInitialLoading = false,
                            isLoading = false,
                        )
                    }
                    setEffect { AppPushContract.Effect.Notification(text = result.error.asUiText(), error = true) }
                }
            }
        }
    }

    private fun loadThisPhone() {
        viewModelScope.launch {
            val serverUuid = settingsRepo.getCurrentServer()?.uuid ?: return@launch
            val isOn = pushRepo.isAppPushOn(serverUuid)
            setState { copy(isThisPhoneOn = isOn) }
        }
    }

    private fun turnOnThisPhone() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val serverUuid = settingsRepo.getCurrentServer()?.uuid
            if (serverUuid == null) {
                setState { copy(isLoading = false) }
                return@launch
            }
            pushRepo.allowAppPush(serverUuid)
            setState { copy(isThisPhoneOn = true) }
            when (val result = pushRepo.registerWithServer()) {
                is Result.Success -> loadDevices()

                // Stays on: Home tries registering again on the next start.
                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AppPushContract.Effect.Notification(text = result.error.asUiText(), error = true) }
                }
            }
        }
    }

    private fun turnOffThisPhone() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val serverUuid = settingsRepo.getCurrentServer()?.uuid
            if (serverUuid == null) {
                setState { copy(isLoading = false) }
                return@launch
            }
            when (val result = pushRepo.declineAppPush(serverUuid)) {
                is Result.Success -> {
                    setState { copy(isThisPhoneOn = false) }
                    loadDevices()
                }

                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AppPushContract.Effect.Notification(text = result.error.asUiText(), error = true) }
                }
            }
        }
    }

    private fun deleteDevice(id: String) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = pushRepo.removeServerDevice(id)) {
                is Result.Success -> {
                    setEffect { AppPushContract.Effect.Notification(text = UiText(resId = R.string.settings_app_push_deleted)) }
                    loadDevices()
                }

                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { AppPushContract.Effect.Notification(text = result.error.asUiText(), error = true) }
                }
            }
        }
    }
}
