package com.weberbox.pifire.feature.update.data.mapper

import com.weberbox.pifire.core.remoteconfig.AppUpdateConfigData
import com.weberbox.pifire.feature.update.data.model.AppUpdatePolicy

/**
 * The latest release newer than [currentVersionCode] (the alpha channel counts only for alpha
 * builds), or null when there is none. The update is required when [currentVersionCode] is below
 * a counted channel's minimum.
 */
fun AppUpdateConfigData.toUpdatePolicy(currentVersionCode: Int, includeAlpha: Boolean): AppUpdatePolicy? {
    val channels = listOfNotNull(stable, alpha.takeIf { includeAlpha })
    val latest = channels.maxByOrNull { it.latestVersionCode }
        ?.takeIf { it.latestVersionCode > currentVersionCode }
        ?: return null
    return AppUpdatePolicy(
        latestVersionCode = latest.latestVersionCode,
        isRequired = channels.any { it.minVersionCode > currentVersionCode },
        message = latest.message.ifBlank { null },
    )
}
