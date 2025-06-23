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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.modifier.doublePulseEffect
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.slideDownExpandEnterTransition
import com.weberbox.pifire.common.presentation.util.slideOutShrinkExitTransition
import com.weberbox.pifire.dashboard.presentation.util.formatRemainingTime
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
internal fun LidDetectedCard(
    modifier: Modifier = Modifier,
    lidOpenDetected: Boolean,
    lidOpenEndTime: Int,
    initialTime: Int = 0,
    onClick: (() -> Unit)? = null
) {
    val visible by rememberSaveable(lidOpenDetected) { mutableStateOf(lidOpenDetected) }
    var remainingTime by rememberSaveable { mutableIntStateOf(initialTime) }
    LaunchedEffect(lidOpenEndTime, lidOpenDetected) {
        if (lidOpenEndTime > 0) {
            val currentTime = Instant.now().epochSecond
            val totalSeconds = lidOpenEndTime - currentTime
            (totalSeconds - 1 downTo 0).onEach { out ->
                delay(1000)
                remainingTime = out.toInt()
            }
        } else remainingTime = 0
    }

    AnimatedVisibility(
        visible = visible,
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
                    .doublePulseEffect(
                        enabled = visible,
                        targetScale = 1.1f,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.large
                    )
                    .height(MaterialTheme.size.largeTwo)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = MaterialTheme.elevation.small
                ),
                onClick = {
                    if (onClick != null) onClick()
                }
            ) {
                Row(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.dash_lid_open_detected),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.smallTwo)
                    )
                    Text(
                        modifier = Modifier.padding(end = MaterialTheme.spacing.extraSmall),
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(R.string.dash_lid_open_paused)
                    )
                    Text(
                        text = formatRemainingTime(remainingTime),
                        style = MaterialTheme.typography.titleSmall,
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
private fun LidDetectedPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                LidDetectedCard(
                    lidOpenDetected = true,
                    lidOpenEndTime = 0
                )
            }
        }
    }
}