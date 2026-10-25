package com.weberbox.pifire.feature.info.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.feature.info.data.model.InfoDto
import com.weberbox.pifire.feature.info.presentation.model.InfoData.Info.Module

object PipListMapper : Mapper<List<InfoDto.Module>?, List<Module>> {

    override fun map(from: List<InfoDto.Module>?): List<Module> {
        return from?.map { item ->
            Module(
                name = item.name.orEmpty(),
                version = item.version.orEmpty()
            )
        } ?: emptyList()
    }
}