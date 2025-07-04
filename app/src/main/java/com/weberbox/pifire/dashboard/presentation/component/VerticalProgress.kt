package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
internal fun VerticalProgress(
    modifier: Modifier = Modifier,
    progress: () -> Float,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surface,
    trackShape: CornerBasedShape = MaterialTheme.shapes.large,
    trackWidth: Dp = MaterialTheme.spacing.smallTwo
) {
    val coercedProgress = { progress().coerceIn(0f, 1f) }
    val iProgress by animateFloatAsState(targetValue = coercedProgress())
    Column(
        modifier = modifier
            .clip(trackShape)
            .background(trackColor)
            .width(trackWidth)
    ) {
        Box(
            modifier = Modifier
                .weight(if ((1f - iProgress) == 0f) 0.0001f else 1 - iProgress)
                .fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .clip(trackShape)
                .weight(if (iProgress == 0f) 0.0001f else iProgress)
                .fillMaxWidth()
                .background(color)
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun VerticalProgressPreview() {
    PiFireTheme {
        Surface {
            VerticalProgress(
                progress = { 0.1f }
            )
        }
    }
}