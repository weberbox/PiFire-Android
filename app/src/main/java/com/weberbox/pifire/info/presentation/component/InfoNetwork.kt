package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cable
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
import com.weberbox.pifire.info.presentation.screens.buildInfo

@Composable
internal fun InfoNetwork(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_network),
        headerIcon = Icons.Outlined.Cable
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.smallTwo)
                .padding(bottom = MaterialTheme.spacing.smallOne)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne)
        ) {
            info.networkMap.forEach { (iface, ifaceData) ->
                Text(
                    text = "$iface:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.smallOne),
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.smallOne
                    )
                ) {
                    Text(
                        text = stringResource(R.string.info_network_ip),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ifaceData.ipAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.smallOne),
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.smallOne
                    )
                ) {
                    Text(
                        text = stringResource(R.string.info_network_mac),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ifaceData.macAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            if (info.networkMap.isEmpty()) {
                Text(
                    text = stringResource(R.string.info_network_unavailable),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoNetworkPreview() {
    val info = remember { buildInfo() }
    PiFireTheme {
        Surface {
            InfoNetwork(
                info = info
            )
        }
    }
}