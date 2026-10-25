package com.weberbox.pifire.feature.changelog.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.feature.changelog.data.model.ChangelogDto
import com.weberbox.pifire.feature.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.feature.changelog.presentation.model.ChangelogData.Changelog
import com.weberbox.pifire.feature.changelog.presentation.model.ChangelogData.Log

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