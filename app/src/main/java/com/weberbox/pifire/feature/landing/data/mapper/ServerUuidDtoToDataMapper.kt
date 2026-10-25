package com.weberbox.pifire.feature.landing.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.feature.landing.data.model.ServerUuidData
import com.weberbox.pifire.feature.landing.data.model.ServerUuidDto

object ServerUuidDtoToDataMapper : Mapper<ServerUuidDto, ServerUuidData> {
    private val defaults = ServerUuidData()

    override fun map(from: ServerUuidDto) = ServerUuidData(
        uuid = from.uuid ?: defaults.uuid
    )
}