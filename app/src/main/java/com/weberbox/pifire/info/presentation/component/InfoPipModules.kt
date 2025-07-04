package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.contract.InfoContract
import com.weberbox.pifire.info.presentation.model.InfoData.Info

@Composable
internal fun InfoPipModules(
    info: Info,
    onEventSent: (event: InfoContract.Event) -> Unit,
) {
    val limit by remember { mutableIntStateOf(6) }
    HeaderCard(
        title = stringResource(R.string.info_pip_modules),
        headerIcon = Icons.Outlined.Memory,
        listSize = info.pipList.size,
        listLimit = limit,
        viewAllClick = {
            onEventSent(InfoContract.Event.PipModulesViewAll)
        }
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = MaterialTheme.spacing.smallTwo)
                .padding(bottom = MaterialTheme.spacing.smallTwo)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.small)
                        .weight(1.4f),
                    text = stringResource(R.string.info_pip_name),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.weight(0.6f),
                    text = stringResource(R.string.info_pip_version),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            info.pipList.take(limit).forEach { module ->
                PipItem(
                    module = module
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoPipModulesPreview() {
    val info = remember {
        Info(
            pipList = buildModules()
        )
    }
    PiFireTheme {
        Surface {
            InfoPipModules(
                info = info,
                onEventSent = { }
            )
        }
    }
}

internal fun buildModules(): List<Info.Module> {
    return listOf(
        Info.Module(
            name = "apprise",
            version = "1.9.3"
        ),
        Info.Module(
            name = "async-timeout",
            version = "5.0.1"
        ),
        Info.Module(
            name = "bidict",
            version = "0.23.1"
        ),
        Info.Module(
            name = "blinker",
            version = "1.9.0"
        ),
        Info.Module(
            name = "bluepy",
            version = "1.3.0"
        ),
        Info.Module(
            name = "certifi",
            version = "2025.6.15"
        ),
        Info.Module(
            name = "charset-normalizer",
            version = "3.4.2"
        )
    )
}