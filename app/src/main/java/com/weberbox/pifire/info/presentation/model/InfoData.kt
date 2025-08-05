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
        val platformInfo: PlatformInfo = PlatformInfo(),
        val osInfo: OsInfo = OsInfo(),
        val networkMap: Map<String, NetworkInfo> = emptyMap(),
        val cpuInfo: String = "",
        val networkInfo: String = "",
        val wifiQualityValue: String = "Unknown",
        val wifiQualityMax: String = "Unknown",
        val wifiQualityPercentage: String = "Unknown",
        val uptime: String = "",
        val cpuTemp: String = "",
        val cpuThrottled: Boolean = false,
        val cpuUnderVolt: Boolean = false,
        val outPins: List<GPIOInOutData> = emptyList(),
        val inPins: List<GPIOInOutData> = emptyList(),
        val devPins: List<GPIODeviceData> = emptyList(),
        val pipList: List<Module> = emptyList(),
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
        val appGitRev: String = "",
        val alphaBuild: Boolean = false,
        val devBuild: Boolean = false
    ) {
        @Serializable
        data class PlatformInfo(
            val systemModel: String = "Unknown",
            val cpuModel: String = "Unknown",
            val cpuHardware: String = "Unknown",
            val cpuCores: String = "Unknown",
            val cpuFrequency: String = "Unknown",
            val totalRam: String = "Unknown",
            val availableRam: String = "Unknown"
        )

        @Serializable
        data class OsInfo(
            val prettyName: String = "Unknown",
            val version: String = "Unknown",
            val codeName: String = "Unknown",
            val architecture: String = "Unknown"
        )

        @Serializable
        data class NetworkInfo(
            val ipAddress: String = "Unknown",
            val macAddress: String = "Unknown"
        )

        @Serializable
        data class Module(
            val name: String = "",
            val version: String = ""
        )
    }
}