package com.weberbox.pifire.home.presentation.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.OutdoorGrill
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.home.presentation.model.NavigationItem
import com.weberbox.pifire.home.presentation.model.StaticNavigationItem

@Composable
fun buildNavigationItems(): List<NavigationItem> {
    return listOf(
        NavigationItem(
            title = stringResource(R.string.nav_pellets),
            route = NavGraph.HomeDest.Pellets,
            selectedIcon = Icons.Outlined.Grain,
            unselectedIcon = Icons.Filled.Grain
        ),
        NavigationItem(
            title = stringResource(R.string.nav_dashboard),
            route = NavGraph.HomeDest.Dashboard,
            selectedIcon = Icons.Filled.OutdoorGrill,
            unselectedIcon = Icons.Outlined.OutdoorGrill
        ),
        NavigationItem(
            title = stringResource(R.string.nav_events),
            route = NavGraph.HomeDest.Events,
            selectedIcon = Icons.AutoMirrored.Filled.EventNote,
            unselectedIcon = Icons.AutoMirrored.Outlined.EventNote
        )
    )
}

@Composable
fun buildStaticNavigationItems(): List<StaticNavigationItem> {
    return listOf(
        StaticNavigationItem(
            title = stringResource(R.string.nav_recipes),
            route = NavGraph.RecipesDest,
            icon = Icons.Filled.LocalDining,
        ),
        StaticNavigationItem(
            title = stringResource(R.string.nav_settings),
            route = NavGraph.SettingsDest,
            icon = Icons.Filled.Settings,
        ),
        StaticNavigationItem(
            title = stringResource(R.string.nav_info),
            route = NavGraph.InfoDest,
            icon = Icons.Filled.Info,
        ),
        StaticNavigationItem(
            title = stringResource(R.string.nav_changelog),
            route = NavGraph.Changelog,
            icon = Icons.Filled.FiberNew,
        ),
        StaticNavigationItem(
            title = stringResource(R.string.nav_logout),
            route = NavGraph.LandingDest.Landing(false),
            icon = Icons.AutoMirrored.Filled.Logout
        )
    )
}