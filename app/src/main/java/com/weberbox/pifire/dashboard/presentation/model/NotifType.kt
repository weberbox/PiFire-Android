package com.weberbox.pifire.dashboard.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.ui.graphics.vector.ImageVector
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.outlined.Target

enum class NotifType(val title: Int, val icon: ImageVector, val defaultVisibility: Boolean) {
    Target(R.string.probe_type_target, Icon.Outlined.Target, true),
    HighTemp(R.string.probe_type_high_limit, Icons.Filled.VerticalAlignTop, false),
    LowTemp(R.string.probe_type_low_limit, Icons.Filled.VerticalAlignBottom, false);

    companion object {
        infix fun from(type: String): NotifType = NotifType.entries.firstOrNull {
            it.name.lowercase() == type.lowercase()
        } ?: Target
    }
}