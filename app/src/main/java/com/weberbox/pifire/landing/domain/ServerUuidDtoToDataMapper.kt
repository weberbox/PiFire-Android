package com.weberbox.pifire.landing.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.landing.data.model.ServerUuidDto
import com.weberbox.pifire.landing.presentation.model.ServerUuidData

object ServerUuidDtoToDataMapper : Mapper<ServerUuidDto, ServerUuidData> {
    private val defaults = ServerUuidData()

    override fun map(from: ServerUuidDto) = ServerUuidData(
        uuid = from.uuid ?: defaults.uuid
    )
}