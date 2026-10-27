package com.weberbox.pifire.application

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.AppSnackbarHost
import com.weberbox.pifire.core.ui.components.EventAlertDialog
import com.weberbox.pifire.core.ui.components.message.MessageHost
import com.weberbox.pifire.core.ui.components.message.MessageHostOwner
import com.weberbox.pifire.core.ui.components.message.MessageHostState
import com.weberbox.pifire.core.ui.model.AppTheme
import com.weberbox.pifire.core.ui.state.rememberEventDialogState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.ObserveAsEvents
import com.weberbox.pifire.core.ui.util.SnackbarController
import com.weberbox.pifire.core.ui.util.UiMessage
import com.weberbox.pifire.core.ui.util.showMessage
import com.weberbox.pifire.feature.update.data.installer.PlayAppInstaller
import com.weberbox.pifire.feature.update.presentation.screen.AppUpdateScreenDest
import com.weberbox.pifire.navigation.RootNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MessageHostOwner {
    @Inject
    lateinit var playAppInstaller: PlayAppInstaller

    override val messageHost = MessageHostState()

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (AppConfig.IS_PLAY_BUILD) playAppInstaller.bind(this)

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
        )

        PiFireTheme(
            darkTheme = determineDarkMode(state.appTheme),
            dynamicColor = state.dynamicColor,
            featureSupport = state.featureSupport,
        ) {
            Scaffold(
                snackbarHost = {
                    AppSnackbarHost(snackbarHostState = snackbarHostState)
                },
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    EventAlertDialog(eventDialogState = eventDialog)
                    RootNavGraph(
                        navController = navController,
                    )
                    // After the nav graph, so a required update covers it.
                    AppUpdateScreenDest()
                    // Last, so messages draw over the nav graph and the update screen.
                    MessageHost(
                        state = messageHost,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                }
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
                    is MainContract.Effect.Navigation.NavRoute -> onNavigationRequested(effect)

                    is MainContract.Effect.Notification -> {
                        showMessage(
                            message = UiMessage.of(text = effect.text, isError = effect.error),
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
    ) {
        ObserveAsEvents(
            flow = SnackbarController.events,
            key1 = snackbarHostState,
        ) { event ->
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
    private fun determineDarkMode(appTheme: AppTheme): Boolean = when (appTheme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.System -> isSystemInDarkTheme()
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.setEvent(MainContract.Event.StoreLatestDataState)
    }
}
