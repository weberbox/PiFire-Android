package com.weberbox.pifire.settings.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.model.PostDto.ProbesDto
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.ThermometerProbe
import com.weberbox.pifire.common.presentation.component.DropdownTextField
import com.weberbox.pifire.common.presentation.component.OutlineFieldWithState
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.state.ErrorStatusSaver
import com.weberbox.pifire.common.presentation.state.FieldInputSaver
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.dashboard.presentation.model.ProbeType
import com.weberbox.pifire.settings.presentation.contract.ProbeContract.Event.UpdateProbe
import com.weberbox.pifire.settings.presentation.model.ProbeMap.ProbeInfo
import com.weberbox.pifire.settings.presentation.model.ProbeProfile

@Composable
internal fun ProbeSheet(
    probeInfo: ProbeInfo,
    profiles: List<ProbeProfile>,
    onEvent: (UpdateProbe) -> Unit,
    onDismiss: () -> Unit
) {
    val probeLabel by remember { mutableStateOf(probeInfo.label) }
    var selectedProfile by remember { mutableStateOf(probeInfo.profile) }
    var enabled by remember { mutableStateOf(probeInfo.enabled) }
    var probeName by rememberSaveable(saver = FieldInputSaver) {
        mutableStateOf(
            FieldInput(
                value = probeInfo.name,
                hasInteracted = probeInfo.name.isNotBlank()
            )
        )
    }
    var probeNameError by rememberSaveable(saver = ErrorStatusSaver) {
        mutableStateOf(
            ErrorStatus(isError = false)
        )
    }

    Column(
        modifier = Modifier.padding(
            horizontal = MaterialTheme.spacing.mediumOne,
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.dialog_probe_edit),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)
        )
        OutlineFieldWithState(
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.settings_probe_name),
            fieldInput = probeName,
            errorStatus = probeNameError,
            leadingIcon = Icons.Filled.Edit,
            onValueChange = {
                probeNameError = ErrorStatus(isError = it.isBlank())
                probeName = probeName.copy(value = it)
            }
        )
        if (probeInfo.port.contains("ADC", ignoreCase = true)) {
            DropdownTextField(
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.smallThree)
                    .fillMaxWidth(),
                selectedValue = selectedProfile,
                options = profiles,
                itemToString = { it.name },
                leadingIcon = {
                    Icon(
                        imageVector = Icon.Filled.ThermometerProbe,
                        contentDescription = null
                    )
                },
                onValueChangedEvent = { selectedProfile = it }
            )
        }
        if (probeInfo.type != ProbeType.Primary.name) {
            DropdownTextField(
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.smallThree)
                    .fillMaxWidth(),
                selectedValue = enabled,
                options = listOf(true, false),
                itemToString = { if (it) "Enabled" else "Disabled" },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Sensors,
                        contentDescription = null
                    )
                },
                onValueChangedEvent = { enabled = it }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MaterialTheme.spacing.smallThree),
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
                onClick = {
                    onEvent(
                        UpdateProbe(
                            ProbesDto(
                                name = probeName.value,
                                label = probeLabel,
                                profileId = selectedProfile.id,
                                enabled = enabled
                            )
                        )
                    )
                },
                enabled = probeName.hasInteracted && !probeNameError.isError,
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
private fun ProbeSheetPreview() {
    val probeInfo = ProbeInfo(
        name = "Test",
        port = "ADC",
        profile = ProbeProfile(name = "Test Profile")
    )
    val profile = ProbeProfile(name = "Test Profile")
    PiFireTheme {
        Surface {
            ProbeSheet(
                probeInfo = probeInfo,
                profiles = listOf(profile),
                onEvent = {},
                onDismiss = {}
            )
        }
    }
}