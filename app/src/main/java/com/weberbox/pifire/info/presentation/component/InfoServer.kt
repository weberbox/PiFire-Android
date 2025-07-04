package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.model.InfoData.Info

@Composable
internal fun InfoServer(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_server_info),
        headerIcon = Icons.Outlined.Dns
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = MaterialTheme.spacing.smallOne,
                    start = MaterialTheme.spacing.smallTwo,
                    end = MaterialTheme.spacing.smallTwo
                )
        ) {
            Text(
                text = stringResource(R.string.info_server_version),
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.serverVersion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.info_server_build),
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.smallOne,
                    bottom = MaterialTheme.spacing.smallOne
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.serverBuild,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoServerPreview() {
    val info = remember {
        Info(
            serverVersion = "1.9.0",
            serverBuild = "23"
        )
    }
    PiFireTheme {
        Surface {
            InfoServer(
                info = info
            )
        }
    }
}