package com.weberbox.pifire.changelog.domain

import com.weberbox.pifire.changelog.data.model.ChangelogDto
import com.weberbox.pifire.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.changelog.presentation.model.ChangelogData.Changelog
import com.weberbox.pifire.changelog.presentation.model.ChangelogData.Log
import com.weberbox.pifire.common.data.interfaces.Mapper

object ChangelogDtoToDataMapper : Mapper<ChangelogDto, ChangelogData> {
    override fun map(from: ChangelogDto) = ChangelogData(
        changelog = from.changelog.map { data ->
            Changelog(
                version = data.version.orEmpty(),
                date = data.date.orEmpty(),
                current = data.current == true,
                isAlpha = data.isAlpha == true,
                logs = data.logs.map { log ->
                    Log(
                        type = log.type ?: "new",
                        text = log.text.orEmpty()
                    )
                }
            )
        }
    )
}