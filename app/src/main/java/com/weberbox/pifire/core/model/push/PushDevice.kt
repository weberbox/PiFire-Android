package com.weberbox.pifire.core.model.push

/** A phone registered for app push on the current PiFire server. */
data class PushDevice(
    val id: String,
    val deviceName: String,
    val appVersion: String,
    /** Epoch seconds, 0 if unknown. */
    val registered: Long,
    val isThisPhone: Boolean,
)
