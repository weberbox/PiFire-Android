package com.weberbox.pifire.feature.dashboard.data.mapper

import com.weberbox.pifire.core.network.model.PostDto.NotifyDto
import com.weberbox.pifire.feature.dashboard.data.model.ProbeNotify

internal fun ProbeNotify.toDto() = NotifyDto(
    label = label,
    targetTemp = targetTemp,
    lowLimitTemp = lowLimitTemp,
    highLimitTemp = highLimitTemp,
    lowLimitReq = lowLimitReq,
    highLimitReq = highLimitReq,
    targetReq = targetReq,
    highLimitShutdown = highLimitShutdown,
    lowLimitShutdown = lowLimitShutdown,
    lowLimitReignite = lowLimitReignite,
    targetShutdown = targetShutdown,
    targetKeepWarm = targetKeepWarm,
)
