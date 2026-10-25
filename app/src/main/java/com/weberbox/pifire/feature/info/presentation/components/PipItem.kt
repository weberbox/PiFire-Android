package com.weberbox.pifire.feature.info.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.feature.info.presentation.model.InfoData.Info.Module

@Composable
internal fun PipItem(
    modifier: Modifier = Modifier,
    module: Module,
    maxLines: Int = 1
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = MaterialTheme.spacing.small)
                .weight(1.4f),
            text = module.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.weight(0.6f),
            text = module.version,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PipItemPreview() {
    PiFireTheme {
        Surface {
            PipItem(
                module = Module(
                    name = "apprise",
                    version = "1.9.3"
                )
            )
        }
    }
}