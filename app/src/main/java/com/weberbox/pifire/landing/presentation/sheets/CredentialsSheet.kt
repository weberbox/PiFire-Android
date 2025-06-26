package com.weberbox.pifire.landing.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person2
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.state.ErrorStatusSaver
import com.weberbox.pifire.common.presentation.state.FieldInputSaver
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.BasicAuth

@Composable
internal fun CredentialsSheet(
    basicAuth: BasicAuth,
    onAction: (BasicAuth) -> Unit,
) {
    var username by rememberSaveable(saver = FieldInputSaver) {
        mutableStateOf(
            FieldInput(
                value = basicAuth.user,
                hasInteracted = basicAuth.user.isNotBlank()
            )
        )
    }
    var password by rememberSaveable(saver = FieldInputSaver) {
        mutableStateOf(
            FieldInput(
                value = basicAuth.pass,
                hasInteracted = basicAuth.pass.isNotBlank()
            )
        )
    }
    var usernameError by rememberSaveable(saver = ErrorStatusSaver) {
        mutableStateOf(
            ErrorStatus(isError = false)
        )
    }
    var passwordError by rememberSaveable(saver = ErrorStatusSaver) {
        mutableStateOf(
            ErrorStatus(isError = false)
        )
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
            text = stringResource(R.string.settings_credentials_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)
        )
        OutlineFieldWithState(
            modifier = Modifier
                .semantics { contentType = ContentType.Username }
                .fillMaxWidth(),
            placeholder = stringResource(R.string.username),
            leadingIcon = Icons.Outlined.Person2,
            fieldInput = username,
            errorStatus = usernameError,
            onValueChange = {
                username = FieldInput(value = it, hasInteracted = true)
                usernameError = ErrorStatus(
                    isError = username.value.isBlank(),
                    errorMsg = UiText(R.string.text_blank_error)
                )
            }
        )
        OutlineFieldWithState(
            modifier = Modifier
                .semantics { contentType = ContentType.Password }
                .fillMaxWidth(),
            placeholder = stringResource(R.string.password),
            leadingIcon = Icons.Outlined.Key,
            isPasswordField = true,
            fieldInput = password,
            errorStatus = passwordError,
            onValueChange = {
                password = FieldInput(value = it, hasInteracted = true)
                passwordError = ErrorStatus(
                    isError = password.value.isBlank(),
                    errorMsg = UiText(R.string.text_blank_error)
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (basicAuth.user.isNotBlank() || basicAuth.pass.isNotBlank()) {
                OutlinedButton(
                    onClick = {
                        onAction(BasicAuth())
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.smallOne)
                        .weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                enabled = username.hasInteracted && !usernameError.isError &&
                        password.hasInteracted && !passwordError.isError,
                onClick = {
                    onAction(
                        basicAuth.copy(
                            user = username.value,
                            pass = password.value
                        )
                    )
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
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
private fun CredentialsSheetPreview() {
    val basicAuth = BasicAuth("Test", "Test")
    PiFireTheme {
        Surface {
            CredentialsSheet(
                basicAuth = basicAuth,
                onAction = {}
            )
        }
    }
}