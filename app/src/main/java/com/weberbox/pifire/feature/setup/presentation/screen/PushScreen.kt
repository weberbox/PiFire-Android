package com.weberbox.pifire.feature.setup.presentation.screen

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.base.getPagerAnimationSpec
import com.weberbox.pifire.core.ui.modifier.limitWidthFraction
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.size
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.NOTIFICATIONS_PERMISSION
import com.weberbox.pifire.core.ui.util.PermissionState
import com.weberbox.pifire.core.ui.util.UiMessage
import com.weberbox.pifire.core.ui.util.rememberPermissionState
import com.weberbox.pifire.core.ui.util.showMessage
import com.weberbox.pifire.feature.setup.presentation.components.SetupBottomNavRow
import com.weberbox.pifire.feature.setup.presentation.contract.PushContract
import com.weberbox.pifire.feature.setup.presentation.model.SetupStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun PushScreenDestination(
    pagerState: PagerState,
    viewModel: PushViewModel = hiltViewModel(),
) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val scope = rememberCoroutineScope()
    PushScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is PushContract.Effect.Navigation.Forward -> {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = SetupStep.Finish.ordinal,
                            animationSpec = getPagerAnimationSpec(),
                        )
                    }
                }

                is PushContract.Effect.Navigation.Back -> onBackDispatcher?.onBackPressed()
            }
        },
    )
}

@Composable
private fun PushScreen(
    state: PushContract.State,
    effectFlow: Flow<PushContract.Effect>?,
    onEventSent: (event: PushContract.Event) -> Unit,
    onNavigationRequested: (PushContract.Effect.Navigation) -> Unit,
) {
    val notificationPermission = rememberPermissionState(
        spec = NOTIFICATIONS_PERMISSION,
        onResult = { granted ->
            if (granted && state.isAppPush) onEventSent(PushContract.Event.AllowAppPush)
        },
    )

    HandleSideEffects(
        effectFlow = effectFlow,
        notificationPermission = notificationPermission,
        onNavigationRequested = onNavigationRequested,
    )

    PushScreenContent(
        state = state,
        onEventSent = onEventSent,
        onNavigationRequested = onNavigationRequested,
        onAllowAppPush = {
            if (notificationPermission.isGranted) {
                onEventSent(PushContract.Event.AllowAppPush)
            } else {
                notificationPermission.request()
            }
        },
    )
}

@Composable
private fun PushScreenContent(
    state: PushContract.State,
    onEventSent: (event: PushContract.Event) -> Unit,
    onNavigationRequested: (PushContract.Effect.Navigation) -> Unit,
    onAllowAppPush: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.isAppPush) {
            AppPushContent(
                isAllowed = state.isAppPushAllowed,
                onAllowAppPush = onAllowAppPush,
            )
        } else {
            OneSignalConsentContent(
                consent = state.consent,
                onEventSent = onEventSent,
            )
        }
        Spacer(Modifier.weight(1f))

        SetupBottomNavRow(
            text = if (state.isAppPush && !state.isAppPushAllowed) {
                stringResource(R.string.skip)
            } else {
                stringResource(R.string.next)
            },
            enabled = !state.isLoading,
            onBackClick = { onNavigationRequested(PushContract.Effect.Navigation.Back) },
            onNextClick = { onEventSent(PushContract.Event.NavigateToFinish) },
        )
    }
}

@Composable
private fun AppPushContent(
    isAllowed: Boolean,
    onAllowAppPush: () -> Unit,
) {
    Column(
        modifier = Modifier
            .limitWidthFraction()
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.largeTwo)
            .padding(MaterialTheme.spacing.smallThree),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
            imageVector = Icons.Outlined.NotificationsActive,
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.setup_app_push_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.setup_app_push_text),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.setup_app_push_privacy),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.small),
            onClick = onAllowAppPush,
            enabled = !isAllowed,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.smallThree),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = null,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAllowed) {
                            stringResource(R.string.setup_app_push_on_title)
                        } else {
                            stringResource(R.string.setup_app_push_allow_title)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (isAllowed) {
                            stringResource(R.string.setup_app_push_on_summary)
                        } else {
                            stringResource(R.string.setup_app_push_allow_summary)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Icon(
                    imageVector = if (isAllowed) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.AutoMirrored.Filled.KeyboardArrowRight
                    },
                    contentDescription = null,
                )
            }
        }
        Text(
            text = stringResource(R.string.setup_app_push_later),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OneSignalConsentContent(
    consent: Boolean,
    onEventSent: (event: PushContract.Event) -> Unit,
) {
    Column(
        modifier = Modifier
            .limitWidthFraction()
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = MaterialTheme.spacing.largeTwo)
            .padding(MaterialTheme.spacing.smallThree),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
            imageVector = Icons.Outlined.NotificationsActive,
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.setup_push_consent_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.settings_onesignal_consent_1),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.settings_onesignal_consent_2),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.settings_onesignal_consent_3),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier
                .height(MaterialTheme.size.extraLarge)
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (consent) {
                    stringResource(R.string.settings_onesignal_accepted)
                } else {
                    stringResource(R.string.settings_onesignal_declined)
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = consent,
                thumbContent = {
                    Icon(
                        modifier = Modifier.padding(MaterialTheme.spacing.extraSmall),
                        imageVector = if (consent) {
                            Icons.Filled.Check
                        } else {
                            Icons.Filled.Close
                        },
                        tint = if (consent) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.inverseOnSurface
                        },
                        contentDescription = null,
                    )
                },
                onCheckedChange = {
                    onEventSent(PushContract.Event.ToggleConsent(it))
                },
            )
        }
    }
}

@Composable
private fun HandleSideEffects(
    effectFlow: Flow<PushContract.Effect>?,
    notificationPermission: PermissionState,
    onNavigationRequested: (PushContract.Effect.Navigation) -> Unit,
) {
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PushContract.Effect.RequestPermission ->
                    notificationPermission.request()

                is PushContract.Effect.Navigation -> onNavigationRequested(effect)

                is PushContract.Effect.Notification -> {
                    activity?.showMessage(
                        message = UiMessage.of(text = effect.text, isError = effect.error),
                    )
                }

                is PushContract.Effect.Dialog -> {
                    scope.launch {
                        DialogController.sendEvent(
                            event = DialogEvent(
                                title = effect.dialogEvent.title,
                                message = effect.dialogEvent.message,
                                positiveAction = DialogAction(
                                    buttonText = effect.dialogEvent.positiveAction.buttonText,
                                    action = {
                                        effect.dialogEvent.positiveAction.action.invoke()
                                    },
                                ),
                            ),
                        )
                    }
                }
            }
        }?.collect()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
internal fun PushScreenPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                PushScreen(
                    state = PushContract.State(
                        consent = true,
                        isAppPush = false,
                        isAppPushAllowed = false,
                        isLoading = false,
                    ),
                    effectFlow = null,
                    onEventSent = {},
                    onNavigationRequested = {},
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PushScreenAppPushPreview() {
    PiFireTheme {
        Surface {
            PushScreen(
                state = PushContract.State(
                    consent = false,
                    isAppPush = true,
                    isAppPushAllowed = false,
                    isLoading = false,
                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {},
            )
        }
    }
}
