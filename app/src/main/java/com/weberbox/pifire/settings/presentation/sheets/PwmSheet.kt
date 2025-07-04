package com.weberbox.pifire.settings.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.Fan
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.asString
import com.weberbox.pifire.settings.presentation.model.PwmControl
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server

internal data class PwmEvent(
    val index: Int,
    val temp: Int,
    val dutyCycle: Int,
    val controlItems: List<PwmControl>
)

@Composable
internal fun PwmSheet(
    title: String,
    index: Int,
    server: Server,
    controlItems: List<PwmControl>,
    onDelete: (List<PwmControl>) -> Unit,
    onUpdate: (PwmEvent) -> Unit,
    viewModel: PwmSheetViewModel = viewModel(key = index.toString())
) {
    LaunchedEffect(Unit) {
        viewModel.setInitialState(
            server = server,
            index = index,
            controlItems = controlItems
        )
    }

    PwmSheetContent(
        state = viewModel.pwmSheetState.value,
        title = title,
        controlItems = controlItems,
        onDelete = onDelete,
        onUpdate = onUpdate,
        onTempUpdated = { viewModel.validateTemp(it) },
        onDutyUpdated = { viewModel.validateDuty(it) }
    )
}

@Composable
private fun PwmSheetContent(
    state: PwmSheetState,
    title: String,
    controlItems: List<PwmControl>,
    onDelete: (List<PwmControl>) -> Unit,
    onUpdate: (PwmEvent) -> Unit,
    onTempUpdated: (String) -> Unit,
    onDutyUpdated: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.mediumOne)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = state.note.asString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.smallOne,
                bottom = MaterialTheme.spacing.smallOne
            )
        )
        OutlineFieldWithState(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraExtraSmall),
            placeholder = stringResource(R.string.settings_pwm_control_temp),
            enabled = state.index != controlItems.lastIndex,
            fieldInput = state.tempInput,
            errorStatus = state.tempError,
            leadingIcon = Icons.Filled.Thermostat,
            onValueChange = {
                onTempUpdated(it)
            }
        )
        OutlineFieldWithState(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.extraExtraSmall),
            placeholder = stringResource(R.string.settings_pwm_control_duty),
            fieldInput = state.dutyInput,
            errorStatus = state.dutyError,
            leadingIcon = Icon.Filled.Fan,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            onValueChange = {
                onDutyUpdated(it)
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.smallThree),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (state.deleteVisible) {
                OutlinedButton(
                    onClick = { onDelete(controlItems) },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.smallOne)
                        .weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                onClick = {
                    onUpdate(
                        PwmEvent(
                            index = state.index,
                            temp = state.tempInput.value.toInt(),
                            dutyCycle = state.dutyInput.value.toInt(),
                            controlItems = controlItems
                        )
                    )
                },
                enabled = state.tempInput.hasInteracted && state.dutyInput.hasInteracted &&
                        !state.dutyError.isError && !state.tempError.isError,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.save),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PwmSheetPreview() {
    PiFireTheme {
        Surface {
            PwmSheetContent(
                state = PwmSheetState(),
                title = stringResource(R.string.settings_pwm_editor_title),
                controlItems = listOf(
                    PwmControl(
                        temp = 3,
                        dutyCycle = 20
                    ),
                    PwmControl(
                        temp = 7,
                        dutyCycle = 35
                    ),
                    PwmControl(
                        temp = 10,
                        dutyCycle = 50
                    ),
                    PwmControl(
                        temp = 15,
                        dutyCycle = 75
                    ),
                    PwmControl(
                        temp = 16,
                        dutyCycle = 100
                    )
                ),
                onDelete = {},
                onUpdate = {},
                onTempUpdated = {},
                onDutyUpdated = {}
            )
        }
    }
}