package com.weberbox.pifire.feature.update.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.core.ui.util.asString
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.presentation.contract.AppUpdateContract
import com.weberbox.pifire.feature.update.presentation.model.UpdateSheet

/** An update that isn't required: offered, downloading, ready to finish, or failed. */
@Composable
fun UpdateSheetContent(
    sheet: UpdateSheet,
    state: AppUpdateContract.State,
    onUpdateClick: () -> Unit,
    onInstallClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.mediumOne,
                end = MaterialTheme.spacing.mediumOne,
                bottom = MaterialTheme.spacing.smallThree,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(
                id = when (sheet) {
                    UpdateSheet.OFFER -> R.string.update_available_title
                    UpdateSheet.DOWNLOADING -> R.string.update_downloading_title
                    UpdateSheet.READY -> R.string.update_ready_title
                    UpdateSheet.FAILED -> R.string.update_failed_title
                },
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.smallThree))
        when (sheet) {
            UpdateSheet.OFFER -> {
                SheetText(text = stringResource(id = R.string.update_available_message))
                UpdateDetails(
                    update = state.update,
                    message = state.message,
                    source = state.source,
                )
            }

            UpdateSheet.DOWNLOADING -> {
                UpdateProgress(progress = state.progress)
            }

            UpdateSheet.READY -> {
                SheetText(text = stringResource(id = R.string.update_ready_message))
            }

            UpdateSheet.FAILED -> {
                SheetText(
                    text = (state.progress as? InstallProgress.Failed)?.message?.asString()
                        ?: stringResource(id = R.string.update_failed_message),
                )
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.mediumOne))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne),
        ) {
            OutlinedButton(
                onClick = onDismissClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(
                        id = when (sheet) {
                            UpdateSheet.OFFER, UpdateSheet.READY -> R.string.update_later
                            UpdateSheet.DOWNLOADING -> R.string.update_hide
                            UpdateSheet.FAILED -> R.string.close
                        },
                    ),
                    fontWeight = FontWeight.Bold,
                )
            }
            val primary = when (sheet) {
                UpdateSheet.OFFER -> R.string.update_now to onUpdateClick
                UpdateSheet.READY -> R.string.update_restart to onInstallClick
                UpdateSheet.FAILED -> R.string.update_try_again to onUpdateClick
                UpdateSheet.DOWNLOADING -> null
            }
            primary?.let { (textRes, onClick) ->
                Button(
                    onClick = onClick,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(id = textRes),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun UpdateOfferPreview() {
    PiFireTheme {
        Surface {
            UpdateSheetContent(
                sheet = UpdateSheet.OFFER,
                state = AppUpdateContract.State(
                    update = AvailableUpdate(versionCode = 3040000, sizeBytes = 18_500_000),
                    message = "Bugfixes and improvements.",
                ),
                onUpdateClick = {},
                onInstallClick = {},
                onDismissClick = {},
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun UpdateDownloadingPreview() {
    PiFireTheme {
        Surface {
            UpdateSheetContent(
                sheet = UpdateSheet.DOWNLOADING,
                state = AppUpdateContract.State(progress = InstallProgress.Downloading(fraction = 0.45f)),
                onUpdateClick = {},
                onInstallClick = {},
                onDismissClick = {},
            )
        }
    }
}
