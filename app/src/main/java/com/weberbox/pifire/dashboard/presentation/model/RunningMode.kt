package com.weberbox.pifire.dashboard.presentation.model

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
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.FireAlert
import com.weberbox.pifire.common.icons.outlined.FlagCheckered
import com.weberbox.pifire.common.icons.outlined.Target

enum class RunningMode(val title: Int, val icon: ImageVector) {
    Stop(R.string.dash_mode_stop, Icons.Outlined.PowerSettingsNew),
    Startup(R.string.dash_mode_startup, Icons.Outlined.Schedule),
    Shutdown(R.string.dash_mode_shutdown, Icon.Outlined.FlagCheckered),
    Smoke(R.string.dash_mode_smoke, Icons.Outlined.Cloud),
    Hold(R.string.dash_mode_hold, Icon.Outlined.Target),
    Prime(R.string.dash_mode_prime, Icons.Filled.DoubleArrow),
    Reignite(R.string.dash_mode_reignite, Icon.Filled.FireAlert),
    Monitor(R.string.dash_mode_monitor, Icons.Outlined.Visibility),
    Recipe(R.string.dash_mode_recipe, Icons.Outlined.LocalDining),
    Manual(R.string.dash_mode_manual, Icons.Filled.Tune),
    Unknown(R.string.unknown, Icons.Outlined.ErrorOutline);

    companion object {
        infix fun from(mode: String): RunningMode = entries.firstOrNull {
            it.name.lowercase() == mode.lowercase()
        } ?: Unknown
    }
}