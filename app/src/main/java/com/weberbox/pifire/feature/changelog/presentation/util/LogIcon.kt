package com.weberbox.pifire.feature.changelog.presentation.util

import androidx.annotation.DrawableRes
import com.weberbox.pifire.R
import com.weberbox.pifire.feature.changelog.data.model.ChangelogData

@get:DrawableRes
internal val ChangelogData.Log.icon: Int
    get() = when (type) {
        "new" -> R.drawable.ic_log_new
        "imp" -> R.drawable.ic_log_imp
        "fix" -> R.drawable.ic_log_fix
        else -> R.drawable.ic_log_note
    }
