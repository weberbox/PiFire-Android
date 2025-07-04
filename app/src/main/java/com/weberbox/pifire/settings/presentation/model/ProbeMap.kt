package com.weberbox.pifire.settings.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class ProbeMap(
    val probeDevices: List<ProbeDevice> = emptyList(),
    val probeInfo: List<ProbeInfo> = emptyList()
) {

    @Serializable
    data class ProbeInfo(
        val device: String = "",
        val enabled: Boolean = true,
        val label: String = "",
        val name: String = "",
        val port: String = "",
        val type: String = "",
        val profile: ProbeProfile = ProbeProfile()
    )

    @Serializable
    data class ProbeDevice(
        val device: String = "",
        val module: String = "",
        val ports: List<String> = emptyList()
    )
}
