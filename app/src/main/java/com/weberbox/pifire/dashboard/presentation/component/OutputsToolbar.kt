package com.weberbox.pifire.dashboard.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.DoubleArrowRight
import com.weberbox.pifire.common.icons.filled.Fan
import com.weberbox.pifire.common.icons.filled.Flame

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun OutputsToolbar(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onActionClick: (Action) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        HorizontalFloatingToolbar(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .offset(y = -ScreenOffset),
            expanded = false,
            colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
            content = {
                Action.entries.forEach { option ->
                    IconButton(
                        onClick = { onActionClick(option) }
                    ) {
                        Icon(
                            imageVector = option.icon ,
                            contentDescription = stringResource(option.description)
                        )
                    }
                }
                FilledIconButton(
                    onClick = { onClose() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            },
        )
    }
}

enum class Action(val icon: ImageVector, val description: Int) {
    Fan(icon = Icon.Filled.Fan, description = R.string.fan),
    Auger(icon = Icon.Filled.DoubleArrowRight, description = R.string.auger),
    Igniter(icon = Icon.Filled.Flame, description = R.string.igniter)
}