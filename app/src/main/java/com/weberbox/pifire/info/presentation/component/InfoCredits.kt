package com.weberbox.pifire.info.presentation.component

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.outlined.Copyright
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.info.presentation.contract.InfoContract
import com.weberbox.pifire.info.presentation.model.Licenses
import com.weberbox.pifire.info.presentation.screens.buildLicenseData
import java.util.Calendar

@Composable
internal fun InfoCredits(
    licenseData: Licenses,
    onNavigationRequested: (InfoContract.Effect.Navigation) -> Unit
) {
    val context = LocalContext.current
    val limit by remember { mutableIntStateOf(AppConfig.LIST_VIEW_LIMIT) }
    val currentYear = remember { Calendar.getInstance()[Calendar.YEAR].toString() }
    HeaderCard(
        title = stringResource(R.string.info_credits),
        headerIcon = Icons.Outlined.Copyright,
        buttonIcon = Icons.AutoMirrored.Filled.NavigateNext,
        onButtonClick = {
            onNavigationRequested(InfoContract.Effect.Navigation.LicenseDetails)
        },
        viewAllClick = {
            onNavigationRequested(InfoContract.Effect.Navigation.LicenseDetails)
        }
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
                text = stringResource(R.string.info_credits_text, currentYear),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
            )
            licenseData.list.take(limit).forEach { item ->
                LicenseItem(item) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(it.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoCreditsPreview() {
    PiFireTheme {
        Surface {
            InfoCredits(
                licenseData = buildLicenseData(),
                onNavigationRequested = {}
            )
        }
    }
}