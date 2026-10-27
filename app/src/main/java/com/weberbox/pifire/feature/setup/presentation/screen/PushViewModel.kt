package com.weberbox.pifire.feature.setup.presentation.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.notification.OneSignalManager
import com.weberbox.pifire.core.repo.ConnectionRepo
import com.weberbox.pifire.core.repo.PushRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.setup.data.repo.SetupRepo
import com.weberbox.pifire.feature.setup.presentation.contract.PushContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushViewModel @Inject constructor(
    private val oneSignalManager: OneSignalManager,
    private val connectionRepo: ConnectionRepo,
    private val pushRepo: PushRepo,
    private val setupRepo: SetupRepo,
) : BaseViewModel<PushContract.Event, PushContract.State, PushContract.Effect>() {
    private var skipConsent by mutableStateOf(false)

    init {
        setConsentData()
        collectServerType()
    }

    override fun setInitialState() = PushContract.State(
        consent = false,
        isAppPush = false,
        isAppPushAllowed = false,
        isLoading = false,
    )

    override fun handleEvents(event: PushContract.Event) {
        when (event) {
            is PushContract.Event.ToggleConsent -> toggleConsent(event.enabled)

            is PushContract.Event.AllowAppPush -> allowAppPush()

            is PushContract.Event.NavigateToFinish -> {
                when {
                    !viewState.value.isAppPush -> checkNavigateToFinish()
                    viewState.value.isAppPushAllowed -> setEffect { PushContract.Effect.Navigation.Forward }
                    else -> skipAppPush()
                }
            }
        }
    }

    // The pager creates this page before Connect has connected, so follow the connection.
    private fun collectServerType() {
        viewModelScope.launch {
            connectionRepo.isApiV2Flow.collect { isApiV2 ->
                setState {
                    copy(
                        isAppPush = isApiV2 && pushRepo.isRelayConfigured,
                        isAppPushAllowed = false,
                    )
                }
            }
        }
    }

    private fun allowAppPush() {
        viewModelScope.launch {
            pushRepo.allowAppPush(setupRepo.getServerUuid())
            setState { copy(isAppPushAllowed = true) }
            setEffect { PushContract.Effect.Navigation.Forward }
        }
    }

    private fun skipAppPush() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = pushRepo.declineAppPush(setupRepo.getServerUuid())) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect { PushContract.Effect.Navigation.Forward }
                }

                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect { PushContract.Effect.Notification(text = result.error.asUiText(), error = true) }
                }
            }
        }
    }

    private fun setConsentData() {
        if (oneSignalManager.getUserConsent()) {
            setState {
                copy(
                    consent = true,
                )
            }
            skipConsent = true
        }
    }

    private fun toggleConsent(consent: Boolean) {
        oneSignalManager.provideUserConsent(consent)
        if (consent) {
            setEffect {
                PushContract.Effect.RequestPermission
            }
        }
        setState {
            copy(
                consent = consent,
            )
        }
        skipConsent = consent
    }

    private fun checkNavigateToFinish() {
        if (skipConsent) {
            setEffect {
                PushContract.Effect.Navigation.Forward
            }
        } else {
            setEffect {
                PushContract.Effect.Dialog(
                    dialogEvent = DialogEvent(
                        title = UiText(R.string.setup_push_dialog_declined_title),
                        message = UiText(R.string.setup_push_dialog_declined_message),
                        positiveAction = DialogAction(
                            buttonText = UiText(R.string.close),
                            action = {
                                skipConsent = true
                            },
                        ),
                    ),
                )
            }
        }
    }
}
