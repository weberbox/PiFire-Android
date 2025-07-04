package com.weberbox.pifire.changelog.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangelogDto(
    var changelog: List<Changelog> = listOf()
) {

    @Serializable
    data class Changelog(
        var version: String? = null,
        var date: String? = null,
        var current: Boolean? = null,
        var logs: List<Logs> = listOf()
    )

    @Serializable
    data class Logs(
        var type: String? = null,
        var text: String? = null
    )
}
