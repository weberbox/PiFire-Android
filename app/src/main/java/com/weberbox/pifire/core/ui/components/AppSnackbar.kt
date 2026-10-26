package com.weberbox.pifire.core.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.spacing

@Composable
fun AppSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(snackbarHostState) { data ->
        AppSnackbar(data = data)
    }
}

@Composable
fun AppSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    Snackbar(
        modifier = modifier.padding(MaterialTheme.spacing.smallTwo),
        action = data.visuals.actionLabel?.let { actionLabel ->
            {
                TextButton(onClick = { data.performAction() }) {
                    Text(
                        text = actionLabel,
                        color = MaterialTheme.colorScheme.inversePrimary,
                    )
                }
            }
        },
        dismissAction = if (data.visuals.duration == SnackbarDuration.Indefinite) {
            {
                TextButton(onClick = { data.dismiss() }) {
                    Text(
                        text = stringResource(R.string.dismiss),
                        color = MaterialTheme.colorScheme.inversePrimary,
                    )
                }
            }
        } else {
            null
        },
    ) {
        Text(data.visuals.message)
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun AppSnackbarPreview() {
    val mockData = object : SnackbarData {
        override val visuals = object : SnackbarVisuals {
            override val message: String = "App update completed"
            override val actionLabel: String = "Cancel"
            override val withDismissAction: Boolean = false
            override val duration: SnackbarDuration = SnackbarDuration.Long
        }

        override fun performAction() {}
        override fun dismiss() {}
    }

    PiFireTheme {
        Surface {
            AppSnackbar(data = mockData)
        }
    }
}
