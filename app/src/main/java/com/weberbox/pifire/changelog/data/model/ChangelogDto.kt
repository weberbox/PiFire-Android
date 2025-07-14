package com.weberbox.pifire.changelog.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangelogDto(
    val changelog: List<Changelog> = listOf()
) {

    @Serializable
    data class Changelog(
        val version: String? = null,
        val date: String? = null,
        val current: Boolean? = null,
        val isAlpha: Boolean? = null,
        val logs: List<Logs> = listOf()
    )

    @Serializable
    data class Logs(
        val type: String? = null,
        val text: String? = null
    )
}
