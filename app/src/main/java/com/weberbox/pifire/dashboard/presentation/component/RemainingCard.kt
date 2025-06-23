package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.slideDownExpandEnterTransition
import com.weberbox.pifire.common.presentation.util.slideOutShrinkExitTransition
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.dashboard.presentation.util.formatRemainingTime
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
internal fun RemainingCard(
    modifier: Modifier = Modifier,
    currentMode: String,
    modeStartTime: Int,
    startDuration: Int,
    shutdownDuration: Int,
    initialTime: Int = 0
) {
    var remainingTime by rememberSaveable { mutableIntStateOf(initialTime) }
    LaunchedEffect(modeStartTime, currentMode) {
        if (modeStartTime > 0) {
            val currentTime = Instant.now().epochSecond
            var endTime = 0
            when (currentMode) {
                RunningMode.Startup.name -> {
                    endTime = modeStartTime + startDuration
                }
                RunningMode.Shutdown.name -> {
                    endTime = modeStartTime + shutdownDuration
                }
                else -> remainingTime = 0
            }
            val totalSeconds = endTime - currentTime
            (totalSeconds - 1 downTo 0).onEach { out ->
                delay(1000)
                remainingTime = out.toInt()
            }
        } else remainingTime = 0
    }
    AnimatedVisibility(
        visible = remainingTime > 0,
        enter = slideDownExpandEnterTransition(),
        exit = slideOutShrinkExitTransition()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.smallOne)
                .fillMaxWidth()
        ) {
            Card(
                modifier = modifier
                    .height(MaterialTheme.size.largeTwo)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = MaterialTheme.elevation.small
                )
            ) {
                Row(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.dash_remaining_time, currentMode),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(
                            start = MaterialTheme.spacing.smallTwo,
                            end = 5.dp
                        )
                    )
                    Text(
                        text = formatRemainingTime(remainingTime),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.smallTwo)
                    )
                }
            }
        }
    }

}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RemainingCardPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                RemainingCard(
                    initialTime = 239,
                    currentMode = "Startup",
                    modeStartTime = 1747117549,
                    startDuration = 240,
                    shutdownDuration = 240
                )
            }
        }
    }
}