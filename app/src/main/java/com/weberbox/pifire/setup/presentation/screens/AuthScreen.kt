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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.outlined.CloudKeyOutline
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.base.getPagerAnimationSpec
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.Credentials
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.modifier.limitWidthFraction
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.state.rememberInputModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.landing.presentation.sheets.HeadersSheet
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.ExtraHeader
import com.weberbox.pifire.setup.presentation.component.SetupBottomNavRow
import com.weberbox.pifire.setup.presentation.contract.AuthContract
import com.weberbox.pifire.setup.presentation.model.SetupStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun AuthScreenDestination(
    pagerState: PagerState,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val scope = rememberCoroutineScope()
    AuthScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is AuthContract.Effect.Navigation.Forward -> {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = SetupStep.Connect.ordinal,
                            animationSpec = getPagerAnimationSpec()
                        )
                    }
                }
                is AuthContract.Effect.Navigation.Back -> onBackDispatcher?.onBackPressed()
            }
        },
    )
}

@Composable
private fun AuthScreen(
    state: AuthContract.State,
    effectFlow: Flow<AuthContract.Effect>?,
    onEventSent: (event: AuthContract.Event) -> Unit,
    onNavigationRequested: (AuthContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val headersSheet = rememberInputModalBottomSheetState<ExtraHeader>()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is AuthContract.Effect.Navigation -> onNavigationRequested(effect)
                is AuthContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
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
                imageVector = Icons.Filled.LockOpen,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.setup_auth_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.setup_auth_text),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.basic_auth),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(Modifier.width(MaterialTheme.size.extraLargeTwo))
                Spacer(Modifier.height(MaterialTheme.spacing.smallThree))
                OutlineFieldWithState(
                    modifier = Modifier
                        .semantics { contentType = ContentType.Username }
                        .fillMaxWidth(),
                    placeholder = stringResource(R.string.username),
                    leadingIcon = Icons.Filled.Person,
                    fieldInput = state.credentials.username.input,
                    errorStatus = state.credentials.username.error,
                    onValueChange = {
                        onEventSent(AuthContract.Event.UpdateUsername(it))
                    }
                )
                OutlineFieldWithState(
                    modifier = Modifier
                        .semantics { contentType = ContentType.Password }
                        .fillMaxWidth(),
                    placeholder = stringResource(R.string.password),
                    leadingIcon = Icons.Filled.Key,
                    isPasswordField = true,
                    fieldInput = state.credentials.password.input,
                    errorStatus = state.credentials.password.error,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        capitalization = KeyboardCapitalization.None
                    ),
                    onValueChange = {
                        onEventSent(AuthContract.Event.UpdatePassword(it))
                    }
                )
                Text(
                    text = stringResource(R.string.extra_headers),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(Modifier.width(MaterialTheme.size.extraLargeTwo))
                Spacer(Modifier.height(MaterialTheme.spacing.smallThree))
                state.extraHeaders.forEach{ header ->
                    Card(
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallTwo),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            headersSheet.open(header)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.spacing.smallOne)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icon.Outlined.CloudKeyOutline,
                                modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne),
                                contentDescription = null
                            )
                            Column {
                                Text(
                                    text = header.key,
                                    modifier = Modifier.padding(
                                        start = MaterialTheme.spacing.extraSmall,
                                        top = MaterialTheme.spacing.extraSmall
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = header.value,
                                    modifier = Modifier.padding(
                                        start = MaterialTheme.spacing.extraSmall,
                                        bottom = MaterialTheme.spacing.extraSmall
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        headersSheet.open(ExtraHeader())
                    }
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.add_extra_header),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        SetupBottomNavRow(
            onBackClick = { onNavigationRequested(AuthContract.Effect.Navigation.Back) },
            onNextClick = { onEventSent(AuthContract.Event.NavigateToConnect) }
        )

        BottomSheet(
            sheetState = headersSheet.sheetState
        ) {
            HeadersSheet(
                extraHeader = headersSheet.data,
                onUpdate = { header ->
                    headersSheet.close()
                    onEventSent(AuthContract.Event.UpdateHeader(header))
                },
                onDelete = { header ->
                    headersSheet.close()
                    onEventSent(AuthContract.Event.DeleteHeader(header))
                }
            )
        }
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
internal fun AuthScreenPreview() {
    PiFireTheme {
        Surface {
            AuthScreen(
                state = AuthContract.State(
                    credentials = Credentials(
                        username = InputState(
                            input = FieldInput(
                                value = "Test",
                                hasInteracted = true
                            )
                        )
                    ),
                    extraHeaders = listOf(
                        ExtraHeader(
                            key = "Test",
                            value = "Test"
                        )
                    )
                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {}
            )
        }
    }
}