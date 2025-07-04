package com.weberbox.pifire.recipes.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector

internal data class FabItem(
    val icon: ImageVector,
    val title: Int,
    val action: Action
) {
    enum class Action {
        Delete,
        Share,
        Print,
        Run
    }
}