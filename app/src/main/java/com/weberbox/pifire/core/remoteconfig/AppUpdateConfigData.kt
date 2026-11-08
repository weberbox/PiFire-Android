package com.weberbox.pifire.core.remoteconfig

import kotlinx.serialization.Serializable

/** Remote Config `app_update_config_v2`. */
@Serializable
data class AppUpdateConfigData(
    val stable: AppUpdateChannelData? = null,
    /** Offered to alpha builds only, alongside [stable]. */
    val alpha: AppUpdateChannelData? = null,
)

@Serializable
data class AppUpdateChannelData(
    val latestVersionCode: Int = 0,
    /** Versions below this can't be used until they update. */
    val minVersionCode: Int = 0,
    /** Release notes of [latestVersionCode]. */
    val message: String = "",
)
