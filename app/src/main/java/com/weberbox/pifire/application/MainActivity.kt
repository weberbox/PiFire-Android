package com.weberbox.pifire.application

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.remoteconfig.UpdateManager
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.AppSnackbarHost
import com.weberbox.pifire.core.ui.components.EventAlertDialog
import com.weberbox.pifire.core.ui.model.AppTheme
import com.weberbox.pifire.core.ui.state.EventDialogState
import com.weberbox.pifire.core.ui.state.rememberEventDialogState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.ObserveAsEvents
import com.weberbox.pifire.core.ui.util.SnackbarController
import com.weberbox.pifire.core.ui.util.SnackbarEvent
import com.weberbox.pifire.core.ui.util.showAlerter
import com.weberbox.pifire.navigation.RootNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appUpdateManager: UpdateManager

    @Inject
    lateinit var analytics: Analytics

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appUpdateManager.attach(this)
        if (AppConfig.IS_PLAY_BUILD) {
            appUpdateManager.register(this)
        }

        setContent {
            MainContent()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun MainContent() {
        val state = mainViewModel.viewState.value
        val effectFlow = mainViewModel.effect
        val context = LocalContext.current
        val navController = rememberNavController()
        val eventDialog = rememberEventDialogState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var currentSnackbarEvent by remember { mutableStateOf<SnackbarEvent?>(null) }

        HandleSideEffects(
            effectFlow = effectFlow,
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    // Outside any NavHost destination, so there's no source entry to guard on
                    is MainContract.Effect.Navigation.NavRoute ->
                        navController.navigate(route = navigationEffect.route) {
                            navigationEffect.popUpToRoute?.let { popUpToRoute ->
                                popUpTo(route = popUpToRoute) {
                                    inclusive = navigationEffect.inclusive
                                }
                            }
                        }
                }
            },
        )
        ObserveSnackbarEvents(
            snackbarHostState = snackbarHostState,
            scope = scope,
            context = context,
            onEventReceived = { currentSnackbarEvent = it },
        )
        ObserveDialogEvents(eventDialog = eventDialog)

        PiFireTheme(
            darkTheme = determineDarkMode(state.appTheme),
            dynamicColor = state.dynamicColor,
            featureSupport = state.featureSupport,
        ) {
            Scaffold(
                snackbarHost = {
                    AppSnackbarHost(
                        snackbarHostState = snackbarHostState,
                        currentSnackbarEvent = currentSnackbarEvent,
                    )
                },
            ) {
                EventAlertDialog(eventDialogState = eventDialog)
                RootNavGraph(
                    navController = navController,
                    analytics = analytics,
                )
            }
        }
    }

    @Composable
    private fun HandleSideEffects(
        effectFlow: Flow<MainContract.Effect>,
        onNavigationRequested: (MainContract.Effect.Navigation) -> Unit,
    ) {
        LaunchedEffect(SIDE_EFFECTS_KEY) {
            effectFlow.onEach { effect ->
                when (effect) {
                    is MainContract.Effect.CheckForAppUpdates -> checkForUpdates()

                    is MainContract.Effect.Navigation.NavRoute -> onNavigationRequested(effect)

                    is MainContract.Effect.Notification -> {
                        showAlerter(
                            message = effect.text,
                            isError = effect.error,
                        )
                    }
                }
            }.collect()
        }
    }

    @Composable
    private fun ObserveSnackbarEvents(
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        context: Context,
        onEventReceived: (SnackbarEvent) -> Unit,
    ) {
        ObserveAsEvents(
            flow = SnackbarController.events,
            key1 = snackbarHostState,
        ) { event ->
            onEventReceived(event)
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()

                val result = snackbarHostState.showSnackbar(
                    message = event.message.asString(context),
                    actionLabel = event.action?.name?.asString(context),
                    duration = event.duration,
                )

                if (result == SnackbarResult.ActionPerformed) {
                    event.action?.action?.invoke()
                }
            }
        }
    }

    @Composable
    private fun ObserveDialogEvents(eventDialog: EventDialogState) {
        ObserveAsEvents(
            flow = DialogController.events,
        ) { event ->
            eventDialog.show(dialogEvent = event)
        }
    }

    @Composable
    private fun determineDarkMode(appTheme: AppTheme): Boolean = when (appTheme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.System -> isSystemInDarkTheme()
    }

    override fun onResume() {
        super.onResume()
        if (AppConfig.IS_PLAY_BUILD) {
            appUpdateManager.resumeUpdateIfNeeded(this)
        }
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.setEvent(MainContract.Event.StoreLatestDataState)
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.detach(this)
        if (AppConfig.IS_PLAY_BUILD) {
            appUpdateManager.unregister()
        }
    }

    private fun checkForUpdates() {
        lifecycleScope.launch {
            appUpdateManager.checkForUpdate()
        }
    }
}
