package com.weberbox.pifire.feature.dashboard.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.ui.graphics.vector.ImageVector
import com.weberbox.pifire.R
import com.weberbox.pifire.core.model.dashboard.RunningMode
import com.weberbox.pifire.core.ui.icons.Icon
import com.weberbox.pifire.core.ui.icons.filled.FireAlert
import com.weberbox.pifire.core.ui.icons.outlined.FlagCheckered
import com.weberbox.pifire.core.ui.icons.outlined.Target

internal val RunningMode.title: Int
    get() = when (this) {
        RunningMode.Stop -> R.string.dash_mode_stop
        RunningMode.Startup -> R.string.dash_mode_startup
        RunningMode.Shutdown -> R.string.dash_mode_shutdown
        RunningMode.Smoke -> R.string.dash_mode_smoke
        RunningMode.Hold -> R.string.dash_mode_hold
        RunningMode.Prime -> R.string.dash_mode_prime
        RunningMode.Reignite -> R.string.dash_mode_reignite
        RunningMode.Monitor -> R.string.dash_mode_monitor
        RunningMode.Recipe -> R.string.dash_mode_recipe
        RunningMode.Manual -> R.string.dash_mode_manual
        RunningMode.Unknown -> R.string.unknown
    }

internal val RunningMode.icon: ImageVector
    get() = when (this) {
        RunningMode.Stop -> Icons.Outlined.PowerSettingsNew
        RunningMode.Startup -> Icons.Outlined.Schedule
        RunningMode.Shutdown -> Icon.Outlined.FlagCheckered
        RunningMode.Smoke -> Icons.Outlined.Cloud
        RunningMode.Hold -> Icon.Outlined.Target
        RunningMode.Prime -> Icons.Filled.DoubleArrow
        RunningMode.Reignite -> Icon.Filled.FireAlert
        RunningMode.Monitor -> Icons.Outlined.Visibility
        RunningMode.Recipe -> Icons.Outlined.LocalDining
        RunningMode.Manual -> Icons.Filled.Tune
        RunningMode.Unknown -> Icons.Outlined.ErrorOutline
    }
