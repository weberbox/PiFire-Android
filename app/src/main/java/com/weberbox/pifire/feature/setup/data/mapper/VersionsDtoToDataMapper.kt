package com.weberbox.pifire.feature.setup.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.feature.setup.data.model.VersionsData
import com.weberbox.pifire.feature.setup.data.model.VersionsDto

object VersionsDtoToDataMapper : Mapper<VersionsDto, VersionsData> {
    private val defaults = VersionsData()

    override fun map(from: VersionsDto) = VersionsData(
        version = from.version ?: defaults.version,
        build = from.build ?: defaults.build,
    )
}
