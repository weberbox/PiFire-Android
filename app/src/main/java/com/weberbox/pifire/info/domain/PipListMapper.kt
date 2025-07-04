package com.weberbox.pifire.info.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.info.data.model.InfoDto
import com.weberbox.pifire.info.presentation.model.InfoData.Info.Module

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