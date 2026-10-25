package com.weberbox.pifire.core.ui.components.sheets

import androidx.compose.ui.text.input.KeyboardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidationViewModelTest {

    private fun isError(input: String, options: ValidationOptions): Boolean {
        val viewModel = InputValidationViewModel()
        viewModel.setInitialState(input = "", validationOptions = options)
        viewModel.validateInput(input)
        return viewModel.validationState.value.error.isError
    }

    private val integer = ValidationOptions(keyboardType = KeyboardType.NumberPassword)
    private val decimal = ValidationOptions(keyboardType = KeyboardType.Decimal)

    @Test
    fun `integer fields reject decimals and text`() {
        assertFalse(isError("60", integer))
        assertTrue(isError("60.5", integer))
        assertTrue(isError("abc", integer))
    }

    @Test
    fun `decimal fields accept either separator and pass a dot to callers`() {
        assertFalse(isError("0.5", decimal))
        assertFalse(isError("0,5", decimal))
        assertEquals("0.5", decimal.normalize(" 0,5 "))
    }

    @Test
    fun `zero is a real bound and null means no limit`() {
        val nonNegative = decimal.copy(min = 0.0)
        assertTrue(isError("-1", nonNegative))
        assertFalse(isError("0", nonNegative))
        assertFalse(isError("-1000", decimal))
    }

    @Test
    fun `blank input is an error unless allowed`() {
        assertTrue(isError("", ValidationOptions()))
        assertFalse(isError("", ValidationOptions(allowBlank = true)))
    }
}
