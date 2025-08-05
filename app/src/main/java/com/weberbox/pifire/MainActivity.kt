package com.weberbox.pifire

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
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
import com.weberbox.pifire.common.presentation.state.EventDialogState
import com.weberbox.pifire.common.presentation.state.rememberEventDialogState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.ObserveAsEvents
import com.weberbox.pifire.common.presentation.util.SnackbarController
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.util.UpdateManager
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

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

        HandleSideEffects(
            effectFlow = effectFlow,
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is MainContract.Effect.Navigation.NavRoute ->
                        navController.safeNavigate(navigationEffect.route)
                }
            }
        )
        ObserveSnackbarEvents(
            snackbarHostState = snackbarHostState,
            scope = scope,
            context = context
        )
        ObserveDialogEvents(eventDialog = eventDialog)

        PiFireTheme(
            darkTheme = determineDarkMode(state.appTheme),
            dynamicColor = state.dynamicColor,
            featureSupport = state.featureSupport
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) {
                EventAlertDialog(eventDialogState = eventDialog)
                RootNavGraph(navController = navController)
            }
        }
    }

    @Composable
    private fun HandleSideEffects(
        effectFlow: Flow<MainContract.Effect>,
        onNavigationRequested: (MainContract.Effect.Navigation) -> Unit
    ) {
        LaunchedEffect(SIDE_EFFECTS_KEY) {
            effectFlow.onEach { effect ->
                when (effect) {
                    is MainContract.Effect.CheckForAppUpdates -> checkForUpdates()
                    is MainContract.Effect.Navigation.NavRoute -> onNavigationRequested(effect)
                    is MainContract.Effect.Notification -> {
                        showAlerter(
                            message = effect.text,
                            isError = effect.error
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
        context: Context
    ) {
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
    }

    @Composable
    private fun ObserveDialogEvents(eventDialog: EventDialogState) {
        ObserveAsEvents(
            flow = DialogController.events
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

    private fun checkForUpdates() {
        lifecycleScope.launch {
            appUpdateManager.checkForUpdate(this@MainActivity)
        }
    }
}