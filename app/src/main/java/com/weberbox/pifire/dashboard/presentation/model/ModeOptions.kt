package com.weberbox.pifire.dashboard.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlagCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.ui.graphics.vector.ImageVector
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.outlined.ArrowRightCircle
import com.weberbox.pifire.common.icons.outlined.CloudCircle
import com.weberbox.pifire.common.icons.outlined.NextCircle
import com.weberbox.pifire.common.icons.outlined.Target
import com.weberbox.pifire.common.icons.outlined.VisibilityCircle
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent

internal enum class ModeOptions(val title: Int, val icon: ImageVector, val event: DashEvent) {
    Stop(R.string.dash_mode_stop, Icons.Outlined.StopCircle, DashEvent.Stop),
    Start(R.string.dash_mode_start, Icons.Outlined.PlayCircle, DashEvent.Start),
    Shutdown(R.string.dash_mode_shutdown, Icons.Outlined.FlagCircle, DashEvent.Shutdown),
    Smoke(R.string.dash_mode_smoke, Icon.Outlined.CloudCircle, DashEvent.Smoke),
    Hold(R.string.dash_mode_hold, Icon.Outlined.Target, DashEvent.HoldDialog),
    Prime(R.string.dash_mode_prime, Icon.Outlined.ArrowRightCircle, DashEvent.PrimeDialog),
    Monitor(R.string.dash_mode_monitor, Icon.Outlined.VisibilityCircle, DashEvent.Monitor),
    Continue(R.string.dash_mode_continue, Icons.Outlined.PlayCircle, DashEvent.RecipeUnPause),
    Next(R.string.dash_mode_next, Icon.Outlined.NextCircle, DashEvent.RecipeUnPause);

    companion object {
        infix fun from(mode: String): ModeOptions = entries.firstOrNull {
            it.name.lowercase() == mode.lowercase()
        } ?: Stop
    }
}