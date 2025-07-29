package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.common.presentation.component.PullToRefresh
import com.weberbox.pifire.common.presentation.feature.Feature
import com.weberbox.pifire.common.presentation.feature.FeatureGate
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.contract.InfoContract
import com.weberbox.pifire.info.presentation.screens.buildInfo
import com.weberbox.pifire.info.presentation.screens.buildLicenseData
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun InfoList(
    state: InfoContract.State,
    onEventSent: (event: InfoContract.Event) -> Unit,
    onNavigationRequested: (InfoContract.Effect.Navigation) -> Unit,
    hazeState: HazeState,
    contentPadding: PaddingValues
) {
    PullToRefresh(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEventSent(InfoContract.Event.Refresh) },
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        Column(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(
                    horizontal = MaterialTheme.spacing.smallOne,
                    vertical = MaterialTheme.spacing.extraSmall
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.size.smallOne),
        ) {
            FeatureGate(
                feature = Feature.NewSystemInfo,
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            MaterialTheme.size.smallOne
                        )
                    ) {
                        InfoPlatform(info = state.info)
                        InfoCpu(info = state.info)
                        InfoOs(info = state.info)
                        InfoNetwork(info = state.info)
                        InfoWifi(info = state.info)
                    }
                },
                fallback = {
                    InfoSystem(info = state.info)
                }
            )
            InfoGpioInOut(info = state.info)
            InfoGpioDevices(info = state.info)
            InfoModules(info = state.info)
            InfoPipModules(
                info = state.info,
                onEventSent = onEventSent
            )
            InfoUptime(info = state.info)
            InfoApp(info = state.info)
            InfoCredits(
                licenseData = state.licenseData,
                onNavigationRequested = onNavigationRequested
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoListPreview() {
    PiFireTheme {
        Surface {
            InfoList(
                state = InfoContract.State(
                    info = buildInfo(),
                    licenseData = buildLicenseData(),
                    isInfoLoading = false,
                    isLicencesLoading = false,
                    isRefreshing = false,
                    isDataError = false

                ),
                onEventSent = {},
                onNavigationRequested = {},
                hazeState = rememberHazeState(),
                contentPadding = PaddingValues()
            )
        }
    }
}