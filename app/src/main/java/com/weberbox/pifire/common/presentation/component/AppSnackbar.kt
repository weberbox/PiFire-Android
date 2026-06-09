package com.weberbox.pifire.common.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.SnackbarController
import com.weberbox.pifire.common.presentation.util.SnackbarEvent

@Composable
fun AppSnackbarHost(
    snackbarHostState: SnackbarHostState,
    currentSnackbarEvent: SnackbarEvent?
) {
    SnackbarHost(snackbarHostState) { data ->
        val showProgress = currentSnackbarEvent?.showProgress == true
        val progress by SnackbarController.updateProgress.collectAsState()

        AppSnackbar(
            data = data,
            showProgress = showProgress,
            progress = progress
        )
    }
}

@Composable
fun ProgressSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    progress: Float = 0f
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.smallThree,
                vertical = MaterialTheme.spacing.small
            ),
        shape = RoundedCornerShape(MaterialTheme.spacing.mediumOne),
        tonalElevation = MaterialTheme.spacing.extraSmallOne,
        shadowElevation = MaterialTheme.spacing.small,
        color = MaterialTheme.colorScheme.inverseSurface
    ) {
        val actionLabel = data.visuals.actionLabel

        val buttonText = actionLabel
            ?: if (data.visuals.duration == SnackbarDuration.Indefinite) {
                stringResource(R.string.dismiss)
            } else {
                null
            }

        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.smallThree)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = data.visuals.message,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.bodyMedium
                )

                buttonText?.let { text ->
                    Spacer(Modifier.width(MaterialTheme.spacing.smallTwo))

                    TextButton(
                        onClick = {
                            if (actionLabel != null) {
                                data.performAction()
                            } else {
                                data.dismiss()
                            }
                        }
                    ) {
                        Text(
                            text = text,
                            color = MaterialTheme.colorScheme.inversePrimary
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showProgress,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                Column {
                    Spacer(Modifier.height(MaterialTheme.spacing.smallTwo))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.extraSmallOne),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
fun AppSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    progress: Float = 0f
) {
    Snackbar(
        modifier = modifier.padding(MaterialTheme.spacing.smallTwo),
        actionOnNewLine = showProgress,
        action = data.visuals.actionLabel?.let { actionLabel ->
            {
                TextButton(onClick = { data.performAction() }) {
                    Text(
                        text = actionLabel,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            }
        },
        dismissAction = if (data.visuals.duration == SnackbarDuration.Indefinite) {
            {
                TextButton(onClick = { data.dismiss() }) {
                    Text(
                        text = stringResource(R.string.dismiss),
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            }
        } else null
    ) {
        Column {
            Text(data.visuals.message)
            if (showProgress) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun AppSnackbarProgressPreview() {
    val mockData = object : SnackbarData {
        override val visuals = object : SnackbarVisuals {
            override val message: String = "Update is currently downloading"
            override val actionLabel: String? = null
            override val withDismissAction: Boolean = false
            override val duration: SnackbarDuration = SnackbarDuration.Indefinite
        }

        override fun performAction() {}
        override fun dismiss() {}
    }

    PiFireTheme {
        Surface {
            AppSnackbar(
                data = mockData,
                showProgress = true,
                progress = 0.45f
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun AppSnackbarProgressPreview2() {
    val mockData = object : SnackbarData {
        override val visuals = object : SnackbarVisuals {
            override val message: String = "Update is currently downloading"
            override val actionLabel: String? = null
            override val withDismissAction: Boolean = false
            override val duration: SnackbarDuration = SnackbarDuration.Long
        }

        override fun performAction() {}
        override fun dismiss() {}
    }

    PiFireTheme {
        Surface {
            ProgressSnackbar(
                data = mockData,
                showProgress = false,
                progress = 0.45f
            )
        }
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
            AppSnackbar(
                data = mockData,
                showProgress = false
            )
        }
    }
}
