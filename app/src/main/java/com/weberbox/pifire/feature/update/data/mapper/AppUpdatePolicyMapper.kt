package com.weberbox.pifire.feature.update.data.mapper

import com.weberbox.pifire.core.remoteconfig.AppUpdateConfigData
import com.weberbox.pifire.core.remoteconfig.UpdateType
import com.weberbox.pifire.feature.update.data.model.AppUpdatePolicy

/**
 * The releases newer than [currentVersionCode] (alpha ones only for alpha builds), or null when
 * there are none. Any IMMEDIATE one among them makes the update required, even when a newer
 * FLEXIBLE release followed it.
 */
fun List<AppUpdateConfigData>.toUpdatePolicy(currentVersionCode: Int, includeAlpha: Boolean): AppUpdatePolicy? {
    val newer = filter { it.appVersionCode > currentVersionCode && (includeAlpha || !it.isAlpha) }
    val latest = newer.maxByOrNull { it.appVersionCode } ?: return null
    return AppUpdatePolicy(
        latestVersionCode = latest.appVersionCode,
        isRequired = newer.any { it.type == UpdateType.IMMEDIATE },
        message = latest.message.ifBlank { null },
    )
}
