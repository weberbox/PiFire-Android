package com.weberbox.pifire.setup.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.setup.data.model.VersionsDto
import com.weberbox.pifire.setup.presentation.model.VersionsData

object VersionsDtoToDataMapper : Mapper<VersionsDto, VersionsData> {
    private val defaults = VersionsData()

    override fun map(from: VersionsDto) = VersionsData(
        version = from.version ?: defaults.version,
        build = from.build ?: defaults.build
    )
}