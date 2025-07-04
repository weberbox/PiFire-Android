package com.weberbox.pifire.common.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolTipBox(
    title: String,
    details: String,
    buttonText: String,
    enableUserInput: Boolean = false,
    spacing: Dp = MaterialTheme.spacing.extraSmall,
    onClick: () -> Unit,
    tooltipState: TooltipState,
    content: @Composable () -> Unit
) {
    val positionProvider = rememberTooltipPositionProvider(spacing)
    TooltipBox(
        enableUserInput = enableUserInput,
        positionProvider = positionProvider,
        tooltip = {
            RichTooltip(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                action = {
                    TextButton(
                        onClick = onClick
                    ) {
                        Text(
                            text = buttonText
                        )
                    }
                },
                colors = TooltipDefaults.richTooltipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Text(details)
            }
        },
        state = tooltipState,
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ToolTipBoxPreview() {
    val tooltipState = rememberTooltipState(true, true)
    PiFireTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ToolTipBox(
                    title = stringResource(R.string.dash_hold_temp_tip_title),
                    details = stringResource(R.string.dash_hold_temp_tip_details),
                    buttonText = stringResource(R.string.dismiss),
                    spacing = (-20).dp,
                    onClick = {},
                    tooltipState = tooltipState
                ) {
                    Text(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        text = "Testing"
                    )
                }
            }
        }
    }
}