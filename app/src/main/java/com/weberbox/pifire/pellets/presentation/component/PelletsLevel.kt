package com.weberbox.pifire.pellets.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.pelletDanger
import com.weberbox.pifire.common.presentation.theme.pelletDangerTrack
import com.weberbox.pifire.common.presentation.theme.pelletWarning
import com.weberbox.pifire.common.presentation.theme.pelletWarningTrack
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import com.weberbox.pifire.pellets.presentation.model.PelletsEvent
import com.weberbox.pifire.pellets.presentation.util.formatPercentage

@Composable
internal fun PelletsLevel(
    pellets: Pellets,
    isConnected: Boolean,
    onEventSent: (event: PelletsContract.Event) -> Unit
) {
    val level by remember(pellets.hopperLevel) { mutableIntStateOf(pellets.hopperLevel) }
    val progress by animateFloatAsState(level.toFloat() / 100)
    var color = MaterialTheme.colorScheme.primary
    var trackColor = MaterialTheme.colorScheme.secondaryContainer
    var textColor = MaterialTheme.colorScheme.onPrimary
    when {
        level <= AppConfig.LOW_PELLET_WARNING && progress > 0 -> {
            color = pelletDanger
            trackColor = pelletDangerTrack
            textColor = Color.White
        }

        level > AppConfig.LOW_PELLET_WARNING &&
                level < AppConfig.MEDIUM_PELLET_WARNING -> {
            color = pelletWarning
            trackColor = pelletWarningTrack
            textColor = Color.White
        }
    }
    HeaderCard(
        title = stringResource(R.string.pellets_hopper_level),
        headerIcon = Icons.Filled.Grain,
        buttonIcon = Icons.Filled.Refresh,
        onButtonClick = {
            if (isConnected) {
                onEventSent(PelletsContract.Event.SendEvent(PelletsEvent.HopperCheck))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(
                    bottom = MaterialTheme.spacing.smallThree,
                    top = MaterialTheme.spacing.extraSmallOne,
                    start = MaterialTheme.spacing.mediumOne,
                    end = MaterialTheme.spacing.mediumOne
                )
                .fillMaxWidth()
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .height(MaterialTheme.size.mediumFour)
                    .align(Alignment.Center)
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth(),
                progress = { progress },
                strokeCap = StrokeCap.Round,
                color = color,
                trackColor = trackColor,
                gapSize = -MaterialTheme.size.mediumFour,
                drawStopIndicator = {}
            )
            Text(
                text = formatPercentage(level),
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PelletsLevelPreview() {
    val pellets = remember {
        Pellets(
            hopperLevel = 39,
        )
    }
    PiFireTheme {
        Surface {
            PelletsLevel(
                pellets = pellets,
                isConnected = true,
                onEventSent = {}
            )
        }
    }
}