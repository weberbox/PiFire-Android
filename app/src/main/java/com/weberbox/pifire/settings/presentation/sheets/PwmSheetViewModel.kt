package com.weberbox.pifire.settings.presentation.sheets

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.uiTextArgsOf
import com.weberbox.pifire.settings.presentation.model.PwmControl
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import java.text.DecimalFormat

data class PwmSheetState(
    val index: Int = 0,
    val minTemp: Int? = null,
    val maxTemp: Int? = null,
    val setTemp: Int = 0,
    val note: UiText = UiText(""),
    val deleteVisible: Boolean = false,
    val tempInput: FieldInput = FieldInput(),
    val tempError: ErrorStatus = ErrorStatus(isError = false),
    val dutyInput: FieldInput = FieldInput(),
    val dutyError: ErrorStatus = ErrorStatus(isError = false),
)

class PwmSheetViewModel : ViewModel() {
    private val _pwmSheetState: MutableState<PwmSheetState> = mutableStateOf(PwmSheetState())
    val pwmSheetState: State<PwmSheetState> = _pwmSheetState

    private fun setState(reducer: PwmSheetState.() -> PwmSheetState) {
        val newState = pwmSheetState.value.reducer()
        _pwmSheetState.value = newState
    }

    private var _server by mutableStateOf(Server())

    fun setInitialState(
        server: Server,
        index: Int,
        controlItems: List<PwmControl>
    ) {
        _server = server

        val controlItem = if (index != -1) controlItems[index] else null

        val minTemp =
            when (index) {
                -1 -> null
                0 -> 0
                else -> controlItems[index - 1].temp + 1
            }

        val maxTemp =
            when (index) {
                -1 -> getMaxProbeTemp()
                controlItems.lastIndex -> null
                controlItems.size - 2 -> 100
                else -> controlItems[index + 1].temp - 1
            }

        val setTemp =
            when (index) {
                -1 -> controlItems[controlItems.lastIndex].temp
                controlItems.lastIndex -> controlItems[index].temp - 1
                else -> controlItems[index].temp
            }

        val note =
            if (minTemp == null) {
                val temp = (setTemp - 1).toString()
                UiText(
                    R.string.settings_pwm_temp_range_note_max,
                    uiTextArgsOf(temp, server.settings.tempUnits)
                )
            } else if (maxTemp == null) {
                UiText(R.string.settings_pwm_temp_range_note_last)
            } else {
                UiText(
                    R.string.settings_pwm_temp_range_note_range,
                    uiTextArgsOf(
                        minTemp.toString(),
                        maxTemp.toString(),
                        server.settings.tempUnits
                    )
                )
            }

        val temp by mutableStateOf(
            controlItem?.temp?.toString()
                ?: controlItems[controlItems.lastIndex].temp.toString()
        )

        val dutyCycle by mutableStateOf(
            controlItem?.dutyCycle?.toString()
                ?: controlItems[controlItems.lastIndex].dutyCycle.toString()
        )

        setState {
            copy(
                index = index,
                minTemp = minTemp,
                maxTemp = maxTemp,
                setTemp = setTemp,
                note = note,
                deleteVisible = index >= controlItems.lastIndex,
                tempInput = FieldInput(
                    value = temp,
                    hasInteracted = temp.isNotBlank()
                ),
                tempError = ErrorStatus(isError = false),
                dutyInput = FieldInput(
                    value = dutyCycle,
                    hasInteracted = dutyCycle.isNotBlank()
                ),
                dutyError = ErrorStatus(isError = false)
            )
        }
    }

    fun validateTemp(temp: String) {
        if (temp.isBlank()) {
            setState {
                copy(
                    tempInput = FieldInput(
                        value = temp,
                        hasInteracted = true
                    ),
                    tempError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.error_text_blank)
                    )
                )
            }
            return
        }
        try {
            val number = temp.toInt()
            if (number < (_pwmSheetState.value.minTemp ?: _pwmSheetState.value.setTemp)) {
                setState {
                    copy(
                        tempInput = FieldInput(
                            value = temp,
                            hasInteracted = true
                        ),
                        tempError = ErrorStatus(
                            isError = true,
                            errorMsg = UiText(
                                R.string.settings_min_error,
                                uiTextArgsOf(DecimalFormat("0.##").format(minTemp ?: setTemp))
                            )
                        )
                    )
                }
                return
            } else if (number > (_pwmSheetState.value.maxTemp ?: getMaxProbeTemp())) {
                setState {
                    copy(
                        tempInput = FieldInput(
                            value = temp,
                            hasInteracted = true
                        ),
                        tempError = ErrorStatus(
                            isError = true,
                            errorMsg = UiText(
                                R.string.settings_max_error,
                                uiTextArgsOf(
                                    DecimalFormat("0.##").format(maxTemp ?: getMaxProbeTemp())
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
                    tempInput = FieldInput(
                        value = temp,
                        hasInteracted = true
                    ),
                    tempError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.settings_number_error)
                    )
                )
            }
            return
        }
        setState {
            copy(
                tempInput = FieldInput(
                    value = temp,
                    hasInteracted = true
                ),
                tempError = ErrorStatus(
                    isError = false,
                    errorMsg = null
                )
            )
        }
    }

    fun validateDuty(dutyCycle: String) {
        if (dutyCycle.isBlank()) {
            setState {
                copy(
                    dutyInput = FieldInput(
                        value = dutyCycle,
                        hasInteracted = true
                    ),
                    dutyError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.error_text_blank)
                    )
                )
            }
            return
        }
        try {
            val input = dutyCycle.toInt()
            val minDutyCycle = _server.settings.pwmMinDutyCycle
            if (input < minDutyCycle) {
                setState {
                    copy(
                        dutyInput = FieldInput(
                            value = dutyCycle,
                            hasInteracted = true
                        ),
                        dutyError = ErrorStatus(
                            isError = true,
                            errorMsg = UiText(
                                R.string.settings_min_error,
                                uiTextArgsOf(DecimalFormat("0.##").format(minDutyCycle))
                            )
                        )
                    )
                }
                return
            } else if (input > 100) {
                setState {
                    copy(
                        dutyInput = FieldInput(
                            value = dutyCycle,
                            hasInteracted = true
                        ),
                        dutyError = ErrorStatus(
                            isError = true,
                            errorMsg = UiText(
                                R.string.settings_max_error,
                                uiTextArgsOf(DecimalFormat("0.##").format(100))
                            )
                        )
                    )
                }
                return
            }
        } catch (_: NumberFormatException) {
            setState {
                copy(
                    dutyInput = FieldInput(
                        value = dutyCycle,
                        hasInteracted = true
                    ),
                    dutyError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.settings_number_error)
                    )
                )
            }
            return
        }
        setState {
            copy(
                dutyInput = FieldInput(
                    value = dutyCycle,
                    hasInteracted = true
                ),
                dutyError = ErrorStatus(
                    isError = false,
                    errorMsg = null
                )
            )
        }
    }

    private fun getMaxProbeTemp(): Int {
        if (_server.settings.tempUnits.equals("F", ignoreCase = true)) {
            return _server.settings.dashMaxPrimaryTempF
        }
        return _server.settings.dashMaxPrimaryTempC
    }
}