package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CycleData(
    @SerialName("HoldCycleTime")
    val holdCycleTime: Int? = null,

    @SerialName("SmokeOnCycleTime")
    val smokeOnCycleTime: Int? = null,

    @SerialName("SmokeOffCycleTime")
    val smokeOffCycleTime: Int? = null,

    @SerialName("PMode")
    val pMode: Int? = null,

    @SerialName("u_min")
    val uMin: Double? = null,

    @SerialName("u_max")
    val uMax: Double? = null,

    @SerialName("LidOpenDetectEnabled")
    val lidOpenDetectEnabled: Boolean? = null,

    @SerialName("LidOpenThreshold")
    val lidOpenThreshold: Int? = null,

    @SerialName("LidOpenPauseTime")
    val lidOpenPauseTime: Int? = null

)
