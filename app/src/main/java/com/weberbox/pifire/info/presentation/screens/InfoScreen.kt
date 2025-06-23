package com.weberbox.pifire.info.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.HazeAppBar
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.CachedDataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.info.presentation.component.InfoList
import com.weberbox.pifire.info.presentation.contract.InfoContract
import com.weberbox.pifire.info.presentation.model.GPIODeviceData
import com.weberbox.pifire.info.presentation.model.GPIOInOutData
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.info.presentation.model.Licenses
import com.weberbox.pifire.info.presentation.model.Licenses.License
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun InfoScreenDestination(
    navController: NavHostController,
    viewModel: InfoViewModel = hiltViewModel(),
) {
    InfoScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is InfoContract.Effect.Navigation.Back ->
                    navController.popBackStack()

                is InfoContract.Effect.Navigation.LicenseDetails ->
                    navController.safeNavigate(
                        route = NavGraph.InfoDest.LicenseDetails(
                            viewModel.viewState.value.licenseData
                        ),
                        popUp = false
                    )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun InfoScreen(
    state: InfoContract.State,
    effectFlow: Flow<InfoContract.Effect>?,
    onEventSent: (event: InfoContract.Event) -> Unit,
    onNavigationRequested: (InfoContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val hazeState = rememberHazeState()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is InfoContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is InfoContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HazeAppBar(
                title = {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.nav_info)
                    )
                },
                scrollBehavior = scrollBehavior,
                hazeState = hazeState,
                onNavigate = { onNavigationRequested(InfoContract.Effect.Navigation.Back) }
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        AnimatedContent(
            targetState = state,
            contentKey = { it.isInfoLoading or it.isDataError or it.isLicencesLoading }
        ) { state ->
            when {
                state.isInfoLoading || state.isLicencesLoading -> InitialLoadingProgress()
                state.isDataError -> CachedDataError { onEventSent(InfoContract.Event.Refresh) }
                else -> {
                    InfoList(
                        state = state,
                        onEventSent = onEventSent,
                        onNavigationRequested = onNavigationRequested,
                        hazeState = hazeState,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoScreenPreview() {
    PiFireTheme {
        Surface {
            InfoScreen(
                state = InfoContract.State(
                    info = buildInfo(),
                    licenseData = buildLicenseData(),
                    isInfoLoading = false,
                    isLicencesLoading = false,
                    isRefreshing = false,
                    isDataError = false

                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {}
            )
        }
    }
}

internal fun buildInfo(): Info {
    return Info(
        cpuInfo = "Intel(R) Xeon(R) CPU-D-1520 @ 2.20GHz",
        cpuTemp = "40.0C",
        networkInfo = "Ethernet",
        outPins = listOf(
            GPIOInOutData(
                name = "fan",
                pin = "15"
            ),
            GPIOInOutData(
                name = "dc_fan",
                pin = "26"
            ),
            GPIOInOutData(
                name = "pwm",
                pin = "13"
            ),
            GPIOInOutData(
                name = "igniter",
                pin = "18"
            )
        ),
        inPins = listOf(
            GPIOInOutData(
                name = "selector",
                pin = "17"
            ),
            GPIOInOutData(
                name = "shutdown",
                pin = ""
            )
        ),
        devPins = listOf(
            GPIODeviceData(
                name = "Input",
                function = "enter_sw",
                pin = "21"
            ),
            GPIODeviceData(
                name = "",
                function = "up_clk",
                pin = "16"
            )
        ),
        platform = "Prototype",
        display = "Prototype",
        distance = "Prototype",
        uptime = "3 Days 24 Hours",
        serverVersion = "1.9.0",
        serverBuild = "23",
        appVersion = "3.0.0",
        appVersionCode = "300000",
        appFlavor = "Dev",
        appBuildType = "Debug",
        appBuildDate = "04/21/2025",
        appGitRev = "147",
        appGitBranch = "development"
    )
}

fun buildLicenseData(): Licenses {
    return Licenses(
        listOf(
            License(
                project = "PiFire Android",
                license = "https://github.com/weberbox/PiFire-Android/blob/master/LICENSE",
                title = "P",
                color = "#32FF4731"
            ),
            License(
                project = "Sentry",
                license = "https://github.com/getsentry/sentry-java/blob/main/LICENSE",
                title = "S",
                color = "#32004CFA"
            ),
            License(
                project = "Sentry",
                license = "https://github.com/getsentry/sentry-java/blob/main/LICENSE",
                title = "T",
                color = "#64666666"
            )
        )
    )
}