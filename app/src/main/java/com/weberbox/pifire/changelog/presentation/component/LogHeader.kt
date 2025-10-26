package com.weberbox.pifire.changelog.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
internal fun LogHeader(
    current: Boolean,
    version: String,
    date: String,
    isAlpha: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.smallOne)
    ) {
        Column {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "v$version",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (current) MaterialTheme.colorScheme.tertiaryContainer else
                        MaterialTheme.colorScheme.onSurface,
                    modifier = modifier.padding(start = MaterialTheme.spacing.extraSmallOne),
                    fontWeight = FontWeight.Bold

                )
                if (date.isNotBlank()) {
                    Text(
                        text = " - $date",
                        modifier = modifier.padding(end = MaterialTheme.spacing.extraSmallOne),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                if (isAlpha) {
                    Text(
                        text = stringResource(R.string.changelog_alpha_release),
                        modifier = modifier.padding(end = MaterialTheme.spacing.extraSmallOne),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (current) MaterialTheme.colorScheme.tertiaryContainer else
                            MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = modifier.padding(top = MaterialTheme.spacing.extraExtraSmall),
                thickness = 1.dp
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun LogHeaderPreview() {
    PiFireTheme {
        Surface {
            LogHeader(
                current = true,
                version = "3.0.0",
                date = "",
                isAlpha = true
            )
        }
    }
}