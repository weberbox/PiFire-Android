package com.weberbox.pifire.feature.update.presentation.screen

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import com.weberbox.pifire.R
import com.weberbox.pifire.core.constants.Constants
import com.weberbox.pifire.core.ui.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.UiMessage
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.core.ui.util.showMessage
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.UpdateSource
import com.weberbox.pifire.feature.update.presentation.components.UpdateRequiredContent
import com.weberbox.pifire.feature.update.presentation.components.UpdateSheetContent
import com.weberbox.pifire.feature.update.presentation.contract.AppUpdateContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

/**
 * App updates over the whole app (in MainActivity, above the nav graph): a required update covers
 * the app until it's installed, and a newer version is offered in a sheet. Not a nav destination,
 * so there's no navigation to handle.
 */
@Composable
fun AppUpdateScreenDest(viewModel: AppUpdateViewModel = hiltViewModel()) {
    LifecycleResumeEffect(viewModel) {
        viewModel.setEvent(AppUpdateContract.Event.OnAppResumed)
        onPauseOrDispose { }
    }
    AppUpdateScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
    )
}

@Composable
private fun AppUpdateScreen(
    state: AppUpdateContract.State,
    effectFlow: Flow<AppUpdateContract.Effect>?,
    onEventSent: (AppUpdateContract.Event) -> Unit,
) {
    HandleSideEffects(effectFlow = effectFlow)

    if (state.isRequired) {
        // Back does nothing: there's no app to go back to until it's updated.
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
        ) {
            AppUpdateScreenContent(
                state = state,
                onEventSent = onEventSent,
            )
        }
    }

    UpdateSheet(
        state = state,
        onEventSent = onEventSent,
    )
}

@Composable
private fun AppUpdateScreenContent(
    state: AppUpdateContract.State,
    onEventSent: (AppUpdateContract.Event) -> Unit,
) {
    UpdateRequiredContent(
        state = state,
        onUpdateClick = { onEventSent(AppUpdateContract.Event.OnUpdateClicked) },
        onInstallClick = { onEventSent(AppUpdateContract.Event.OnInstallClicked) },
        onOpenStoreClick = { onEventSent(AppUpdateContract.Event.OnOpenStoreClicked) },
        onCheckAgainClick = { onEventSent(AppUpdateContract.Event.OnCheckAgainClicked) },
    )
}

@Composable
private fun UpdateSheet(
    state: AppUpdateContract.State,
    onEventSent: (AppUpdateContract.Event) -> Unit,
) {
    val isVisible = state.sheet != null && !state.isRequired
    val sheetState = rememberModalBottomSheetState(
        initialDetent = if (isVisible) SheetDetent.FullyExpanded else SheetDetent.Hidden,
    )
    val currentIsVisible by rememberUpdatedState(isVisible)
    val currentOnEventSent by rememberUpdatedState(onEventSent)

    LaunchedEffect(isVisible) {
        sheetState.targetDetent = if (isVisible) SheetDetent.FullyExpanded else SheetDetent.Hidden
    }
    // Swiped away or the scrim tapped: closed by the user, not by the state.
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentDetent }
            .drop(1)
            .filter { it == SheetDetent.Hidden && currentIsVisible }
            .collect { currentOnEventSent(AppUpdateContract.Event.OnSheetDismissed) }
    }

    // The last sheet shown stays while it slides away.
    var shownSheet by remember { mutableStateOf(state.sheet) }
    if (state.sheet != null && state.sheet != shownSheet) shownSheet = state.sheet

    BottomSheet(sheetState = sheetState) {
        shownSheet?.let { sheet ->
            UpdateSheetContent(
                sheet = sheet,
                state = state,
                onUpdateClick = { onEventSent(AppUpdateContract.Event.OnUpdateClicked) },
                onInstallClick = { onEventSent(AppUpdateContract.Event.OnInstallClicked) },
                onDismissClick = { onEventSent(AppUpdateContract.Event.OnSheetDismissed) },
            )
        }
    }
}

@Composable
private fun HandleSideEffects(effectFlow: Flow<AppUpdateContract.Effect>?) {
    val activity = LocalActivity.current
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is AppUpdateContract.Effect.OpenStoreListing -> {
                    activity?.openStoreListing()
                }

                is AppUpdateContract.Effect.OpenReleasePage -> {
                    activity?.openLink(url = Constants.GITHUB_RELEASE_LINK)
                }
            }
        }?.collect()
    }
}

/** The Play Store app's listing, or the web one when the Play Store isn't installed. */
private fun Activity.openStoreListing() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()))
    } catch (e: ActivityNotFoundException) {
        Timber.w(e, "No Play Store app, opening the web listing")
        openLink(url = Constants.PLAY_RELEASE_LINK)
    }
}

private fun Activity.openLink(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (e: ActivityNotFoundException) {
        Timber.w(e, "No activity to open the update link")
        showMessage(
            message = UiMessage.error(text = UiText(R.string.update_no_browser)),
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun AppUpdateScreenContentPreview() {
    PiFireTheme {
        AppUpdateScreenContent(
            state = AppUpdateContract.State(
                isRequired = true,
                update = AvailableUpdate(versionCode = 3040000),
                message = "Critical fix for the dashboard.",
                source = UpdateSource.GITHUB,
            ),
            onEventSent = {},
        )
    }
}
