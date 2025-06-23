package com.weberbox.pifire.home.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.TrayArrowUp
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition

@Composable
internal fun HomeAppBarActions(
    lidOpenDetectEnabled: Boolean,
    isConnected: Boolean,
    isHoldMode: Boolean,
    onTriggerLidEvent: () -> Unit
) {
    var showLidDetectMenu by rememberSaveable { mutableStateOf(false) }
    AnimatedVisibility(
        visible = lidOpenDetectEnabled && isHoldMode,
        enter = fadeEnterTransition(),
        exit = fadeExitTransition()
    ) {
        Row {
            IconButton(
                onClick = {
                    if (isConnected) showLidDetectMenu = true
                }
            ) {
                Icon(
                    imageVector = Icon.Filled.TrayArrowUp,
                    contentDescription = stringResource(R.string.settings_cat_lid_detection)
                )
            }
        }
        DropdownMenu(
            expanded = showLidDetectMenu,
            onDismissRequest = { showLidDetectMenu = false },
            shape = MaterialTheme.shapes.large
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.trigger_lid_open)
                    )
                },
                onClick = {
                    onTriggerLidEvent()
                    showLidDetectMenu = false
                }
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun HomeAppBarActionsPreview() {
    PiFireTheme {
        Surface {
            HomeAppBarActions(
                lidOpenDetectEnabled = true,
                isConnected = true,
                isHoldMode = true,
                onTriggerLidEvent = {}
            )
        }
    }
}