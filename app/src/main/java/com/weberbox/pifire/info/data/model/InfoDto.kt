package com.weberbox.pifire.info.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InfoDto(
    val uuid: String? = null,
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
    data class Module(
        val name: String? = null,
        val version: String? = null
    )
}