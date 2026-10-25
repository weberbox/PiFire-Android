package com.weberbox.pifire.feature.update.presentation.contract

import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.data.model.UpdateSource
import com.weberbox.pifire.feature.update.presentation.model.UpdateSheet

class AppUpdateContract {
    sealed class Event : ViewEvent {
        data object OnAppResumed : Event()
        data object OnUpdateClicked : Event()

        /** Restarts into a downloaded update. */
        data object OnInstallClicked : Event()

        /** "Later", "Hide" or "Close" on the sheet, or swiping it away. */
        data object OnSheetDismissed : Event()
        data object OnOpenStoreClicked : Event()
        data object OnCheckAgainClicked : Event()
    }

    data class State(
        /** A required release is newer than this version: the app is blocked until it updates. */
        val isRequired: Boolean = false,
        /** What this install can update to; null when there's nothing (or Play doesn't offer it yet). */
        val update: AvailableUpdate? = null,
        /** Release notes from Remote Config. */
        val message: String? = null,
        val source: UpdateSource = UpdateSource.PLAY_STORE,
        val progress: InstallProgress = InstallProgress.Idle,
        /** Null when the sheet is closed; never shown while [isRequired]. */
        val sheet: UpdateSheet? = null,
        /** The user hid the download's progress; it's still shown once it's ready. */
        val isDownloadHidden: Boolean = false,
        val isChecking: Boolean = false,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        /** The app's Google Play listing, when Play can't update it in the app. */
        data object OpenStoreListing : Effect()

        /** The GitHub releases page, where the `github` flavor's APK is downloaded. */
        data object OpenReleasePage : Effect()
    }
}
