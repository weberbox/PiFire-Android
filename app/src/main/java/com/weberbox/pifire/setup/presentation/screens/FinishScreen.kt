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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.OutdoorGrill
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.modifier.limitWidthFraction
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.common.presentation.component.CircularLoadingIndicator
import com.weberbox.pifire.setup.presentation.component.SetupBottomNavRow
import com.weberbox.pifire.setup.presentation.contract.FinishContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun FinishScreenDestination(
    viewModel: FinishViewModel = hiltViewModel(),
    onNavigateLanding: () -> Unit
) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    FinishScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is FinishContract.Effect.Navigation.Forward -> onNavigateLanding()
                is FinishContract.Effect.Navigation.Back -> onBackDispatcher?.onBackPressed()
            }
        }
    )
}

@Composable
private fun FinishScreen(
    state: FinishContract.State,
    effectFlow: Flow<FinishContract.Effect>?,
    onEventSent: (event: FinishContract.Event) -> Unit,
    onNavigationRequested: (FinishContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is FinishContract.Effect.Navigation -> onNavigationRequested(effect)

                is FinishContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
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
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.setup_finish_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.setup_finish_text),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                OutlineFieldWithState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.medium),
                    enabled = !state.isLoading,
                    placeholder = stringResource(R.string.setup_grill_name),
                    leadingIcon = Icons.Outlined.OutdoorGrill,
                    fieldInput = state.grillName.input,
                    errorStatus = state.grillName.error,
                    onValueChange = {
                        onEventSent(FinishContract.Event.ValidateName(it))
                    }
                )
            }
            Spacer(Modifier.weight(1f))
            SetupBottomNavRow(
                enabled = !state.grillName.error.isError && state.grillName.input.hasInteracted,
                onBackClick = { onNavigationRequested(FinishContract.Effect.Navigation.Back) },
                onNextClick = {
                    onEventSent(FinishContract.Event.SaveGrillName(state.grillName.input.value))
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
internal fun FinishScreenPreview() {
    PiFireTheme {
        Surface {
            FinishScreen(
                state = FinishContract.State(
                    grillName = InputState(),
                    isLoading = false
                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {}
            )
        }
    }
}