package com.weberbox.pifire.feature.update.data.model

import com.weberbox.pifire.core.ui.util.UiText

/** Where this build gets its updates. */
enum class UpdateSource {
    /** The `playstore` flavor: Google Play's in-app updates. */
    PLAY_STORE,

    /** The `github` flavor: the user downloads the new APK from the GitHub releases page. */
    GITHUB,
}

/** What Remote Config says about versions newer than this one. */
data class AppUpdatePolicy(
    val latestVersionCode: Int,
    /** This version is below the minimum Remote Config allows: the app is blocked until it updates. */
    val isRequired: Boolean,
    /** Release notes of the latest version, from Remote Config. */
    val message: String?,
)

/** A newer version this install can update to. */
data class AvailableUpdate(
    val versionCode: Int,
    /** Null when the source doesn't say (GitHub, or Google Play before the download is known). */
    val sizeBytes: Long? = null,
    /** Google Play: an immediate update was started and interrupted; it should be resumed. */
    val isInProgress: Boolean = false,
)

/** An update started from the app (Google Play only; GitHub updates happen in the browser). */
sealed interface InstallProgress {
    data object Idle : InstallProgress

    /** [fraction] is 0 to 1, or null while the size isn't known. */
    data class Downloading(val fraction: Float?) : InstallProgress

    /** Downloaded in the background; finishing it restarts the app. */
    data object Downloaded : InstallProgress

    data object Installing : InstallProgress

    data class Failed(val message: UiText) : InstallProgress
}
