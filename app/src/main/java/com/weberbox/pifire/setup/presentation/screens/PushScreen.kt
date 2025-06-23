package com.weberbox.pifire.setup.presentation.screens

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
import androidx.compose.material.icons.outlined.NotificationsActive
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.base.getPagerAnimationSpec
import com.weberbox.pifire.common.presentation.modifier.limitWidthFraction
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.setup.presentation.component.SetupBottomNavRow
import com.weberbox.pifire.setup.presentation.contract.PushContract
import com.weberbox.pifire.setup.presentation.model.SetupStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
fun PushScreenDestination(
    pagerState: PagerState,
    viewModel: PushViewModel = hiltViewModel()
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
                            animationSpec = getPagerAnimationSpec()
                        )
                    }
                }
                is PushContract.Effect.Navigation.Back -> onBackDispatcher?.onBackPressed()
            }
        }
    )
}

@Composable
private fun PushScreen(
    state: PushContract.State,
    effectFlow: Flow<PushContract.Effect>?,
    onEventSent: (event: PushContract.Event) -> Unit,
    onNavigationRequested: (PushContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is PushContract.Effect.Navigation -> onNavigationRequested(effect)
                is PushContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
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
                                    }
                                )
                            )
                        )
                    }
                }
            }
        }?.collect()
    }

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
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.setup_push_consent_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.settings_onesignal_consent_1),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.settings_onesignal_consent_2),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.settings_onesignal_consent_3),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .height(MaterialTheme.size.extraLarge)
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.consent)
                        stringResource(R.string.settings_onesignal_accepted) else
                        stringResource(R.string.settings_onesignal_declined)
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = state.consent,
                    onCheckedChange = {
                        onEventSent(PushContract.Event.ToggleConsent(it))
                    }
                )
            }
        }
        Spacer(Modifier.weight(1f))

        SetupBottomNavRow(
            onBackClick = { onNavigationRequested(PushContract.Effect.Navigation.Back) },
            onNextClick = { onEventSent(PushContract.Event.NavigateToFinish) }
        )
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
                        consent = true
                    ),
                    effectFlow = null,
                    onEventSent = {},
                    onNavigationRequested = {}
                )
            }
        }
    }
}