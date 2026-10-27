package com.weberbox.pifire.core.ui.components.message

/** Implemented by the activity whose content holds the [MessageHost], so `showMessage` finds it. */
interface MessageHostOwner {
    val messageHost: MessageHostState
}
