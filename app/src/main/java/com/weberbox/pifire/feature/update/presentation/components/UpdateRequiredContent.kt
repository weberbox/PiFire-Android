package com.weberbox.pifire.feature.update.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.size
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.asString
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.data.model.UpdateSource
import com.weberbox.pifire.feature.update.presentation.contract.AppUpdateContract

/**
 * The app is blocked until it updates: the release notes, how far the update got, and the one
 * thing to do next (update, restart, or open Google Play when it can't update here).
 */
@Composable
fun UpdateRequiredContent(
    state: AppUpdateContract.State,
    onUpdateClick: () -> Unit,
    onInstallClick: () -> Unit,
    onOpenStoreClick: () -> Unit,
    onCheckAgainClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.widthIn(max = MaterialTheme.size.maxWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.SystemUpdate,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.size.extraLarge),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.mediumTwo))
                Text(
                    text = stringResource(id = R.string.update_required_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallTwo))
                Text(
                    text = stringResource(id = R.string.update_required_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                UpdateDetails(
                    update = state.update,
                    message = state.message,
                    source = state.source,
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                RequiredActions(
                    state = state,
                    onUpdateClick = onUpdateClick,
                    onInstallClick = onInstallClick,
                    onOpenStoreClick = onOpenStoreClick,
                    onCheckAgainClick = onCheckAgainClick,
                )
            }
        }
    }
}

@Composable
private fun RequiredActions(
    state: AppUpdateContract.State,
    onUpdateClick: () -> Unit,
    onInstallClick: () -> Unit,
    onOpenStoreClick: () -> Unit,
    onCheckAgainClick: () -> Unit,
) {
    when (val progress = state.progress) {
        is InstallProgress.Downloading, InstallProgress.Installing -> {
            UpdateProgress(progress = progress)
        }

        InstallProgress.Downloaded -> {
            FullWidthButton(
                text = stringResource(id = R.string.update_restart),
                onClick = onInstallClick,
            )
        }

        is InstallProgress.Failed -> {
            Text(
                text = progress.message.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallThree))
            FullWidthButton(
                text = stringResource(id = R.string.update_try_again),
                onClick = onUpdateClick,
            )
        }

        InstallProgress.Idle -> {
            if (state.update != null) {
                FullWidthButton(
                    text = stringResource(id = R.string.update_now),
                    onClick = onUpdateClick,
                )
            } else {
                // Remote Config can require a release before Play offers it to this device.
                Text(
                    text = stringResource(id = R.string.update_play_not_ready),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallThree))
                FullWidthButton(
                    text = stringResource(id = R.string.update_open_play_store),
                    onClick = onOpenStoreClick,
                )
                TextButton(
                    onClick = onCheckAgainClick,
                    enabled = !state.isChecking,
                ) {
                    Text(text = stringResource(id = R.string.update_check_again))
                }
            }
        }
    }
}

@Composable
private fun FullWidthButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun UpdateRequiredDownloadingPreview() {
    PiFireTheme {
        UpdateRequiredContent(
            state = AppUpdateContract.State(
                isRequired = true,
                update = AvailableUpdate(versionCode = 3040000, sizeBytes = 24_000_000),
                message = "Critical fix for the dashboard.",
                progress = InstallProgress.Downloading(fraction = 0.4f),
            ),
            onUpdateClick = {},
            onInstallClick = {},
            onOpenStoreClick = {},
            onCheckAgainClick = {},
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun UpdateRequiredNotOnPlayPreview() {
    PiFireTheme {
        UpdateRequiredContent(
            state = AppUpdateContract.State(isRequired = true, source = UpdateSource.PLAY_STORE),
            onUpdateClick = {},
            onInstallClick = {},
            onOpenStoreClick = {},
            onCheckAgainClick = {},
        )
    }
}
