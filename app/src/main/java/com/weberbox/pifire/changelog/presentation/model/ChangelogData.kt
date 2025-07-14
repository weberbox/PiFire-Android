package com.weberbox.pifire.changelog.presentation.model

import com.weberbox.pifire.R

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
    ) {
        val icon = when (type) {
            "new" -> R.drawable.ic_log_new
            "imp" -> R.drawable.ic_log_imp
            "fix" -> R.drawable.ic_log_fix
            "note" -> R.drawable.ic_log_note
            else -> R.drawable.ic_log_note
        }
    }
}
