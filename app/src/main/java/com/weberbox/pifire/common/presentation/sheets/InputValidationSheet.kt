package com.weberbox.pifire.common.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import kotlinx.coroutines.delay

@Composable
fun InputValidationSheet(
    input: String,
    title: String,
    placeholder: String = "",
    leadingIcon: ImageVector = Icons.Outlined.Edit,
    contentType: ContentType? = null,
    validationOptions: ValidationOptions = ValidationOptions(),
    onUpdate: (String) -> Unit,
    onDelete: ((String) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    viewModel: InputValidationViewModel = viewModel(key = title)
) {
    LaunchedEffect(Unit) {
        viewModel.setInitialState(
            input = input,
            validationOptions = validationOptions
        )
    }

    InputValidationContent(
        state = viewModel.validationState.value,
        input = input,
        title = title,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        contentType = contentType,
        onUpdate = onUpdate,
        onDelete = onDelete,
        onDismiss = onDismiss,
        onValueChange = { viewModel.validateInput(it) }
    )
}

@Composable
private fun InputValidationContent(
    state: ValidationState,
    input: String,
    title: String,
    placeholder: String = "",
    leadingIcon: ImageVector,
    contentType: ContentType? = null,
    onUpdate: (String) -> Unit,
    onDelete: ((String) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
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
                .focusRequester(focusRequester)
                .then(
                    contentType?.let { type ->
                        Modifier.semantics { this.contentType = type }
                    } ?: Modifier
                ),
            placeholder = placeholder,
            fieldInput = state.input,
            errorStatus = state.error,
            keyboardOptions = KeyboardOptions(
                keyboardType = state.validationOptions.keyboardType
            ),
            leadingIcon = leadingIcon,
            onValueChange = onValueChange
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (onDismiss != null) {
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
            }
            if (onDelete != null && input.isNotBlank()) {
                OutlinedButton(
                    onClick = { onDelete(state.input.value) },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.smallOne)
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                enabled = state.input.hasInteracted && !state.error.isError,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
                onClick = { onUpdate(state.input.value) }
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
private fun InputValidationSheetPreview() {
    PiFireTheme {
        Surface(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            InputValidationContent(
                state = ValidationState(
                    input = FieldInput(),
                    error = ErrorStatus(isError = false),
                    validationOptions = ValidationOptions()
                ),
                input = "Test",
                title = stringResource(R.string.settings_server_address),
                placeholder = stringResource(R.string.settings_server_address),
                leadingIcon = Icons.Outlined.Edit,
                onUpdate = {},
                onDelete = {},
                onValueChange = {}
            )
        }
    }
}