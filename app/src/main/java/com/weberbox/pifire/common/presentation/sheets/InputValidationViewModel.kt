package com.weberbox.pifire.common.presentation.sheets

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.uiTextArgsOf
import java.text.DecimalFormat

/**
 * [KeyboardType.Number] and [KeyboardType.NumberPassword] accept whole numbers only and
 * [KeyboardType.Decimal] accepts either decimal separator, so callers can parse the value passed
 * to `onUpdate` with `toIntOrNull()` / `toDoubleOrNull()`. A null [min] or [max] means no limit.
 */
data class ValidationOptions(
    val keyboardType: KeyboardType = KeyboardType.Text,
    val allowBlank: Boolean = false,
    val min: Double? = null,
    val max: Double? = null,
) {
    val isInteger: Boolean
        get() = keyboardType == KeyboardType.Number || keyboardType == KeyboardType.NumberPassword

    val isNumeric: Boolean
        get() = isInteger || keyboardType == KeyboardType.Decimal

    /** The input as callers receive it: numbers trimmed and with a `.` decimal separator. */
    fun normalize(input: String): String {
        return if (isNumeric) input.trim().replace(',', '.') else input
    }
}

data class ValidationState(
    val input: FieldInput = FieldInput(),
    val error: ErrorStatus = ErrorStatus(isError = false),
    val validationOptions: ValidationOptions = ValidationOptions(),
)

class InputValidationViewModel : ViewModel() {
    private val _validationState = mutableStateOf(ValidationState())
    val validationState: State<ValidationState> = _validationState

    fun setInitialState(
        input: String,
        validationOptions: ValidationOptions = ValidationOptions(),
    ) {
        _validationState.value =
            ValidationState(
                input = FieldInput(
                    value = input,
                    hasInteracted = input.isNotBlank()
                ),
                error = ErrorStatus(
                    isError = false
                ),
                validationOptions = validationOptions
            )
    }

    fun validateInput(input: String) {
        val options = validationState.value.validationOptions
        val errorMsg = findError(input = input, options = options)
        _validationState.value = validationState.value.copy(
            input = FieldInput(
                value = input,
                hasInteracted = true
            ),
            error = ErrorStatus(
                isError = errorMsg != null,
                errorMsg = errorMsg
            )
        )
    }

    private fun findError(input: String, options: ValidationOptions): UiText? {
        if (input.isBlank()) {
            return if (options.allowBlank) null else UiText(R.string.error_text_blank)
        }
        if (!options.isNumeric) return null

        val normalized = options.normalize(input)
        val number = if (options.isInteger) {
            normalized.toIntOrNull()?.toDouble()
                ?: return UiText(R.string.settings_whole_number_error)
        } else {
            normalized.toDoubleOrNull()?.takeIf { it.isFinite() }
                ?: return UiText(R.string.settings_number_error)
        }

        return when {
            options.min != null && number < options.min -> UiText(
                R.string.settings_min_error,
                uiTextArgsOf(DecimalFormat("0.##").format(options.min))
            )

            options.max != null && number > options.max -> UiText(
                R.string.settings_max_error,
                uiTextArgsOf(DecimalFormat("0.##").format(options.max))
            )

            else -> null
        }
    }
}
