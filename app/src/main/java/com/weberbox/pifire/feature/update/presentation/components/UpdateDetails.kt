package com.weberbox.pifire.feature.update.presentation.components

import android.text.format.Formatter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.data.model.UpdateSource

/** The download size, where the update comes from, and the release notes. */
@Composable
fun UpdateDetails(
    update: AvailableUpdate?,
    message: String?,
    source: UpdateSource,
) {
    val context = LocalContext.current
    val details = listOfNotNull(
        update?.sizeBytes?.let {
            stringResource(id = R.string.update_download_size, Formatter.formatShortFileSize(context, it))
        },
        stringResource(id = R.string.update_github_hint).takeIf { source == UpdateSource.GITHUB },
    )
    details.forEach { detail ->
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        Text(
            text = detail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
    if (message != null) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallThree))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

/** A download's progress bar and label, or "Installing…". */
@Composable
fun UpdateProgress(
    progress: InstallProgress,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val fraction = (progress as? InstallProgress.Downloading)?.fraction
        if (fraction != null) {
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        Text(
            text = when {
                progress is InstallProgress.Installing -> stringResource(id = R.string.update_installing)
                fraction != null -> stringResource(id = R.string.update_downloading_percent, (fraction * 100).toInt())
                else -> stringResource(id = R.string.update_downloading)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
