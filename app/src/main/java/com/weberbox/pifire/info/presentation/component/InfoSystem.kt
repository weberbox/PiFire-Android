package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storage
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
internal fun InfoSystem(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_system_info),
        headerIcon = Icons.Outlined.Storage
    ) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = MaterialTheme.spacing.smallOne,
                    start = MaterialTheme.spacing.smallTwo,
                    end = MaterialTheme.spacing.smallTwo
                )
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.info_cpu),
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.cpuInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.info_cpu_temp),
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.smallOne),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.cpuTemp,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.info_network),
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.smallOne),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.networkInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoSystemPreview() {
    val info = remember {
        Info(
            cpuInfo = "Intel x5240",
            networkInfo = "Ethernet 100",
            cpuTemp = "86c"
        )
    }
    PiFireTheme {
        Surface {
            InfoSystem(
                info = info
            )
        }
    }
}