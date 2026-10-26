package com.weberbox.pifire.core.remoteconfig

import kotlinx.serialization.Serializable

/** One release in Remote Config `app_update_config`. */
@Serializable
data class AppUpdateConfigData(
    val appVersionCode: Int,
    val type: UpdateType,
    val message: String = "",
    val isAlpha: Boolean = false,
)

@Serializable
enum class UpdateType {
    /** Versions below this release can't be used until they update. */
    IMMEDIATE,
    FLEXIBLE,
}
