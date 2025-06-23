package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Smartphone
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
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.info.presentation.model.InfoData.Info

@Composable
internal fun InfoApp(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_app_info),
        headerIcon = Icons.Outlined.Smartphone
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
                text = stringResource(R.string.info_app_version),
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.appVersion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.info_app_version_code),
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.smallOne,
                    bottom = MaterialTheme.spacing.smallOne
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.appVersionCode,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.info_app_build_type),
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.smallOne,
                    bottom = MaterialTheme.spacing.smallOne
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.appBuildType,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.info_app_flavor),
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.smallOne,
                    bottom = MaterialTheme.spacing.smallOne
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.appFlavor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.info_app_build_date),
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.smallOne,
                    bottom = MaterialTheme.spacing.smallOne
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info.appBuildDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (AppConfig.IS_DEV_BUILD) {
                Text(
                    text = stringResource(R.string.info_app_build_git_branch),
                    modifier = Modifier.padding(
                        top = MaterialTheme.spacing.smallOne,
                        bottom = MaterialTheme.spacing.smallOne
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = info.appGitBranch,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.info_app_build_git_rev),
                    modifier = Modifier.padding(
                        top = MaterialTheme.spacing.smallOne,
                        bottom = MaterialTheme.spacing.smallOne
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = info.appGitRev,
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
private fun InfoAppPreview() {
    val info = remember {
        Info(
            appVersion = "3.0.0",
            appVersionCode = "300000",
            appFlavor = "Dev",
            appBuildType = "Debug",
            appBuildDate = "04/21/2025",
            appGitRev = "147",
            appGitBranch = "development"
        )
    }
    PiFireTheme {
        Surface {
            InfoApp(
                info = info
            )
        }
    }
}