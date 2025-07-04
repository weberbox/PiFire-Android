package com.weberbox.pifire.settings.presentation.sheets

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.model.ErrorStatus
import com.weberbox.pifire.common.presentation.model.FieldInput
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.uiTextArgsOf
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.model.SmartStart
import java.text.DecimalFormat

data class SmartStartSheetState(
    val index: Int = 0,
    val minTemp: Int? = null,
    val maxTemp: Int? = null,
    val setTemp: Int = 0,
    val note: UiText = UiText(""),
    val pMode: String = "2",
    val deleteVisible: Boolean = false,
    val tempInput: FieldInput = FieldInput(),
    val tempError: ErrorStatus = ErrorStatus(isError = false),
    val startUpInput: FieldInput = FieldInput(),
    val startUpError: ErrorStatus = ErrorStatus(isError = false),
    val augerOnInput: FieldInput = FieldInput(),
    val augerOnError: ErrorStatus = ErrorStatus(isError = false),
)

class SmartStartSheetViewModel : ViewModel() {
    private val _smartStartSheetState = mutableStateOf(SmartStartSheetState())
    val smartStartSheetState = _smartStartSheetState

    private fun setState(reducer: SmartStartSheetState.() -> SmartStartSheetState) {
        val newState = smartStartSheetState.value.reducer()
        _smartStartSheetState.value = newState
    }

    private var _server by mutableStateOf(Server())

    fun setInitialState(
        server: Server,
        index: Int,
        smartStartItems: List<SmartStart>
    ) {
        _server = server

        val smartStartItem = if (index != -1) smartStartItems[index] else null

        val minTemp =
            when (index) {
                -1 -> null
                0 -> 0
                else -> smartStartItems[index - 1].temp + 1
            }

        val maxTemp =
            when (index) {
                -1 -> getMaxProbeTemp()
                smartStartItems.lastIndex -> null
                smartStartItems.size - 2 -> 100
                else -> smartStartItems[index + 1].temp - 1
            }

        val setTemp =
            when (index) {
                -1 -> smartStartItems[smartStartItems.lastIndex].temp
                smartStartItems.lastIndex -> smartStartItems[index].temp - 1
                else -> smartStartItems[index].temp
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
            smartStartItem?.temp?.toString()
                ?: smartStartItems[smartStartItems.lastIndex].temp.toString()
        )

        val startUp by mutableStateOf(
            smartStartItem?.startUp?.toString()
                ?: smartStartItems[smartStartItems.lastIndex].startUp.toString()
        )

        val augerOn by mutableStateOf(
            smartStartItem?.augerOn?.toString()
                ?: smartStartItems[smartStartItems.lastIndex].augerOn.toString()
        )

        val pMode by mutableStateOf(
            smartStartItem?.pMode?.toString()
                ?: smartStartItems[smartStartItems.lastIndex].pMode.toString()
        )

        setState {
            copy(
                index = index,
                minTemp = minTemp,
                maxTemp = maxTemp,
                setTemp = setTemp,
                note = note,
                pMode = pMode,
                deleteVisible = index >= smartStartItems.lastIndex,
                tempInput = FieldInput(
                    value = temp,
                    hasInteracted = temp.isNotBlank()
                ),
                tempError = ErrorStatus(isError = false),
                startUpInput = FieldInput(
                    value = startUp,
                    hasInteracted = startUp.isNotBlank()
                ),
                startUpError = ErrorStatus(isError = false),
                augerOnInput = FieldInput(
                    value = augerOn,
                    hasInteracted = augerOn.isNotBlank()
                ),
                augerOnError = ErrorStatus(isError = false)
            )
        }

    }

    fun setPmode(pMode: String) {
        setState {
            copy(
                pMode = pMode
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
            if (number < (_smartStartSheetState.value.minTemp
                    ?: _smartStartSheetState.value.setTemp)
            ) {
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
            } else if (number > (_smartStartSheetState.value.maxTemp ?: 600)) {
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

    fun validateStartUp(startUp: String) {
        if (startUp.isBlank()) {
            setState {
                copy(
                    startUpInput = FieldInput(
                        value = startUp,
                        hasInteracted = true
                    ),
                    startUpError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.error_text_blank)
                    )
                )
            }
            return
        }
        try {
            startUp.toInt()
        } catch (_: NumberFormatException) {
            setState {
                copy(
                    startUpInput = FieldInput(
                        value = startUp,
                        hasInteracted = true
                    ),
                    startUpError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.settings_number_error)
                    )
                )
            }
            return
        }
        setState {
            copy(
                startUpInput = FieldInput(
                    value = startUp,
                    hasInteracted = true
                ),
                startUpError = ErrorStatus(
                    isError = false,
                    errorMsg = null
                )
            )
        }
    }

    fun validateAugerOn(augerOn: String) {
        if (augerOn.isBlank()) {
            setState {
                copy(
                    augerOnInput = FieldInput(
                        value = augerOn,
                        hasInteracted = true
                    ),
                    augerOnError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.error_text_blank)
                    )
                )
            }
            return
        }
        try {
            augerOn.toInt()
        } catch (_: NumberFormatException) {
            setState {
                copy(
                    augerOnInput = FieldInput(
                        value = augerOn,
                        hasInteracted = true
                    ),
                    augerOnError = ErrorStatus(
                        isError = true,
                        errorMsg = UiText(R.string.settings_number_error)
                    )
                )
            }
            return
        }
        setState {
            copy(
                augerOnInput = FieldInput(
                    value = augerOn,
                    hasInteracted = true
                ),
                augerOnError = ErrorStatus(
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