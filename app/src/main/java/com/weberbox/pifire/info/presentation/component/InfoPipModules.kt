package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.model.InfoData.Info

@Composable
internal fun InfoPipModules(
    info: Info
) {
    var limit by rememberSaveable { mutableIntStateOf(6) }
    HeaderCard(
        title = stringResource(R.string.info_pip_modules),
        headerIcon = Icons.Outlined.Memory,
        listSize = info.pipList.size,
        listLimit = limit,
        viewAllClick = {
            limit = info.pipList.size + 1
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
                info = info
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
            name = "bluepy2",
            version = "1.3.1"
        )
    )
}