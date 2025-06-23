package com.weberbox.pifire.common.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
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
import com.weberbox.pifire.common.presentation.base.gradientBackground
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
fun CachedDataError(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    ErrorScreen(
        modifier = modifier,
        title = stringResource(R.string.error_no_cached_data_title),
        summary = stringResource(R.string.error_no_cached_data_description),
        buttonText = stringResource(R.string.try_again),
        icon = Icons.Filled.ErrorOutline,
        onButtonClick = onButtonClick,
    )
}

@Composable
fun DataError(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    ErrorScreen(
        modifier = modifier,
        title = stringResource(R.string.error_data_title),
        summary = stringResource(R.string.error_data_description),
        buttonText = stringResource(R.string.go_back),
        icon = Icons.Filled.ErrorOutline,
        onButtonClick = onButtonClick,
    )
}

@Suppress("unused")
@Composable
fun NetworkError(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    ErrorScreen(
        modifier = modifier,
        title = stringResource(R.string.error_network_title),
        summary = stringResource(R.string.error_network_description),
        buttonText = stringResource(R.string.try_again),
        icon = Icons.Filled.ErrorOutline,
        onButtonClick = onButtonClick,
    )
}

@Composable
private fun ErrorScreen(
    modifier: Modifier = Modifier,
    title: String,
    summary: String,
    buttonText: String,
    icon: ImageVector = Icons.Filled.ErrorOutline,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(gradientBackground())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .padding(bottom = MaterialTheme.spacing.smallThree)
                .size(MaterialTheme.spacing.extraLargeIcon),
            imageVector = icon,
            contentDescription = null
        )
        Text(
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

        Button(onClick = onButtonClick) {
            Text(
                text = buttonText
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ErrorScreenPreview() {
    PiFireTheme {
        Surface {
            ErrorScreen(
                modifier = Modifier,
                title = stringResource(R.string.error_no_cached_data_title),
                summary = stringResource(R.string.error_no_cached_data_description),
                buttonText = stringResource(R.string.try_again),
                onButtonClick = {}
            )
        }
    }
}