package com.weberbox.pifire.landing.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Api
import androidx.compose.material.icons.outlined.Key
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.DropdownTextField
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.state.ErrorStatusSaver
import com.weberbox.pifire.common.presentation.state.FieldInputSaver
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.ExtraHeader

@Composable
fun HeadersSheet(
    extraHeader: ExtraHeader,
    onUpdate: (header: ExtraHeader) -> Unit,
    onDelete: (header: ExtraHeader) -> Unit
) {
    var key by rememberSaveable(saver = FieldInputSaver) {
        mutableStateOf(
            FieldInput(
                value = extraHeader.key,
                hasInteracted = extraHeader.key.isNotBlank()
            )
        )
    }
    var value by rememberSaveable(saver = FieldInputSaver) {
        mutableStateOf(
            FieldInput(
                value = extraHeader.value,
                hasInteracted = extraHeader.value.isNotBlank()
            )
        )
    }
    var keyError by rememberSaveable(saver = ErrorStatusSaver) {
        mutableStateOf(
            ErrorStatus(isError = false)
        )
    }
    var valueError by rememberSaveable(saver = ErrorStatusSaver) {
        mutableStateOf(
            ErrorStatus(isError = false)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.mediumOne),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = stringResource(R.string.settings_extra_headers_editor),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Column(
            modifier = Modifier.padding(top = MaterialTheme.spacing.mediumThree),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropdownTextField(
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.medium)
                    .fillMaxWidth(),
                selectedValue = HeaderType.Custom.title,
                options = HeaderType.entries.map { it.title },
                onValueChangedEvent = {
                    val text = HeaderType.from(it).text
                    key = FieldInput(
                        value = text,
                        hasInteracted = text.isNotBlank()
                    )
                }
            )
            OutlineFieldWithState(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.settings_headers_key),
                leadingIcon = Icons.Outlined.Key,
                fieldInput = key,
                errorStatus = keyError,
                onValueChange = {
                    key = FieldInput(value = it, hasInteracted = true)
                    keyError = ErrorStatus(
                        isError = it.isBlank(),
                        errorMsg = UiText(R.string.error_text_blank)
                    )
                }
            )
            OutlineFieldWithState(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.settings_headers_value),
                leadingIcon = Icons.Outlined.Api,
                fieldInput = value,
                errorStatus = valueError,
                onValueChange = {
                    value = FieldInput(value = it, hasInteracted = true)
                    valueError = ErrorStatus(
                        isError = it.isBlank(),
                        errorMsg = UiText(R.string.error_text_blank)
                    )
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.smallThree),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (extraHeader.key.isNotBlank() || extraHeader.value.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onDelete(extraHeader) },
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
                    enabled = key.hasInteracted && !keyError.isError &&
                            value.hasInteracted && !valueError.isError,
                    onClick = {
                        onUpdate(ExtraHeader(key.value, value.value))
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
}

enum class HeaderType(val title: String, val text: String) {
    Custom("Custom", ""),
    ClientId("Cloudflare: Client ID", "CF-Access-Client-Id"),
    Secret("Cloudflare: Secret", "CF-Access-Client-Secret");

    companion object {
        infix fun from(title: String): HeaderType = entries.firstOrNull {
            it.title == title
        } ?: Custom
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun HeadersSheetPreview() {
    val extraHeader = ExtraHeader("Test", "Test")
    PiFireTheme {
        Surface {
            HeadersSheet(
                extraHeader = extraHeader,
                onUpdate = {},
                onDelete = {}
            )
        }
    }
}