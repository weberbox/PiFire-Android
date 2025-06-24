package com.weberbox.pifire.setup.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.core.util.OneSignalManager
import com.weberbox.pifire.setup.presentation.contract.PushContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PushViewModel @Inject constructor(
    private val oneSignalManager: OneSignalManager
): BaseViewModel<PushContract.Event, PushContract.State, PushContract.Effect>() {
    private var skipConsent by mutableStateOf(false)

    init {
        setConsentData()
    }

    override fun setInitialState() = PushContract.State(
        consent = false
    )

    override fun handleEvents(event: PushContract.Event) {
        when (event) {
            is PushContract.Event.ToggleConsent -> toggleConsent(event.enabled)
            is PushContract.Event.NavigateToFinish -> checkNavigateToFinish()
        }
    }

    private fun setConsentData() {
        if (oneSignalManager.getUserConsent()) {
            setState {
                copy(
                    consent = true
                )
            }
            skipConsent = true
        }
    }

    private fun toggleConsent(consent: Boolean) {
        oneSignalManager.provideUserConsent(consent)
        if (consent) {
            oneSignalManager.promptForPushNotifications()
        }
        setState {
            copy(
                consent = consent
            )
        }
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
                            }
                        )
                    )
                )
            }
        }
    }
}