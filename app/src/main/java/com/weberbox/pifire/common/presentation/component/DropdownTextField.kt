package com.weberbox.pifire.common.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.weberbox.pifire.common.presentation.base.outlinedTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownTextField(
    modifier: Modifier = Modifier,
    selectedValue: String,
    options: List<String>,
    label: String? = null,
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    leadingIcon: @Composable (() -> Unit)? = null,
    onValueChangedEvent: (String) -> Unit,
) {
    var value by remember(selectedValue) { mutableStateOf(selectedValue) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            singleLine = true,
            value = value,
            shape = shape,
            onValueChange = {},
            label = { if (label != null) Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            leadingIcon = leadingIcon,
            colors = outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        value = option
                        onValueChangedEvent(option)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownTextField(
    modifier: Modifier = Modifier,
    selectedValue: T,
    options: List<T>,
    itemToString: (T) -> String,
    label: String? = null,
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    leadingIcon: @Composable (() -> Unit)? = null,
    onValueChangedEvent: (T) -> Unit,
) {
    var value by remember { mutableStateOf(selectedValue) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            singleLine = true,
            value = itemToString(value),
            shape = shape,
            onValueChange = {},
            label = { if (label != null) Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            leadingIcon = leadingIcon,
            colors = outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = itemToString(option)) },
                    onClick = {
                        expanded = false
                        value = option
                        onValueChangedEvent(option)
                    }
                )
            }
        }
    }
}