package com.weberbox.pifire.feature.info.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.model.info.InfoData.Info.Module
import com.weberbox.pifire.feature.info.data.model.InfoDto

object PipListMapper : Mapper<List<InfoDto.Module>?, List<Module>> {

    override fun map(from: List<InfoDto.Module>?): List<Module> = from?.map { item ->
        Module(
            name = item.name.orEmpty(),
            version = item.version.orEmpty(),
        )
    } ?: emptyList()
}
