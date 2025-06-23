package com.weberbox.pifire.common.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.outlinedTextFieldColors
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.util.asString

@Composable
fun OutlineFieldWithState(
    modifier: Modifier = Modifier,
    placeholder: String,
    fieldInput: FieldInput,
    errorStatus: ErrorStatus,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPasswordField: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    colors: TextFieldColors = outlinedTextFieldColors(),
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    leadingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var textFieldValueState by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(fieldInput) {
        if (textFieldValueState.text.isEmpty()) {
            textFieldValueState = textFieldValueState.copy(
                text = fieldInput.value, selection = when {
                    fieldInput.value.isEmpty() -> TextRange.Zero
                    else -> TextRange(
                        start = fieldInput.value.length,
                        end = fieldInput.value.length
                    )
                }
            )
        }
    }

    OutlinedTextField(
        modifier = modifier,
        value = textFieldValueState,
        enabled = enabled,
        onValueChange = {
            textFieldValueState = it
            onValueChange(it.text)
        },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = colors,
        shape = shape,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon?.let {
            {
                Icon(imageVector = it, contentDescription = null)
            }
        },
        isError = fieldInput.hasInteracted && errorStatus.isError,
        supportingText = {
            if (fieldInput.hasInteracted && errorStatus.isError) {
                errorStatus.errorMsg?.let {
                    Text(
                        text = it.asString(),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else Text(text = "")
        },
        trailingIcon = {
            if (isPasswordField) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Outlined.Visibility else
                            Icons.Outlined.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            stringResource(R.string.hide_password) else
                            stringResource(R.string.show_password)
                    )
                }
            } else if (fieldInput.hasInteracted && errorStatus.isError) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        visualTransformation = if (isPasswordField && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None
    )
}