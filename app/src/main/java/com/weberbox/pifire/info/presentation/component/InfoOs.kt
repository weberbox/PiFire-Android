package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardCommandKey
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
internal fun InfoOs(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_os_info),
        headerIcon = Icons.Outlined.KeyboardCommandKey
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.smallTwo)
                .fillMaxWidth()
        ) {
            InfoItemRow(
                title = stringResource(R.string.info_os_name),
                summary = info.osInfo.prettyName
            )
            InfoItemRow(
                title = stringResource(R.string.info_os_version),
                summary = info.osInfo.version
            )
            InfoItemRow(
                title = stringResource(R.string.info_os_codename),
                summary = info.osInfo.codeName
            )
            InfoItemRow(
                title = stringResource(R.string.info_os_arch),
                summary = info.osInfo.architecture
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoOsPreview() {
    val info = remember { buildInfo() }
    PiFireTheme {
        Surface {
            InfoOs(
                info = info
            )
        }
    }
}