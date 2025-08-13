package com.weberbox.pifire.dashboard.presentation.sheets

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.dashboard.presentation.component.ModeButton
import com.weberbox.pifire.dashboard.presentation.component.SlideToStart
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent
import com.weberbox.pifire.dashboard.presentation.util.getModeButtons
import kotlinx.coroutines.delay

@Composable
internal fun ModeSheet(
    modifier: Modifier = Modifier,
    currentMode: String,
    recipePaused: Boolean,
    startupCheck: Boolean,
    onEvent: (DashEvent) -> Unit,
    onPrime: () -> Unit,
    onHold: () -> Unit,
    onStart: () -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var swipeVisible by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableIntStateOf(3) }
    LaunchedEffect(swipeVisible, remainingTime, sliderPosition) {
        if (sliderPosition <= 0f && swipeVisible) {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--
            }
            swipeVisible = false
        } else {
            remainingTime = 3
        }
    }
    Box(
        modifier = modifier
            .height(110.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            getModeButtons(currentMode, recipePaused).forEach { mode ->
                ModeButton(
                    mode = stringResource(mode.title),
                    icon = mode.icon,
                    onClick = {
                        when (mode.event) {
                            is DashEvent.HoldDialog -> onHold()
                            is DashEvent.PrimeDialog -> onPrime()
                            is DashEvent.Start -> {
                                if (startupCheck) swipeVisible = true else onStart()
                            }
                            else -> onEvent(mode.event)
                        }
                    }
                )
            }
        }
        AnimatedVisibility(
            visible = swipeVisible,
            enter = fadeEnterTransition(),
            exit = fadeExitTransition()
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(start = 40.dp, end = 40.dp, top = 22.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                SlideToStart(
                    btnText = stringResource(R.string.swipe_to_start),
                    btnTextStyle = MaterialTheme.typography.titleSmall,
                    outerBtnBackgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                    sliderBtnBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    sliderBtnIcon = Icons.Outlined.Lock,
                    onSliderChange = { sliderPosition = it },
                    onBtnSwipe = { onStart() }
                )
            }
        }
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ModeSheetPreview() {
    PiFireTheme {
        Surface {
            ModeSheet(
                currentMode = "Startup",
                recipePaused = false,
                startupCheck = false,
                onEvent = {},
                onHold = {},
                onPrime = {},
                onStart = {}
            )
        }
    }
}