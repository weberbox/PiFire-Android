package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.dashboard.presentation.contract.DashContract.DashEvent

@Composable
internal fun ModeButton(
    mode: String,
    icon: ImageVector,
    onClick: (DashEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .size(MaterialTheme.size.extraExtraLarge)
            .clip(MaterialTheme.shapes.large)
            .clickable(
                onClick = { onClick(DashEvent.Start) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(MaterialTheme.size.extraLarge),
            imageVector = icon,
            contentDescription = mode
        )
        Text(
            text = mode,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun SettingsSheetPreview() {
    PiFireTheme {
        Surface {
            ModeButton(
                mode = "Stop",
                icon = Icons.Outlined.PlayCircle,
                onClick = {}
            )
        }
    }
}