package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.modifier.doublePulseEffect
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.slideDownExpandEnterTransition
import com.weberbox.pifire.common.presentation.util.slideOutShrinkExitTransition

@Composable
internal fun CriticalErrorCard(
    criticalError: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = criticalError,
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
                        enabled = criticalError,
                        targetScale = 1.2f,
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.large
                    )
                    .height(80.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = MaterialTheme.elevation.small
                ),
                onClick = {
                    if (onClick != null) onClick()
                }
            ) {
                Column(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.dash_critical_error_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.errorContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            top = MaterialTheme.spacing.smallOne,
                            start = MaterialTheme.spacing.smallTwo
                        )
                    )
                    Text(
                        text = stringResource(R.string.dash_critical_error_description),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            top = 5.dp,
                            start = MaterialTheme.spacing.smallTwo,
                            end = MaterialTheme.spacing.smallTwo
                        )
                    )
                }
            }
        }
    }

}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun CriticalErrorCardPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                CriticalErrorCard(
                    criticalError = true
                )
            }
        }
    }
}