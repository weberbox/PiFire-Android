package com.weberbox.pifire.common.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.modifier.limitWidthFraction
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
fun EmptyItems(
    modifier: Modifier = Modifier,
    title: String,
    summary: String,
    icon: ImageVector,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .padding(bottom = MaterialTheme.spacing.smallThree)
                .size(MaterialTheme.spacing.largeIcon),
            imageVector = icon,
            contentDescription = null
        )
        Text(
            modifier = Modifier.limitWidthFraction(),
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = summary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(MaterialTheme.spacing.smallThree),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
fun EmptyItemsPreview() {
    PiFireTheme {
        Surface {
            EmptyItems(
                modifier = Modifier,
                icon = Icons.Filled.NoFood,
                title = stringResource(R.string.error_no_results_landing_title),
                summary = stringResource(R.string.error_no_results_landing_summary),
            )
        }
    }
}