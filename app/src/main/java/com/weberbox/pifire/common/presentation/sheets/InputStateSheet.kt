package com.weberbox.pifire.common.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.InputState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import kotlinx.coroutines.delay

@Composable
fun InputStateSheet(
    inputState: InputState,
    title: String,
    placeholder: String = "",
    leadingIcon: ImageVector = Icons.Outlined.Dns,
    onValueChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.mediumOne,
            end = MaterialTheme.spacing.mediumOne,
            bottom = MaterialTheme.spacing.smallThree
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)
        )
        OutlineFieldWithState(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.smallOne)
                .focusRequester(focusRequester),
            placeholder = placeholder,
            fieldInput = inputState.input,
            errorStatus = inputState.error,
            leadingIcon = leadingIcon,
            onValueChange = { onValueChange(it) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { onDismiss() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                enabled = inputState.input.hasInteracted && !inputState.error.isError,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
                onClick = { onConfirm(inputState.input.value) }
            ) {
                Text(
                    text = stringResource(R.string.save),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun AddressSheetPreview() {
    PiFireTheme {
        Surface(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            InputStateSheet(
                inputState = InputState(),
                title = stringResource(R.string.settings_server_address),
                placeholder = stringResource(R.string.settings_server_address),
                onConfirm = {},
                onDismiss = {},
                onValueChange = {}
            )
        }
    }
}