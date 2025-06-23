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

data class ValidationOptions(
    val keyboardType: KeyboardType = KeyboardType.Text,
    val allowBlank: Boolean = false,
    val min: Double = 0.0,
    val max: Double = 0.0
)

data class ValidationState(
    val input: FieldInput = FieldInput(),
    val error: ErrorStatus = ErrorStatus(isError = false),
    val validationOptions: ValidationOptions = ValidationOptions()
)

class InputValidationViewModel : ViewModel() {
    private val _validationState = mutableStateOf(ValidationState())
    val validationState: State<ValidationState> = _validationState

    private fun setState(reducer: ValidationState.() -> ValidationState) {
        val newState = validationState.value.reducer()
        _validationState.value = newState
    }

    fun setInitialState(
        input: String,
        validationOptions: ValidationOptions = ValidationOptions()
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
        if (!validationState.value.validationOptions.allowBlank && input.isBlank()) {
            setState {
                copy(
                    input = FieldInput(
                        value = input,
                        hasInteracted = true
                    ),
                    error = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.text_blank_error)
                    )
                )
            }
            return
        }
        if (validationState.value.validationOptions.keyboardType == KeyboardType.Number ||
            validationState.value.validationOptions.keyboardType == KeyboardType.Decimal ||
            validationState.value.validationOptions.keyboardType == KeyboardType.NumberPassword
        ) {
            try {
                val number = input.toDouble()
                if (validationState.value.validationOptions.min != 0.0 && number <
                    validationState.value.validationOptions.min
                ) {
                    setState {
                        copy(
                            input = FieldInput(
                                value = input,
                                hasInteracted = true
                            ),
                            error = ErrorStatus(
                                isError = true,
                                errorMsg = UiText(
                                    R.string.settings_min_error,
                                    uiTextArgsOf(
                                        DecimalFormat("0.##").format(
                                            validationState.value.validationOptions.min
                                        )
                                    )
                                )
                            )
                        )
                    }
                    return
                } else if (validationState.value.validationOptions.max != 0.0 && number >
                    validationState.value.validationOptions.max
                ) {
                    setState {
                        copy(
                            input = FieldInput(
                                value = input,
                                hasInteracted = true
                            ),
                            error = ErrorStatus(
                                isError = true,
                                errorMsg = UiText(
                                    R.string.settings_max_error,
                                    uiTextArgsOf(
                                        DecimalFormat("0.##").format(
                                            validationState.value.validationOptions.max
                                        )
                                    )
                                )
                            )
                        )
                    }
                    return
                }
            } catch (_: NumberFormatException) {
                setState {
                    copy(
                        input = FieldInput(
                            value = input,
                            hasInteracted = true
                        ),
                        error = ErrorStatus(
                            isError = true,
                            errorMsg = UiText(R.string.settings_number_error)
                        )
                    )
                }
                return
            }
        }
        setState {
            copy(
                input = FieldInput(
                    value = input,
                    hasInteracted = true
                ),
                error = ErrorStatus(
                    isError = false,
                    errorMsg = null
                )
            )
        }
    }
}