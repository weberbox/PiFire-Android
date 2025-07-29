package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.info.presentation.screens.buildInfo

@Composable
internal fun InfoPlatform(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_platform),
        headerIcon = Icons.Outlined.Apps
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.spacing.smallTwo
                )
                .fillMaxWidth()
        ) {
            InfoItemRow(
                title = stringResource(R.string.info_platform_system_model),
                summary = info.platformInfo.systemModel
            )
            InfoItemRow(
                title = stringResource(R.string.info_platform_cpu_model),
                summary = info.platformInfo.cpuModel
            )
            InfoItemRow(
                title = stringResource(R.string.info_platform_hardware),
                summary = info.platformInfo.cpuHardware
            )
            InfoItemRow(
                title = stringResource(R.string.info_platform_cores),
                summary = info.platformInfo.cpuCores
            )
            InfoItemRow(
                title = stringResource(R.string.info_platform_frequency),
                summary = info.platformInfo.cpuFrequency
            )
            InfoItemRow(
                title = stringResource(R.string.info_platform_total_ram),
                summary = info.platformInfo.totalRam
            )
            InfoItemRow(
                title = stringResource(R.string.info_platform_available_ram),
                summary = info.platformInfo.availableRam
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoPlatformPreview() {
    val info = remember { buildInfo() }
    PiFireTheme {
        Surface {
            InfoPlatform(
                info = info
            )
        }
    }
}