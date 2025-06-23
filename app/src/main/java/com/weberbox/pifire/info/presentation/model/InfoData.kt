package com.weberbox.pifire.info.presentation.model

import androidx.compose.runtime.Immutable
import com.weberbox.pifire.common.domain.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class InfoData(
    val infoMap: Map<Uuid, Info> = emptyMap()
) {
    @Immutable
    @Serializable
    data class Info (
        val uuid: String = "",
        val cpuInfo: String = "",
        val networkInfo: String = "",
        val uptime: String = "",
        val cpuTemp: String = "",
        val outPins: List<GPIOInOutData> = emptyList(),
        val inPins: List<GPIOInOutData> = emptyList(),
        val devPins: List<GPIODeviceData> = emptyList(),
        val platform: String = "",
        val display: String = "",
        val distance: String = "",
        val serverVersion: String = "",
        val serverBuild: String = "",
        val appVersion: String = "",
        val appVersionCode: String = "",
        val appBuildType: String = "",
        val appFlavor: String = "",
        val appBuildDate: String = "",
        val appGitBranch: String = "",
        val appGitRev: String = ""
    )
}