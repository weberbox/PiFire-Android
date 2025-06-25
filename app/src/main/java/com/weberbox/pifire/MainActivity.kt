package com.weberbox.pifire

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.EventAlertDialog
import com.weberbox.pifire.common.presentation.contract.MainContract
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.common.presentation.navigation.RootNavGraph
import com.weberbox.pifire.common.presentation.state.rememberEventDialogState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.ObserveAsEvents
import com.weberbox.pifire.common.presentation.util.SnackbarController
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.util.UpdateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var appUpdateManager: UpdateManager

    private val mainViewModel by viewModels<MainViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state = mainViewModel.viewState.value
            val effectFlow = mainViewModel.effect
            val context = LocalContext.current
            val navController = rememberNavController()
            val eventDialog = rememberEventDialogState()
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            LaunchedEffect(SIDE_EFFECTS_KEY) {
                effectFlow.onEach { effect ->
                    when (effect) {
                        is MainContract.Effect.Notification -> {
                            this@MainActivity.showAlerter(
                                message = effect.text,
                                isError = effect.error
                            )
                        }

                        is MainContract.Effect.CheckForAppUpdates -> checkForUpdates()
                    }
                }.collect()
            }

            ObserveAsEvents(
                flow = SnackbarController.events,
                key1 = snackbarHostState
            ) { event ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val result = snackbarHostState.showSnackbar(
                        message = event.message.asString(context),
                        actionLabel = event.action?.name?.asString(context),
                        duration = event.duration
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        event.action?.action?.invoke()
                    }
                }
            }

            ObserveAsEvents(
                flow = DialogController.events
            ) { event ->
                eventDialog.show(event)
            }

            PiFireTheme(
                darkTheme = when (state.appTheme) {
                    AppTheme.Light -> false
                    AppTheme.Dark -> true
                    AppTheme.System -> isSystemInDarkTheme()
                },
                dynamicColor = state.dynamicColor
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) {
                    EventAlertDialog(
                        eventDialogState = eventDialog,
                    )
                    RootNavGraph(navController = navController)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (AppConfig.IS_PLAY_BUILD) {
            appUpdateManager.resumeUpdateIfNeeded(this@MainActivity)
        }
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.setEvent(MainContract.Event.StoreLatestDataState)
    }

    private fun checkForUpdates() {
        lifecycleScope.launch {
            appUpdateManager.checkForUpdate(this@MainActivity)
        }
    }
}