package com.weberbox.pifire.home.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector

data class StaticNavigationItem(
    val title: String,
    val route: Any,
    val icon: ImageVector,
    val badgeCount: Int? = null
)
