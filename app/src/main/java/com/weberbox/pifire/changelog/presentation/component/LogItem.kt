package com.weberbox.pifire.changelog.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.changelog.presentation.model.ChangelogData.Log
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
internal fun LogItem(
    log: Log,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.smallOne)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                modifier = modifier
                    .size(
                        width = MaterialTheme.size.largeTwo,
                        height = MaterialTheme.size.mediumOne
                    )
                    .padding(
                        top = MaterialTheme.spacing.extraExtraSmall,
                        end = MaterialTheme.spacing.extraSmallOne
                    ),
                painter = painterResource(log.icon),
                contentDescription = null
            )
            Text(
                text = log.text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = modifier.padding(start = MaterialTheme.spacing.extraSmall)
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun LogItemPreview() {
    val log by remember {
        mutableStateOf(
            Log(
                type = "new",
                text = "Added high and low probe notifications for supported PiFire versions"
            )
        )
    }
    PiFireTheme {
        Surface {
            LogItem(
                log = log
            )
        }
    }
}