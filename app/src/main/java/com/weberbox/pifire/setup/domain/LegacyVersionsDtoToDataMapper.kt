package com.weberbox.pifire.setup.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.core.annotations.Compatibility
import com.weberbox.pifire.setup.data.model.LegacyVersionsDto
import com.weberbox.pifire.setup.presentation.model.VersionsData

@Compatibility(versionBelow = "1.10.0", build = "25")
object LegacyVersionsDtoToDataMapper : Mapper<LegacyVersionsDto, VersionsData> {
    private val defaults = VersionsData()

    override fun map(from: LegacyVersionsDto) = VersionsData(
        version = from.settings?.versions?.server ?: defaults.version,
        build = from.settings?.versions?.build ?: defaults.build
    )
}