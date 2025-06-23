package com.weberbox.pifire.setup.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.base.getPagerAnimationSpec
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.modifier.limitWidthFraction
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.common.presentation.component.CircularLoadingIndicator
import com.weberbox.pifire.setup.presentation.component.SetupBottomNavRow
import com.weberbox.pifire.setup.presentation.contract.ConnectContract
import com.weberbox.pifire.setup.presentation.model.SetupStep
import com.weberbox.pifire.setup.presentation.util.isCameraAvailable
import com.weberbox.pifire.setup.presentation.util.isGooglePlayServicesAvailable
import com.weberbox.pifire.setup.presentation.util.openQRCodeScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun ConnectScreenDestination(
    pagerState: PagerState,
    viewModel: ConnectViewModel = hiltViewModel()
) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val scope = rememberCoroutineScope()
    ConnectScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ConnectContract.Effect.Navigation.Forward -> {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = SetupStep.Push.ordinal,
                            animationSpec = getPagerAnimationSpec()
                        )
                    }
                }

                is ConnectContract.Effect.Navigation.Back -> onBackDispatcher?.onBackPressed()
            }
        }
    )
}

@Composable
private fun ConnectScreen(
    state: ConnectContract.State,
    effectFlow: Flow<ConnectContract.Effect>?,
    onEventSent: (event: ConnectContract.Event) -> Unit,
    onNavigationRequested: (ConnectContract.Effect.Navigation) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val cameraAvailable by remember { mutableStateOf(isCameraAvailable(context)) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is ConnectContract.Effect.Navigation -> onNavigationRequested(effect)
                is ConnectContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }

                is ConnectContract.Effect.Dialog -> {
                    scope.launch {
                        DialogController.sendEvent(
                            event = DialogEvent(
                                title = effect.dialogEvent.title,
                                message = effect.dialogEvent.message,
                                positiveAction = DialogAction(
                                    buttonText = effect.dialogEvent.positiveAction.buttonText,
                                    action = {
                                        effect.dialogEvent.positiveAction.action.invoke()
                                    }
                                )
                            )
                        )
                    }
                }
            }
        }?.collect()
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .limitWidthFraction()
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(top = MaterialTheme.spacing.largeTwo)
                    .padding(MaterialTheme.spacing.smallThree),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
                    imageVector = Icons.Filled.OutdoorGrill,
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.setup_connect_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.setup_connect_text),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                OutlineFieldWithState(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    placeholder = stringResource(R.string.setup_server_address),
                    leadingIcon = Icons.Filled.Dns,
                    fieldInput = state.serverAddress.input,
                    errorStatus = state.serverAddress.error,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                    onValueChange = {
                        onEventSent(ConnectContract.Event.ValidateAddress(it))
                    }
                )
                if (cameraAvailable) {
                    val googlePlayAvailable by remember {
                        mutableStateOf(isGooglePlayServicesAvailable(context))
                    }
                    IconButton(
                        modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
                        onClick = {
                            if (!state.isLoading) {
                                if (googlePlayAvailable) {
                                    openQRCodeScanner(activity, onEventSent)
                                } else {
                                    activity?.showAlerter(
                                        message = UiText(R.string.setup_error_play_services)
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = stringResource(R.string.setup_scan_qr)
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            SetupBottomNavRow(
                enabled = !state.serverAddress.error.isError &&
                        state.serverAddress.input.hasInteracted,
                onBackClick = { onNavigationRequested(ConnectContract.Effect.Navigation.Back) },
                onNextClick = {
                    onEventSent(ConnectContract.Event.GetServerVersions)
                }
            )
        }
        CircularLoadingIndicator(
            isLoading = state.isLoading
        )
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
internal fun ConnectScreenPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(12.dp)
            ) {
                ConnectScreen(
                    state = ConnectContract.State(
                        serverAddress = InputState(),
                        isLoading = false
                    ),
                    effectFlow = null,
                    onEventSent = {},
                    onNavigationRequested = {}
                )
            }
        }
    }
}