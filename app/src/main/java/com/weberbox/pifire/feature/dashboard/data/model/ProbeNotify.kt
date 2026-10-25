package com.weberbox.pifire.feature.dashboard.data.model

/** A probe's notification settings; null fields are left unchanged on the server. */
data class ProbeNotify(
    val label: String,
    val targetTemp: Int? = null,
    val lowLimitTemp: Int? = null,
    val highLimitTemp: Int? = null,
    val lowLimitReq: Boolean? = null,
    val highLimitReq: Boolean? = null,
    val targetReq: Boolean? = null,
    val highLimitShutdown: Boolean? = null,
    val lowLimitShutdown: Boolean? = null,
    val lowLimitReignite: Boolean? = null,
    val targetShutdown: Boolean? = null,
    val targetKeepWarm: Boolean? = null,
)
