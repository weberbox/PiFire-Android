package com.weberbox.pifire.settings.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
internal fun PreferenceNote(
    note: String,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick?.invoke() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.smallThree,
                    vertical = MaterialTheme.spacing.smallOne
                )
        ) {
            Text(
                text = stringResource(R.string.settings_note),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(horizontal = MaterialTheme.spacing.extraSmall),
                fontWeight = FontWeight.Bold

            )
            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
internal fun PreferenceWarning(
    warning: String,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick?.invoke() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.smallThree,
                    vertical = MaterialTheme.spacing.smallOne
                )
        ) {
            Text(
                text = stringResource(R.string.settings_warning),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = MaterialTheme.spacing.extraSmall),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = warning,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
internal fun PreferenceDanger(
    warning: String,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick?.invoke() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.smallThree,
                    vertical = MaterialTheme.spacing.smallOne
                )
        ) {
            Text(
                text = stringResource(R.string.settings_danger),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = MaterialTheme.spacing.extraSmall),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = warning,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
internal fun PreferenceInfo(
    info: String,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick?.invoke() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.smallThree,
                    vertical = MaterialTheme.spacing.smallOne
                )
        ) {
            Text(
                text = stringResource(R.string.settings_info),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = MaterialTheme.spacing.extraSmall),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = info,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun PreferenceNotePreview() {
    PiFireTheme {
        Surface {
            PreferenceNote(stringResource(R.string.settings_mqtt_user_note))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun PreferenceWarningPreview() {
    PiFireTheme {
        Surface {
            PreferenceWarning(stringResource(R.string.settings_mqtt_user_note))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun PreferenceDangerPreview() {
    PiFireTheme {
        Surface {
            PreferenceDanger(stringResource(R.string.settings_mqtt_user_note))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun PreferenceInfoPreview() {
    PiFireTheme {
        Surface {
            PreferenceInfo(stringResource(R.string.settings_mqtt_user_note))
        }
    }
}