package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.Cpu64Bit
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.info.presentation.screens.buildInfo

@Composable
internal fun InfoCpu(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_cpu_info),
        headerIcon = Icon.Filled.Cpu64Bit
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.smallTwo)
                .fillMaxWidth()
        ) {
            InfoItemRow(
                title = stringResource(R.string.info_cpu_temp),
                summary = "${info.cpuTemp}°c"
            )
            InfoItemColumn(
                title = stringResource(R.string.info_cpu_throttled),
                summary = if (info.cpuThrottled)
                    stringResource(R.string.info_cpu_throttled_true)
                else
                    stringResource(R.string.info_cpu_throttled_false)
            )
            InfoItemColumn(
                title = stringResource(R.string.info_cpu_under_volt),
                summary = if (info.cpuUnderVolt)
                    stringResource(R.string.info_cpu_under_volt_true)
                else
                    stringResource(R.string.info_cpu_under_volt_false)
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoCpuPreview() {
    val info = remember { buildInfo() }
    PiFireTheme {
        Surface {
            InfoCpu(
                info = info
            )
        }
    }
}