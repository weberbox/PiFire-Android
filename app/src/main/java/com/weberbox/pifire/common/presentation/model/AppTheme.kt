package com.weberbox.pifire.common.presentation.model

import com.weberbox.pifire.R

enum class AppTheme(val title: Int, val type: String) {
    Light(R.string.app_theme_light, "light"),
    Dark(R.string.app_theme_dark, "dark"),
    System(R.string.app_theme_system, "system");

    companion object {
        infix fun from(type: String?): AppTheme = AppTheme.entries.firstOrNull {
            it.type.lowercase() == type?.lowercase()
        } ?: System
    }
}
