package com.weberbox.pifire.feature.update.presentation.model

/** What the update sheet shows when an update isn't required. */
enum class UpdateSheet {
    /** A newer version: update or put it off. */
    OFFER,
    DOWNLOADING,

    /** Downloaded: restart to finish. */
    READY,
    FAILED,
}
