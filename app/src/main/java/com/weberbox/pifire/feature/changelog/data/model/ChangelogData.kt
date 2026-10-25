package com.weberbox.pifire.feature.changelog.data.model

data class ChangelogData(
    val changelog: List<Changelog> = listOf()
) {

    data class Changelog(
        val version: String = "",
        val date: String = "",
        val current: Boolean = false,
        val isAlpha: Boolean = false,
        val logs: List<Log> = listOf()
    )

    data class Log(
        val type: String = "note",
        val text: String = ""
    )
}
