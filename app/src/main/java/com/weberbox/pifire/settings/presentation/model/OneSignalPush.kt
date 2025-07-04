package com.weberbox.pifire.settings.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class OneSignalPush(
    val enabled: Boolean = false,
    val uuid: String = "",
    val appID: String = "",
    val devices: Map<String, OneSignalDeviceInfo> = emptyMap()
) {

    @Serializable
    data class OneSignalDeviceInfo(
        val deviceName: String = "",
        val friendlyName: String = "",
        val appVersion: String = ""
    )
}
