package com.weberbox.pifire.settings.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.settings.presentation.util.copy
import com.weberbox.pifire.settings.presentation.util.offset
import me.zhanghai.compose.preference.LocalPreferenceTheme
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun TwoTargetSwitchPreference(
    state: MutableState<Boolean>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    switchEnabled: Boolean = enabled,
    onClick: (() -> Unit)? = null,
) {
    var value by state
    TwoTargetSwitchPreference(
        value = value,
        onValueChange = { value = it },
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary,
        switchEnabled = switchEnabled,
        onClick = onClick,
    )
}

@Composable
fun TwoTargetSwitchPreference(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    switchEnabled: Boolean = enabled,
    onClick: (() -> Unit)? = null
) {
    TwoTargetPreference(
        title = title,
        secondTarget = {
            val theme = LocalPreferenceTheme.current
            Switch(
                checked = value,
                onCheckedChange = onValueChange,
                thumbContent = {
                    Icon(
                        modifier = Modifier.padding(MaterialTheme.spacing.extraSmall),
                        imageVector = if (value) Icons.Filled.Check else Icons.Filled.Close,
                        tint = if (value) MaterialTheme.colorScheme.onSurface else
                            MaterialTheme.colorScheme.inverseOnSurface,
                        contentDescription = null
                    )
                },
                modifier =
                    Modifier.padding(
                        theme.padding
                            .copy(start = theme.horizontalSpacing)
                            .offset(vertical = (-8).dp)
                    ),
                enabled = switchEnabled,
            )
        },
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary,
        onClick = onClick
    )
}

@Composable
private fun TwoTargetPreference(
    title: @Composable () -> Unit,
    secondTarget: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Preference(
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary,
        widgetContainer = {
            val theme = LocalPreferenceTheme.current
            Row(
                modifier = Modifier.padding(start = theme.horizontalSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(end = MaterialTheme.spacing.small),
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = DividerDefaults.color.let {
                        if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                    }
                )
                Box(
                    modifier =
                        Modifier
                            .size(DividerDefaults.Thickness, theme.dividerHeight)
                            .background(
                                DividerDefaults.color.let {
                                    if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                                }
                            )
                )
                secondTarget()
            }
        },
        onClick = onClick,
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun TwoTargetSwitchPreferencePreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            TwoTargetSwitchPreference(
                state = remember { mutableStateOf(true) },
                title = { Text(text = "Two target switch") },
                modifier = Modifier.fillMaxWidth(),
                icon = { Icon(imageVector = Icons.Outlined.Info, contentDescription = null) },
                summary = { Text(text = "Summary") },
            )
        }
    }
}