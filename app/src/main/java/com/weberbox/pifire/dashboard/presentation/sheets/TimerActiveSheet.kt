package com.weberbox.pifire.dashboard.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.dashboard.presentation.component.ModeButton
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent

@Composable
internal fun TimerActiveSheet(
    modifier: Modifier = Modifier,
    paused: Boolean,
    onClick: (DashEvent) -> Unit
) {
    Box(
        modifier = modifier
            .height(110.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = MaterialTheme.spacing.smallOne)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ModeButton(
                mode = stringResource(R.string.stop),
                icon = Icons.Outlined.StopCircle,
                onClick = {
                    onClick(DashEvent.TimerAction(ServerConstants.PT_TIMER_STOP))
                }
            )
            if (paused) {
                ModeButton(
                    mode = stringResource(R.string.resume),
                    icon = Icons.Outlined.PlayCircle,
                    onClick = {
                        onClick(DashEvent.TimerAction(ServerConstants.PT_TIMER_START))
                    }
                )
            } else {
                ModeButton(
                    mode = stringResource(R.string.pause),
                    icon = Icons.Outlined.PauseCircle,
                    onClick = {
                        onClick(DashEvent.TimerAction(ServerConstants.PT_TIMER_PAUSE))
                    }
                )
            }
        }
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun TimerActiveSheetPreview() {
    PiFireTheme {
        Surface {
            TimerActiveSheet(
                paused = false,
                onClick = {}
            )
        }
    }
}