package com.weberbox.pifire.info.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InfoDto(
    val uuid: String? = null,
    val platformInfo: PlatformInfo? = null,
    val osInfo: OsInfo? = null,
    val networkInfo: Map<String, NetworkInfo>? = null,
    val cpuThrottled: Boolean? = null,
    val cpuUnderVolt: Boolean? = null,
    val wifiQualityValue: Int? = null,
    val wifiQualityMax: Int? = null,
    val wifiQualityPercentage: Double? = null,
    val cpuInfo: List<String>? = null,
    val ifConfig: List<String>? = null,
    val cpuTemp: String? = null,
    val upTime: String? = null,
    val outPins: Map<String, Int?>? = null,
    val inPins: Map<String, Int?>? = null,
    val devPins: Map<String, Map<String, Int>>? = null,
    val serverVersion: String? = null,
    val serverBuild: String? = null,
    val pipList: List<Module>? = null,
    val platform: String? = null,
    val display: String? = null,
    val distance: String? = null,
    val dcFan: Boolean? = null
) {
    @Serializable
    data class PlatformInfo(
        val systemModel: String? = null,
        val cpuModel: String? = null,
        val cpuHardware: String? = null,
        val cpuCores: Int? = null,
        val cpuFrequency: Double? = null,
        val totalRam: Long? = null,
        val availableRam: Long? = null
    )

    @Serializable
    data class OsInfo(
        val prettyName: String? = null,
        val version: String? = null,
        val codeName: String? = null,
        val architecture: String? = null
    )

    @Serializable
    data class NetworkInfo(
        @SerialName("ip_address")
        val ipAddress: String? = null,

        @SerialName("mac_address")
        val macAddress: String? = null
    )

    @Serializable
    data class Module(
        val name: String? = null,
        val version: String? = null
    )
}