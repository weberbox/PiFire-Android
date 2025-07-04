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
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import com.weberbox.pifire.common.icons.outlined.Target
import com.weberbox.pifire.common.presentation.component.DropdownTextField
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.asString
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.model.SmartStart

internal data class SmartStartEvent(
    val index: Int,
    val temp: Int,
    val startUp: Int,
    val augerOn: Int,
    val pMode: Int,
    val smartStartItems: List<SmartStart>
)

@Composable
internal fun SmartStartSheet(
    title: String,
    index: Int = -1,
    server: Server,
    smartStartItems: List<SmartStart>,
    onDelete: (List<SmartStart>) -> Unit,
    onUpdate: (SmartStartEvent) -> Unit,
    viewModel: SmartStartSheetViewModel = viewModel(key = index.toString())
) {
    LaunchedEffect(Unit) {
        viewModel.setInitialState(
            server = server,
            index = index,
            smartStartItems = smartStartItems
        )
    }

    SmartStartSheetContent(
        state = viewModel.smartStartSheetState.value,
        title = title,
        smartStartItems = smartStartItems,
        onDelete = onDelete,
        onUpdate = onUpdate,
        onTempUpdated = { viewModel.validateTemp(it) },
        onStartupUpdated = { viewModel.validateStartUp(it) },
        onAugerOnUpdated = { viewModel.validateAugerOn(it) },
        onPModeUpdated = { viewModel.setPmode(it) },
    )
}

@Composable
private fun SmartStartSheetContent(
    state: SmartStartSheetState,
    title: String,
    smartStartItems: List<SmartStart>,
    onDelete: (List<SmartStart>) -> Unit,
    onUpdate: (SmartStartEvent) -> Unit,
    onTempUpdated: (String) -> Unit,
    onStartupUpdated: (String) -> Unit,
    onAugerOnUpdated: (String) -> Unit,
    onPModeUpdated: (String) -> Unit
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
            placeholder = stringResource(R.string.settings_smart_start_temp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            enabled = state.index != smartStartItems.lastIndex,
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
            placeholder = stringResource(R.string.settings_smart_start_startup),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            fieldInput = state.startUpInput,
            errorStatus = state.startUpError,
            leadingIcon = Icons.Outlined.Schedule,
            onValueChange = {
                onStartupUpdated(it)
            }
        )
        OutlineFieldWithState(
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.settings_smart_start_auger_on),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            fieldInput = state.augerOnInput,
            errorStatus = state.augerOnError,
            leadingIcon = Icons.Filled.DoubleArrow,
            onValueChange = {
                onAugerOnUpdated(it)
            }
        )
        DropdownTextField(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallThree),
            selectedValue = state.pMode,
            options = (0..9).map { it.toString() },
            leadingIcon = {
                Icon(
                    imageVector = Icon.Outlined.Target,
                    contentDescription = null
                )
            },
            onValueChangedEvent = {
                onPModeUpdated(it)
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
                    onClick = {
                        onDelete(smartStartItems)
                    },
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
                        SmartStartEvent(
                            index = state.index,
                            temp = state.tempInput.value.toInt(),
                            startUp = state.startUpInput.value.toInt(),
                            augerOn = state.augerOnInput.value.toInt(),
                            pMode = state.pMode.toInt(),
                            smartStartItems = smartStartItems
                        )
                    )
                },
                enabled = state.tempInput.hasInteracted && state.startUpInput.hasInteracted &&
                        state.augerOnInput.hasInteracted && !state.tempError.isError &&
                        !state.startUpError.isError && !state.augerOnError.isError,
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun SmartStartSheetPreview() {
    PiFireTheme {
        Surface {
            SmartStartSheetContent(
                state = SmartStartSheetState(
                    deleteVisible = true
                ),
                title = stringResource(R.string.settings_smart_start_editor_title),
                smartStartItems = listOf(
                    SmartStart(
                        temp = 60,
                        startUp = 360,
                        augerOn = 15,
                        pMode = 0
                    ),
                    SmartStart(
                        temp = 80,
                        startUp = 360,
                        augerOn = 15,
                        pMode = 1
                    ),
                    SmartStart(
                        temp = 90,
                        startUp = 240,
                        augerOn = 15,
                        pMode = 3
                    ),
                    SmartStart(
                        temp = 91,
                        startUp = 240,
                        augerOn = 15,
                        pMode = 5
                    ),
                ),
                onDelete = {},
                onUpdate = {},
                onTempUpdated = {},
                onStartupUpdated = {},
                onPModeUpdated = {},
                onAugerOnUpdated = {}
            )
        }
    }
}
