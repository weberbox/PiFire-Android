package com.weberbox.pifire.common.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.DialogProperties
import com.composeunstyled.UnstyledDialog
import com.composeunstyled.UnstyledDialogPanel
import com.composeunstyled.UnstyledScrim
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.state.EventDialogState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.asString
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.common.presentation.util.scaleEnterTransition

@Composable
fun EventAlertDialog(
    modifier: Modifier = Modifier,
    eventDialogState: EventDialogState
) {
    val event = eventDialogState.event ?: return
    UnstyledDialog(
        state = eventDialogState.state,
        properties = DialogProperties(
            dismissOnBackPress = event.dismissible,
            dismissOnClickOutside = event.dismissible
        ),
        onDismiss = { eventDialogState.dismiss() },
    ) {
        UnstyledScrim(
            enter = fadeEnterTransition(200),
            exit = fadeExitTransition(200)
        )
        UnstyledDialogPanel(
            modifier = modifier
                .displayCutoutPadding()
                .systemBarsPadding()
                .widthIn(min = 280.dp, max = 560.dp)
                .padding(MaterialTheme.spacing.mediumOne)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            enter = scaleEnterTransition(),
            exit = fadeExitTransition(200)
        ) {
            DialogContent(
                event = event,
                onAction = { action -> eventDialogState.runAction(action) },
            )
        }
    }
}

@Composable
private fun DialogContent(
    event: DialogEvent,
    onAction: (DialogAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(
                start = MaterialTheme.spacing.mediumTwo,
                top = MaterialTheme.spacing.mediumTwo,
                end = MaterialTheme.spacing.mediumTwo
            )
        ) {
            Text(
                text = event.title.asString(),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))
            Text(
                text = event.message.asString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.smallTwo)
                .padding(end = MaterialTheme.spacing.smallOne)
                .align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallOne)
        ) {
            event.negativeAction?.let { action ->
                OutlinedButton(
                    contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.smallThree),
                    onClick = { onAction(action) }
                ) {
                    Text(
                        text = action.buttonText.asString(),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            event.positiveAction.let { action ->
                Button(
                    contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.smallThree),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                    ),
                    onClick = { onAction(action) },
                ) {
                    Text(
                        text = action.buttonText.asString(),
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun EventAlertDialogPreview() {
    PiFireTheme {
        Surface {
            DialogContent(
                event = DialogEvent(
                    title = UiText(R.string.dialog_auth_with_unsecure_protocol_title),
                    message = UiText(R.string.dialog_auth_with_unsecure_protocol_message),
                    positiveAction = DialogAction(
                        buttonText = UiText(R.string.continue_text),
                        action = {}
                    ),
                    negativeAction = DialogAction(
                        buttonText = UiText(R.string.cancel),
                        action = {}
                    )
                ),
                onAction = {},
            )
        }
    }
}